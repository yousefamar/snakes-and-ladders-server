all:
	javac -d bin -sourcepath src src/core/Main.java

clean :
	find . -name "*.class" -type f -delete
