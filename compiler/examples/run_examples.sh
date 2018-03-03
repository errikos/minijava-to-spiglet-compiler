#!/usr/bin/env bash

find "minijava" -type f -iname "*.java" -print0 | while IFS= read -r -d $'\0' file; do
    echo "*** Compiling: ${file}..."
    echo -n "*** :: "
    java -cp ../src/out/production/MiniJava minijava.MiniJavaC "${file}"
    echo "*** Parsing: ${file%.*}.spg... "
    echo -n "*** :: "
    java -jar spp.jar < ${file%.*}.spg
    echo "*** Running your output vs. example"
    java -jar pgiv2.jar < ${file%.*}.spg > .yours.out
    java -jar pgiv2.jar < "spiglet/$(basename "${file%.*}.spg")" > .example.out
    echo "*** Comparing outputs..."
    diff .yours.out .example.out
    rm -f ${file%.*}.spg .yours.out .example.out
    echo
done
