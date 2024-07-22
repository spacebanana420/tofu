# Tofu

Tofu is a barebones scripting language for people who have use-cases for using small, simple scripts but don't like their syntax, features, complexity or platform dependance. Tofu is platform-agnostic (except for the commands you run) and the language specification is very small.


## Running Tofu

You can download the latest version of Tofu **[here](https://github.com/spacebanana420/tofu/releases)**

Tofu's official implementation is an interpreter written in Scala. To run said interpreter, all you need is:

* Java 8 or higher, or Scala 3

I also distribute x86_64 binaries for Linux, so if you happen to use a Linux system with this CPU architecture, you don't need Java or Scala.

Once Tofu is downloaded, you can run it with Scala or Java (or without a runtime, in case of Linux) depending on the JAR/binary you download. You can run `tofu --help` for a quick help screen. To run a script, you can run `tofu script.tofu` (assuming the name is "script.tofu").

You can also pass CLI arguments as global variables: `tofu script.tofu var1 var2 var3`

## Documentation and getting started

Tofu has a small specification and feature-set. You can begin learning how to use it in the links below.

### Tofu v0.3 Documentation

* **[Syntax and instruction set](doc/instructions.md)**
* **[Conditions and branches](doc/conditions.md)**
* **[Variables](doc/variables.md)**

### Tofu v0.2 Documentation

* **[Syntax and instruction set](https://github.com/spacebanana420/tofu/tree/v0.2/doc/instructions.md)**
* **[Conditions and branches](https://github.com/spacebanana420/tofu/tree/v0.2/doc/conditions.md)**
* **[Variables](https://github.com/spacebanana420/tofu/tree/v0.2/doc/variables.md)**

## Building from source

### Scalac

For a lightweight JAR, dependant on Scala, you can compile Tofu with `scalac`. The following command requires a shell that supports the "*" wildcard:

```
mkdir build
scalac src/* src/*/* -d build/tofu.jar
```

### Scala-CLI

You can also build some sort of lightweight JAR with Scala-CLI, but said JAR can only run with Scala-CLI:

```
mkdir build
scala-cli --power package src --library -f -o build/tofu.jar
```

You can instead build an assembly JAR which can be run directly with Java:

```
mkdir build
scala-cli --power package src --assembly --preamble=false -f -o build/tofu.jar
```
