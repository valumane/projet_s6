#!/usr/bin/env sh

rm -rf out sources.txt
find src -name "*.java" > sources.txt
javac -d out -sourcepath src @sources.txt || exit 1

if [ -z "$1" ]; 
then java -cp out application.Main
else java --enable-native-access=javafx.graphics -cp out application.GuiMain
fi