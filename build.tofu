function build-scala
  exec bash build.sh
end

function build-bootstrap
  print Building bootstrap JAR
  exec scala-cli --power package src -f -o build/tofu-bootstrap.jar
end

function build-java
  print Building assembly JARs
  exec scala-cli --power package src --assembly --preamble=false -f --jvm 8 -o build/tofu-java.jar
  exec scala-cli --power package src --assembly --preamble=false -f --jvm 19 -o build/tofu-java-19ZZ.jar
end

function fullbuild
  goto build-scala
  goto build-java
  goto build-bootstrap

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
  goto build-scala
  stop
endif

if $0 == full
  goto fullbuild
  stop
endif

if $0 == java
  goto build-java
  stop
endif

if $0 == bootstrap
  goto build-bootstrap
  stop
endif

goto build-scala
goto build-java
