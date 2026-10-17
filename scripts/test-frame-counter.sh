#!/usr/bin/env bash
set -euo pipefail
project_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
test_output="$(mktemp -d)"
trap 'rm -rf "$test_output"' EXIT
cc -std=c11 -pthread -I "$project_root/app_pojavlauncher/src/main/jni" "$project_root/tests/frame_counter_test.c" -o "$test_output/counter"
"$test_output/counter"
javac -d "$test_output" "$project_root/app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/utils/FrameRateSample.java" "$project_root/tests/FrameRateSampleTest.java"
java -cp "$test_output" FrameRateSampleTest
