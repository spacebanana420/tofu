function fullbuild
  exec bash build.sh
  print Building bootstrap JAR
  exec scala-cli --power package src -f -o build/tofu-bootstrap.jar

  print Building assembly JAR
  exec scala-cli --power package src --assembly --preamble=false -f -o build/tofu-java.jar

  print Building native binary (Linux x86_64) (GraalVM)
  exec native-image --no-fallback --static -O3 -jar build/tofu-java.jar -o build/tofu

  print Packaging native binary
  exec 7z a -mx5 -mmt0 build/tofu-linux-x86_64.zip build/tofu
end

if $0 == help
  print Available options: scala, full, java, bootstrap
  print In absence of an option, a lightweight JAR and assembly JAR are built
  stop
endif

if $0 == scala
  exec bash build.sh
  stop
endif

if $0 == full
  goto fullbuild
  stop
endif

if $0 == java
  print Building assembly JAR
  exec scala-cli --power package src --assembly --preamble=false -f -o build/tofu-java.jar
  stop
endif

if $0 == bootstrap
  print Building bootstrap JAR
  exec scala-cli --power package src -f -o build/tofu-bootstrap.jar
  stop
endif

exec bash build.sh
print Building assembly JAR
exec scala-cli --power package src --assembly --preamble=false -f -o build/tofu-java.jar
