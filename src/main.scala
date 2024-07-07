package tofu

import tofu.reader.*, tofu.parser.*, tofu.runner.*
import java.io.File

var debug_mode: Boolean = false

@main def main(args: String*) =
  val argv = args.toVector
  readArgs(argv)
  val scripts = argv.filter(x => isScript(x))
  for s <- scripts do runScript(s)

def readArgs(args: Seq[String]) =
  if args.contains("--debug") then debug_mode = true

def isScript(arg: String): Boolean =
  val f_arg = File(arg)
  f_arg.isFile() && f_arg.canRead() && arg.contains(".tofu")
