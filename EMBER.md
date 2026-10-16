# Ember Launcher

An independent Android Minecraft: Java Edition launcher based on Amethyst and PojavLauncher. Package: `io.github.catoware.ember.debug`.

## Changes

- Charcoal surfaces, orange accents, rounded buttons, and an Ember flame icon.
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
