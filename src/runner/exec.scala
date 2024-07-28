package tofu.runner

import tofu.{debugMessage, debug_printSeq}
import tofu.variables.*, tofu.parser.*, tofu.reader.*
import scala.sys.process.*
import tofu.closeTofu

private def addArg(args: Vector[String], arg: String): Vector[String] =
  args :+ VarReader(arg).value_str

private def mkcommand(line: String, cmd: Vector[String] = Vector(), arg: String = "", i: Int = 0, ignore_spaces: Boolean = false): Vector[String] =
  if i >= line.length then
    if arg.length == 0 then cmd
    else addArg(cmd, arg)
  else if line(i) == '"' then
    mkcommand(line, cmd, arg, i+1, !ignore_spaces)
  else if line(i) == ' ' || line(i) == '\t' then
    if ignore_spaces then
      mkcommand(line, cmd, arg + line(i), i+1, ignore_spaces)
    else
      mkcommand(line, addArg(cmd, arg), "", i+1, ignore_spaces)
  else
    mkcommand(line, cmd, arg + line(i), i+1, ignore_spaces)

def exec(line: String) =
  val cmd_start = findLineStart(line, 4)
  debugMessage(s"Exec parsing started at $cmd_start")
  val cmd = mkcommand(line, i = cmd_start)
  if cmd.length == 0 then closeTofu(s"Syntax error! Command is empty at line:\n$line\n")
  debug_printSeq("Running command:", cmd)
  cmd.!<
