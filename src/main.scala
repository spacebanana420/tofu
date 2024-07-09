package tofu

import tofu.reader.*, tofu.parser.*, tofu.runner.*
import java.io.File

var debug_mode: Boolean = false
private val interpreter_version = "Tofu version 0.1"

@main def main(args: String*) =
  val argv = args.toVector
  readArgs(argv)
  val scripts = argv.filter(x => isScript(x))
  script_args = argv.filter(x => x.length > 0 && !isScript(x) && x(0) != '-')
  debug_printSeq("The following CLI args have been passed to the script", script_args)
  for s <- scripts do runScript(s)

private def readArgs(args: Seq[String]) =
  if args.contains("--debug") then debug_mode = true
  if args.contains("--version") then printVersion()
  if args.contains("--help") then printHelp()

private def isScript(arg: String): Boolean =
  val f_arg = File(arg)
  f_arg.isFile() && f_arg.canRead() && arg.contains(".tofu")

private def printHelp() =
  println(
    s"$interpreter_version\n\nUsage: tofu [script path] [arguments]\nExample: tofu /path/to/script.tofu"
    + "\n\nAvailable arguments:\n\t--help - Prints this message\n\t--version - Prints the Tofu version"
    + "\n\t--debug - Enables debug mode, the interpreter prints information on what it's doing"
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
  System.exit
