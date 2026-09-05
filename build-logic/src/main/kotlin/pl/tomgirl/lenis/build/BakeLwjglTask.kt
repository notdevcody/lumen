package pl.tomgirl.lenis.build

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import org.objectweb.asm.*
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.SimpleRemapper
import org.objectweb.asm.tree.*
import java.nio.file.Files
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

abstract class BakeLwjglTask : DefaultTask() {
    @get:InputFile @get:Classpath
    abstract val inputJar: RegularFileProperty

    @get:Classpath
    abstract val patchClasses: ConfigurableFileCollection

    @get:OutputFile
    abstract val outputJar: RegularFileProperty

    @TaskAction
    fun bake() {
        val output = outputJar.get().asFile.toPath()
        val patched = mutableSetOf<String>()
        Files.createDirectories(output.parent)
        JarFile(inputJar.get().asFile).use { input ->
            JarOutputStream(Files.newOutputStream(output)).use { jar ->
                input.stream().sorted(compareBy(JarEntry::getName)).forEach { entry ->
                    var bytes = input.getInputStream(entry).use { it.readAllBytes() }
                    val name = entry.name.removeSuffix(".class")
                    if (name in patches || name == CAPABILITIES) {
                        val target = ClassNode().also { ClassReader(bytes).accept(it, 0) }
                        patches[name]?.let { merge(target, it) } ?: extendCapabilities(target)
                        bytes = ClassWriter(ClassWriter.COMPUTE_MAXS).also(target::accept).toByteArray()
                        patched += name
                    }
                    jar.putNextEntry(JarEntry(entry.name).apply { time = 0 })
                    jar.write(bytes)
                    jar.closeEntry()
                }
            }
        }
        val missing = patches.keys + CAPABILITIES - patched
        if (missing.isNotEmpty()) {
            Files.delete(output)
            error("Missing LWJGL patch targets: $missing")
        }
        logger.lifecycle("Baked LWJGL compatibility into $output")
    }

    private fun merge(target: ClassNode, template: String) {
        val sourceName = "$TEMPLATES$template"
        val templateFile = patchClasses.files.asSequence()
            .map { it.toPath().resolve("$sourceName.class") }
            .firstOrNull(Files::isRegularFile)
            ?: error("Missing template: $template")
        val source = ClassNode()
        Files.newInputStream(templateFile).use {
            ClassReader(it).accept(
                ClassRemapper(source, SimpleRemapper(Opcodes.ASM9, mapOf(sourceName to target.name))), 0,
            )
        }
        source.fields.forEach { field ->
            check(target.fields.none { it.name == field.name }) { "Field collision: ${target.name}.${field.name}" }
            target.fields.add(field)
        }
        source.methods.filterNot { it.name == "<init>" }.forEach { method ->
            check(method.name != "<clinit>") { "Template static initializers are not supported: $template" }
            check(target.find(method.name, method.desc) == null) {
                "Method collision: ${target.name}.${method.name}${method.desc}"
            }
            method.invisibleAnnotations?.filter { it.desc == STUB }?.forEach { annotation ->
                check(method.access and Opcodes.ACC_STATIC != 0) { "CompatStub must be static: ${method.name}" }
                method.access = method.access and (Opcodes.ACC_PRIVATE or Opcodes.ACC_PROTECTED).inv() or Opcodes.ACC_PUBLIC
                annotation.values?.chunked(2)?.firstOrNull { it[0] == "value" }
                    ?.let { delegate(target, method, it[1] as String) }
            }
            method.invisibleAnnotations?.removeIf { it.desc == STUB }
            target.methods.add(method)
        }
        if (target.name == "org/lwjgl/openal/AL") {
            val destroy = target.find("destroy", "()V") ?: error("Missing AL.destroy")
            destroy.access = destroy.access and (Opcodes.ACC_PRIVATE or Opcodes.ACC_PROTECTED).inv() or Opcodes.ACC_PUBLIC
            destroy.instructions.toArray().filter { it.opcode == Opcodes.RETURN }.forEach {
                destroy.instructions.insertBefore(
                    it, MethodInsnNode(Opcodes.INVOKESTATIC, target.name, "exit", "()V", false),
                )
            }
        }
    }

    private fun delegate(owner: ClassNode, stub: MethodNode, name: String) {
        if (name.isEmpty()) return
        val target = owner.find(name, stub.desc)
        check(target != null && target.access and Opcodes.ACC_STATIC != 0) {
            "Missing static delegate: ${owner.name}.$name${stub.desc}"
        }
        stub.instructions.clear()
        stub.tryCatchBlocks.clear()
        stub.localVariables?.clear()
        var local = 0
        Type.getArgumentTypes(stub.desc).forEach {
            stub.instructions.add(VarInsnNode(it.getOpcode(Opcodes.ILOAD), local))
            local += it.size
        }
        stub.instructions.add(MethodInsnNode(Opcodes.INVOKESTATIC, owner.name, name, stub.desc, false))
        stub.instructions.add(InsnNode(Type.getReturnType(stub.desc).getOpcode(Opcodes.IRETURN)))
    }

    private fun extendCapabilities(target: ClassNode) {
        val constructor = target.find(
            "<init>", "(Lorg/lwjgl/system/FunctionProvider;Ljava/util/Set;ZLjava/util/function/IntFunction;)V",
        ) ?: error("Unexpected GLCapabilities constructor")
        capabilityExtensions.forEach { extension ->
            check(target.fields.none { it.name == extension }) { "Capability field collision: $extension" }
            target.fields.add(FieldNode(Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL, extension, "Z", null, null))
            constructor.instructions.toArray().filter { it.opcode == Opcodes.RETURN }.forEach {
                constructor.instructions.insertBefore(it, InsnList().apply {
                    add(VarInsnNode(Opcodes.ALOAD, 0))
                    add(VarInsnNode(Opcodes.ALOAD, 2))
                    add(LdcInsnNode(extension))
                    add(MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/Set", "contains", "(Ljava/lang/Object;)Z", true))
                    add(FieldInsnNode(Opcodes.PUTFIELD, target.name, extension, "Z"))
                })
            }
        }
    }

    private fun ClassNode.find(name: String, descriptor: String) =
        methods.find { it.name == name && it.desc == descriptor }

    private companion object {
        const val TEMPLATES = "pl/tomgirl/lenis/bakery/patch/"
        const val STUB = "L${TEMPLATES}CompatStub;"
        const val CAPABILITIES = "org/lwjgl/opengl/GLCapabilities"
        val patches = mapOf(
            "org/lwjgl/openal/AL" to "ALPatch",
            "org/lwjgl/openal/AL10" to "AL10Patch",
            "org/lwjgl/opengl/GL11" to "GL11Patch",
            "org/lwjgl/opengl/GL20" to "GL20Patch",
            "org/lwjgl/opengl/ARBShaderObjects" to "ARBShaderObjectsPatch",
        )
        val capabilityExtensions = listOf(
            "GL_EXT_multi_draw_arrays", "GL_EXT_paletted_texture", "GL_EXT_rescale_normal",
            "GL_EXT_texture_3d", "GL_EXT_texture_lod_bias", "GL_EXT_vertex_shader", "GL_EXT_vertex_weighting",
        )
    }
}
