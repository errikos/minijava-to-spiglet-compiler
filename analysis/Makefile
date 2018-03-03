JAVAC=javac
JVM=java
CLASSPATH = lib/iris-0.60.jar:lib/iris-parser-0.60.jar:src/
sources = $(wildcard src/*.java)
classes = $(sources:.java=.class)

all: compile

compile: $(classes)

clean :
	find src/. -name "*.class" -exec rm -rf {} +

%.class : %.java
	$(JAVAC) -cp $(CLASSPATH) $<
