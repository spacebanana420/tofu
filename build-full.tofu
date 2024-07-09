print Building lightweight JAR
exec bash build.sh

print Building assembly JAR (Java 8)
exec scala-cli --power package src --assembly --preamble=false --jvm 8 -f -o build/tofu-java.jar

print Building native binary (Linux x86_64) (GraalVM)
exec native-image --no-fallback --static -O3 -jar build/tofu-java.jar -o build/tofu

print Packaging native binary
exec 7z a -mx5 -mmt0 build/tofu-linux-x86_64.zip build/tofu
