# Tous les .java SAUF ceux dans un dossier tests/
SRC := $(shell find src -name "*.java" -not -path "src/*/tests/*")

# Tous les fichiers de tests (entity + location)
TESTSRC := $(shell find src -path "src/*/tests/*IT.java")

# On transforme les chemins src/foo/bar/BlaIT.java -> foo.bar.BlaIT
TESTCLASSES := $(shell \
	find src -path "src/*/tests/*IT.java" \
	| sed 's|^src/||; s|/|.|g; s|\.java$$||' \
)

BIN := bin
TESTBIN := testbin
JAR := game.jar   # 🔹 nom du jar

.PHONY: all compile run cRun jar exe test clean

all: 
	@echo "usage :"
	@echo "     make compile -> compile game + test"
	@echo "     make run  -> run game"
	@echo "     make cRun -> compile and run"
	@echo "     make jar -> compile and create the .jar file"
	@echo "     make clean -> remove all .class"
	@echo "     make test -> compile and run test"

compile:
	mkdir -p $(BIN)
	javac -d $(BIN) $(SRC)

# Lance le jeu directement (sans recompiler)
run:
	@echo "Running game..."
	java -cp $(BIN) game.Main

# Compile + run
cRun: compile
	java -cp $(BIN) game.Main

# 🔹 Construction du JAR exécutable
jar: compile
	@echo "Building JAR: $(JAR)"
	jar cfe $(JAR) game.Main -C $(BIN) .

# alias "exe" qui fait juste le jar
exe: jar

test: compile
	mkdir -p $(TESTBIN)
	javac -cp lib/junit.jar:lib/hamcrest.jar:$(BIN) -d $(TESTBIN) $(TESTSRC)
	java -cp lib/junit.jar:lib/hamcrest.jar:$(BIN):$(TESTBIN) org.junit.runner.JUnitCore $(TESTCLASSES)

clean:
	rm -rf $(BIN) $(TESTBIN) $(JAR)
