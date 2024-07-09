# Tofu

Tofu is a barebones scripting language for people who have use-cases for using shell scripts but don't like their syntax, features, size or platform dependance. Tofu is platform-agnostic (except for the commands you run) and the language specification is very small.

# Requirements

Tofu's official implementation is an interpreter written in Scala. To run said interpreter, all you need is:

* Scala 3 or Java 8 or higher

**This repository is brand-new and the program isn't ready to be used yet.**

# Documentation and getting started

Tofu has a small specification and feature-set. You can begin learning how to use it in the links below

* **[Tofu syntax and instruction set](doc/instructions.md)**

# Building from source


## Scalac

For a lightweight JAR, dependant on Scala, you can compile Tofu with `scalac`. The following command requires a shell that supports the "*" wildcard:

```
mkdir build
scalac src/* src/*/* -d build/tofu.jar
```

## Scala-CLI

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
