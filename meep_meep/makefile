JFX=/usr/share/openjfx/lib
SRC_DIR=src
OUT_DIR=out
SOURCES=sources.txt

JAVAC=javac
JAVA=java

JAVAC_FLAGS=--module-path $(JFX) --add-modules javafx.controls,javafx.graphics \
            -d $(OUT_DIR) -sourcepath $(SRC_DIR)

JAVA_FX_FLAGS=--module-path $(JFX) --add-modules javafx.controls,javafx.graphics \
              -cp $(OUT_DIR)

JAVA_CLI_FLAGS=-cp $(OUT_DIR)

.PHONY: all build gui random cli clean rebuild

all: cli

build:
    rm -rf $(OUT_DIR) $(SOURCES)
    find $(SRC_DIR) -name "*.java" > $(SOURCES)
    $(JAVAC) $(JAVAC_FLAGS) @$(SOURCES)

cli: build
    $(JAVA) $(JAVA_CLI_FLAGS) application.Main

gui: build
    $(JAVA) $(JAVA_FX_FLAGS) application.GuiMain

random: build
    $(JAVA) $(JAVA_FX_FLAGS) application.RandomGuiMain

clean:
    rm -rf $(OUT_DIR) $(SOURCES)

rebuild: clean build