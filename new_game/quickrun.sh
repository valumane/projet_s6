#!/usr/bin/env sh

rm -rf out sources.txt
find src -name "*.java" > sources.txt

JFX="/usr/share/openjfx/lib"

javac --module-path "$JFX" --add-modules javafx.controls,javafx.graphics \
      -d out -sourcepath src @sources.txt || exit 1

case "$1" in
  "")
    java -cp out application.Main
    ;;
  gui)
    java --module-path "$JFX" --add-modules javafx.controls,javafx.graphics \
         -cp out application.GuiMain
    ;;
  random)
    java --module-path "$JFX" --add-modules javafx.controls,javafx.graphics \
         -cp out application.RandomGuiMain
    ;;
  *)
    echo "Usage: ./quickrun.sh [gui|random]"
    ;;
esac