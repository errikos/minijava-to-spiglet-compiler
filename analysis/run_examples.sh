#!/usr/bin/env bash

CLASSPATH=lib/iris-0.60.jar:lib/iris-parser-0.60.jar:src/

echo "[*] Building project..."
make

find "examples" -type f -iname "*.spg" -print0 | while IFS= read -r -d $'\0' file; do
    echo "[*] Running optimizer for file ${file}..."
    java -cp ${CLASSPATH} SpigletOptimizer ${file}
done
