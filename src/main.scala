package tofu

import tofu.reader.*, tofu.parser.*, tofu.runner.*
import java.io.File

var debug_mode: Boolean = false

@main def main(args: String*) =
  val argv = args.toVector
  readArgs(argv)
  val scripts = argv.filter(x => isScript(x))
  for s <- scripts do runScript(s)

private def readArgs(args: Seq[String]) =
  if args.contains("--debug") then debug_mode = true

private def isScript(arg: String): Boolean =
  val f_arg = File(arg)
  f_arg.isFile() && f_arg.canRead() && arg.contains(".tofu")

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
