package tofu.runner

import tofu.parser.*
import tofu.reader.findLineStart

import scala.sys.process.*
import scala.compiletime.ops.long
import tofu.reader.readScript

private def lineType(line: String, types: Vector[String] = Vector("set", "exec", "goto", "stop", "loop"),  i: Int = 0): String =
  if i >= types.length then "none"
  else if startsWith(line, types(i)) then types(i)
  else lineType(line, types, i+1)

private def findInList(find: String, list: Seq[String], i: Int = 0): Int =
  if i >= list.length then -1
  else if find == list(i) then i
  else findInList(find, list, i+1)

def runScript(path: String) =
  val script = readScript(path)
  val i_variable = getVariables(script) //maybe variables should be checked at runtime instead
  val name_var = getVariableNames(script, i_variable)
  val val_var = getVariableValues(script, i_variable)

  val i_func = getFuncIndexes(script)
  val name_func = getFuncNames(script, i_func)

  loopScript(script, i_variable, name_var, i_func, name_func)

//   val i_checkpoint = getCheckpoints(script)
//   val name_checkpoint = getCheckpointNames(script, i_checkpoint)

private def skipFunction(s: Seq[String], i: Int): Int =
  if i >= s.length then -1 //must not happen!!!!! functions must have an "end"
  else if startsWith(s(i), "end") then i+1
  else skipFunction(s, i+1)

private def removeLastPointer(stack: Vector[Int], newstack: Vector[Int] = Vector(), i: Int = 0): Vector[Int] =
  if i >= stack.length-1 then newstack
  else removeLastPointer(stack, newstack :+ stack(i), i+1)

private def loopScript(s: Seq[String], iv: Seq[Int], nv: Seq[String], ifunc: Seq[Int], nfunc: Seq[String], i: Int = 0, runningfuncs: Int = 0, pointer_stack: Vector[Int] = Vector()): Unit =
  if i < s.length then
    if runningfuncs > 0 && startsWith(s(i), "end") then loopScript(s, iv, nv, ifunc, nfunc, pointer_stack(pointer_stack.length-1), runningfuncs-1, removeLastPointer(pointer_stack))
    else
      val linetype = lineType(s(i))
      if linetype != "stop" then linetype match
        case "function" => //be careful for when actually calling functions (maybe i dont have to worry)
          val afterfunc = skipFunction(s, i+1)
          loopScript(s, iv, nv, ifunc, nfunc, afterfunc, runningfuncs, pointer_stack)
        case "set" =>
        case "exec" => exec(s(i))
        case "goto" => loopScript(s, iv, nv, ifunc, nfunc, goToFunc(s(i), ifunc, nfunc), runningfuncs+1, pointer_stack :+ (i+1))
        loopScript(s, iv, nv, ifunc, nfunc, i+1, runningfuncs, pointer_stack)


def goToLine(line: String, ci: Seq[Int], cn: Seq[String]): Int = //make goto function instead
  val name_start = findLineStart(line, 4)
  val name = getName(line, name_start)
  val i = findInList(line, cn)
  ci(i)

def goToFunc(line: String, fi: Seq[Int], fn: Seq[String]): Int =
  val name_start = findLineStart(line, 4)
  val name = getName(line, name_start)
  val i = findInList(line, fn)
  fi(i)+1

private def mkcommand(line: String, cmd: Vector[String] = Vector(), arg: String = "", i: Int = 0, ignore_spaces: Boolean = false): Vector[String] =
  if i >= line.length then cmd
  else if line(i) == ' ' || line(i) == '\t' then
    if ignore_spaces then
      mkcommand(line, cmd, arg + line(i), i+1, ignore_spaces)
    else if arg.length > 0 then
      mkcommand(line, cmd :+ arg, "", i+1, ignore_spaces)
    else
      mkcommand(line, cmd, arg, i+1, ignore_spaces)
  else if line(i) == '"' then
    mkcommand(line, cmd, arg, i+1, !ignore_spaces)
  else
    mkcommand(line, cmd, arg + line(i), i+1, ignore_spaces)

def exec(line: String) =
  val cmd_start = findLineStart(line, 4)
  val cmd = mkcommand(line)
  cmd.!
