#!/usr/bin/env bash
set -euo pipefail
project_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
test_output="$(mktemp -d)"
trap 'rm -rf "$test_output"' EXIT
javac -d "$test_output" "$project_root/app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/utils/MemoryBudget.java" "$project_root/tests/MemoryBudgetTest.java"
java -cp "$test_output" MemoryBudgetTest
