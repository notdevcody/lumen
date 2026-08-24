package org.lwjgl.util.glu;

import static org.lwjgl.util.glu.GLU.GLU_EXTENSIONS;
import static org.lwjgl.util.glu.GLU.GLU_VERSION;

@SuppressWarnings("unused")
public class Registry extends Util {
    private static final String versionString = "1.3";
    private static final String extensionString = "GLU_EXT_nurbs_tessellator " + "GLU_EXT_object_space_tess ";

    public static String gluGetString(int name) {
        return name == GLU_VERSION ? versionString
            : name == GLU_EXTENSIONS ? extensionString
            : null;
    }

    public static boolean gluCheckExtension(String extName, String extString){
        return extString != null && extName != null && extString.contains(extName);
    }
}
