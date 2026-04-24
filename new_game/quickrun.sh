#!/usr/bin/env sh

rm -rf out sources.txt
find src -name "*.java" > sources.txt

JFX="/usr/share/openjfx/lib"

javac --module-path "$JFX" --add-modules javafx.controls,javafx.graphics \
      -d out -sourcepath src @sources.txt || exit 1

if [ -z "$1" ]
then
    java -cp out application.Main
else
    java --module-path "$JFX" --add-modules javafx.controls,javafx.graphics \
         -cp out application.GuiMain
fi