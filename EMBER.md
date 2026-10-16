# Ember Launcher

An independent Android Minecraft: Java Edition launcher based on Amethyst and PojavLauncher. Package: `io.github.catoware.ember.debug`.

## Changes

- A phone layout with a selected-instance launch panel, full-width Play button, a list that fits the instance count, and paired utility controls. The page scrolls on smaller screens and in landscape.
- Rebuilt identity, offline profile, instance library, instance editor, modpack entry, and file-picker screens.
- Neutral surfaces, softly rounded panels, readable form headings, simple settings rows, and matching line icons.
- **Auto Configure RAM** in Settings → Java: opt in to a fresh RAM budget before every launch. Total RAM, available RAM, Android’s low-memory threshold, and 32-bit limits inform the budget. Manual settings are preserved when switched off.
- Java starts with at most 128 MB of heap and grows up to the configured limit. Automatic allocation caps at 2 GB and leaves Android/native-renderer headroom; heavy modpacks may need manual tuning. No background apps are killed.
- **Device & benchmark** reports phone/Android/ABI/CPU/RAM and the real GLES driver. Its short CPU and memory-copy tests report measured throughput, not a fabricated score or Minecraft FPS. GPU performance is not measured.
- Ember branding in app icons, notifications, profile fallbacks, and Android document-provider entries.
- An emulator visual-review workflow that checks navigation and captures real Android screenshots.
- Offline profile creation without a previous Microsoft sign-in.
- Stable username-based offline UUIDs and proper reporting when a profile cannot be saved.
- Local profiles can install game versions with an internet connection and use the installer, modpack, and game-folder tools.
- Optional Microsoft sign-in remains available. Offline identities do not authenticate to online-mode servers.

## Build and download

Open this repository's Actions tab and run **Ember Android APK**. The workflow installs Java 21, Android SDK 36 and NDK 27, retrieves the upstream Java 8 runtime, and builds a debug APK without private release-signing credentials. Download **Ember-Launcher-APK** from the completed run. The artifact includes a SHA-256 checksum.

The debug APK uses the inherited development key. It is suitable for development; a private signing key is needed before a production release. A CurseForge API key is optional and is not bundled.

## Use

1. Install the APK, open the profile selector, and choose **Offline profile — no sign-in**.
2. Enter a unique 3–16 character username using letters, numbers, or underscores.
3. Select a game version and connect once to install its files. Newer Minecraft versions may require a newer Java runtime from launcher settings.
4. Launch an installed version offline. Mod installers and missing dependencies may still need internet.

## Workspace

The working checkout is `/root/projects/ember-launcher`. Shared-storage backups and downloaded APKs are under `/sdcard/Documents/EmberLauncher`. Run `bash scripts/save-to-storage.sh` on this device to refresh the source snapshot and Git history bundle. Build caches stay outside the shared-storage backup.

## Credits and license

Based on [Amethyst Android](https://github.com/AngelAuraMC/Amethyst-Android) and PojavLauncher. Original copyrights and the LGPLv3 license are retained. See `README.md` for upstream documentation and `LICENSE` for terms.
