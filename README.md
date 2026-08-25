<img alt="Lenis logo" src="src/main/resources/assets/lenis/icon.png" width="128px" />

# Lenis
Lenis allows legacy Minecraft versions to run with LWJGL 3. It replaces the outdated windowing system with SDL 3, and serves as a library for mods.

Currently only 1.8.9 on Ornithe is supported, with support for more versions coming soon.

## Downloads
* [Latest Release](https://github.com/notdevcody/lenis/releases/latest)
* [Latest Nightly](https://nightly.link/notdevcody/lenis/workflows/nightly/main/lenis-nightly.zip)

## Features
### Fixes
- Lag from high-polling-rate mice
- Keys and mouse buttons getting stuck
- Crash when resizing the window on macOS

### Improvements
- System IME support
- More precise mouse input
- Proper clipboard handling
- Reduced display / input overhead
- Dark mode for the window title bar
- Better fullscreen and window resizing

## Developers
Lenis is published to the Clover Client Maven, and can be added as a dependency like so:
```kts
repositories {
    maven("https://maven.cloverclient.com/releases")
}

dependencies {
    modImplementation("pl.tomgirl:lenis:${version}")
}
```
