CLASSES=wakeonlan.java
JAR=wakeonlan.jar

all:
	javac $(CLASSES)
	jar cfe $(JAR) wakeonlan *.class
	rm -f *.class

clean:
	rm -f $(JAR)
	rm -f *.class
