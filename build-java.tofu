print Building Tofu as an assembly JAR
exec scala-cli --power package src --assembly --preamble=false -f -o build/tofu-java.jar
