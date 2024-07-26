function build-scalacli
  exec scala-cli --power package src --assembly --preamble=false -f --jvm 11 -o build/tofu.jar
  exec 7z d build/tofu.jar scala LICENSE NOTICE library.properties rootdoc.txt
end

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
  exec scala-cli --power package src --assembly --preamble=false -f --jvm 19 -o build/tofu-java-19.jar
end

function fullbuild
  call build-scala
  call build-java
  call build-bootstrap

  print Building native binary (Linux x86_64) (GraalVM)
  exec native-image --no-fallback --static -O3 -jar build/tofu-java.jar -o build/tofu

  print Packaging native binary
  exec 7z a -mx5 -mmt0 build/tofu-linux-x86_64.zip build/tofu
end

if $0 == help
  print Available options: light, scala, full, java, bootstrap
  print " "
  print light - lightweight JAR (requires Scala) (Uses Scala-CLI and 7zip instead of scalac)
  print scala - lightweight JAR (requires Scala)
  print java - assembly JAR (requires Java only)
  print bootstrap - lightweight JAR with shell preamble (requires Scala) (is executed directly)
  print full - portable executable (requires GraalVM to build)
  print " "
  print In absence of an option, a lightweight JAR and assembly JAR are built
  stop
endif

if $0 == scala
  call build-scala
  stop
endif

if $0 == light
  call build-scalacli
  stop
endif

if $0 == full
  call fullbuild
  stop
endif

if $0 == java
  call build-java
  stop
endif

if $0 == bootstrap
  call build-bootstrap
  stop
endif

call build-scala
call build-java
