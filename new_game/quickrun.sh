rm -rf out sources.txt
find src -name "*.java" > sources.txt
javac -d out -sourcepath src @sources.txt
java -cp out app.Main