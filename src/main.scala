package tofu

import tofu.reader.*, tofu.parser.*, tofu.runner.*, tofu.variables.*
import java.io.File

var debug_mode: Boolean = false
private val interpreter_version = "Tofu version 0.5"

@main def main(args: String*) =
  val scripts = parseCLI(args.toVector)
  for s <- scripts do runScript(s)

private def parseCLI(argv: Vector[String]): Vector[String] =
  val hasArgs = readArgs(argv)
  val scripts = argv.filter(x => isScript(x))
  val script_args = argv.filter(x => x.length > 0 && !isScript(x) && x(0) != '-')
  addGlobalVariables(script_args)
  debug_printSeq("The following CLI args have been passed to the script", script_args)
  debug_printSeq("Startup variables:", var_name)
  if scripts.length == 0 && !hasArgs then printHelp()
  scripts


private def addGlobalVariables(vars: Vector[String], i: Int = 0): Unit =
  if i < vars.length then
    if isInt(vars(i)) then declareInt(i.toString(), mkInt(vars(i)))
    else declareString(i.toString(), vars(i))
    addGlobalVariables(vars, i+1)

private def readArgs(args: Seq[String]): Boolean =
  val d = args.contains("--debug")
  val v = args.contains("--version")
  val h = args.contains("--help")

  if d then debug_mode = true
  if v then printVersion()
  if h then printHelp()
  v || h

private def isScript(arg: String): Boolean =
  val f_arg = File(arg)
  f_arg.isFile() && f_arg.canRead() && arg.contains(".tofu")

private def printHelp() =
  println(
    s"$interpreter_version\n\nUsage: tofu [script path] [program arguments] [script arguments]\nExample: tofu /path/to/script.tofu"
    + "\n\nAvailable program arguments:\n\t--help - Prints this message\n\t--version - Prints the Tofu version"
    + "\n\t--debug - Enables debug mode, the interpreter prints information on what it's doing"
    + "\n\nYou can pass CLI arugments as global variables by passing arguments which do not start with '-' and are not paths leading to a readable file.\nExample: tofu script.tofu argument1 argument2 argument3\nInside the script, you can read the 3 arguments respectively with the $0, $1 and $2 variable calls"
  )

private def printVersion() = println(interpreter_version)

def debugMessage(msg: String) = //will be fancier later
  if debug_mode then println(s"[DEBUG] $msg")

def debug_printSeq(msg: String, s: Seq[String]) =
  if debug_mode then
    println(s"[DEBUG] $msg")
    for i <- s do
      println(s"\t$i")

def closeTofu(msg: String = "") =
  if msg.length > 0 then println(msg)
  System.exit(1)
