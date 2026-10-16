#!/usr/bin/env bash
set -euo pipefail
project_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
backup_root="${1:-/sdcard/Documents/EmberLauncher}"
mkdir -p "$backup_root/source" "$backup_root/apk" "$backup_root/history"
# Preserve the files of submodules too; exclude local credentials, toolchains and caches.
tar -C "$project_root" --exclude='./.git' --exclude='*/.git' --exclude='*/build' --exclude='*/.gradle' --exclude='*/.cxx' --exclude='./local.properties' --exclude='*.jks' --exclude='*/curseforge_key.txt' -czf "$backup_root/source/Ember-Launcher-source.tar.gz" .
git -C "$project_root" bundle create "$backup_root/history/Ember-Launcher.bundle" --all
cp "$project_root/EMBER.md" "$backup_root/README.md"
printf 'Saved source and Git history to %s\n' "$backup_root"
