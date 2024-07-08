package tofu.runner

import tofu.{debugMessage, debug_printSeq}
import tofu.parser.*
import tofu.reader.findLineStart

import scala.sys.process.*
import tofu.reader.readScript
import tofu.closeTofu

private def lineType(line: String, types: Vector[String] = Vector("set", "print", "function", "exec", "goto", "stop", "loop"),  i: Int = 0): String =
  if i >= types.length then "none"
  else if startsWith(line, types(i)) then types(i)
  else lineType(line, types, i+1)

private def findInList(find: String, list: Seq[String], i: Int = 0): Int =
  if i >= list.length then -1
  else if find == list(i) then i
  else findInList(find, list, i+1)

private var var_i = Vector[Int]()
private var var_name = Vector[String]()
private var var_val = Seq[String]()

def runScript(path: String) =
  val script = readScript(path)
  var_i = getVariables(script) //maybe variables should be checked at runtime instead
  var_name = getVariableNames(script, var_i)
  var_val = getVariableValues(script, var_i)

  val i_func = getFuncIndexes(script)
  val name_func = getFuncNames(script, i_func)

  debug_printSeq("Script in memory:", script)
  debug_printSeq("Variable names:", var_name)
  debug_printSeq("Function names:", name_func)

  loopScript(script, i_func, name_func)

//   val i_checkpoint = getCheckpoints(script)
//   val name_checkpoint = getCheckpointNames(script, i_checkpoint)

private def skipFunction(s: Seq[String], i: Int): Int =
  if i >= s.length then -1 //must not happen!!!!! functions must have an "end"
  else if startsWith(s(i), "end") then i+1
  else skipFunction(s, i+1)

private def removeLastPointer(stack: Vector[Int], newstack: Vector[Int] = Vector(), i: Int = 0): Vector[Int] =
  if i >= stack.length-1 then newstack
  else removeLastPointer(stack, newstack :+ stack(i), i+1)

private def loopScript(s: Seq[String], ifunc: Seq[Int], nfunc: Seq[String], i: Int = 0, runningfuncs: Int = 0, pointer_stack: Vector[Int] = Vector()): Unit =
  if i < s.length then
    if runningfuncs > 0 && startsWith(s(i), "end") then
      val new_i = pointer_stack(pointer_stack.length-1)
      debugMessage(s"Found the end of a function, returning to $new_i")
      loopScript(s, ifunc, nfunc, new_i, runningfuncs-1, removeLastPointer(pointer_stack))
    else
      val linetype = lineType(s(i))
      if linetype == "stop" then closeTofu()
      else linetype match
        case "function" =>
          val afterfunc = skipFunction(s, i+1)
          debugMessage(s"Skipping function at ${s(i)}")
          loopScript(s, ifunc, nfunc, afterfunc, runningfuncs, pointer_stack)
        case "set" =>
          loopScript(s, ifunc, nfunc, i+1, runningfuncs, pointer_stack)
        case "exec" =>
          exec(s(i))
          loopScript(s, ifunc, nfunc, i+1, runningfuncs, pointer_stack)
        case "goto" =>
          loopScript(s, ifunc, nfunc, goToFunc(s(i), ifunc, nfunc), runningfuncs+1, pointer_stack :+ (i+1))
        case "print" =>
          printArg(s(i))
          loopScript(s, ifunc, nfunc, i+1, runningfuncs, pointer_stack)
        case _ =>
          loopScript(s, ifunc, nfunc, i+1, runningfuncs, pointer_stack)

// def goToLine(line: String, ci: Seq[Int], cn: Seq[String]): Int =
//   val name = getName(line, name_start)
//   val i = findInList(line, cn)
//   ci(i)

//for later: process function and arguments all in a vector, similar to exec
def goToFunc(line: String, fi: Seq[Int], fn: Seq[String]): Int =
  val name_start = findLineStart(line, 4)
  val name = getName(line, name_start)
  debugMessage(s"Calling function $name")
  val i = findInList(name, fn)
  debugMessage(s"Moved to line ${fi(i)+1}")
  fi(i)+1

private def addArg(args: Vector[String], arg: String): Vector[String] =
  if arg(0) == '$' then args :+ readVariable(arg)
  else args :+ arg

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

private def readVariable(variable: String, i: Int = 0, v: String = ""): String =
  if i >= variable.length || variable(i) == ' ' then
    val var_index = findInList(v, var_name)
    if var_index == -1 then v else var_val(var_index)
  else if i == 0 then readVariable(variable, i+1, v)
  else readVariable(variable, i+1, v + variable(i))

def exec(line: String) =
  val cmd_start = findLineStart(line, 4)
  debugMessage(s"Exec parsing started at $cmd_start")
  val cmd = mkcommand(line, i = cmd_start)
  debug_printSeq("Running command:", cmd)
  cmd.!<

private def getPrintMsg(line: String, i: Int, msg: String = ""): String =
  if i >= line.length then
    if msg(0) == '$' then readVariable(msg)
    else msg
  else getPrintMsg(line, i+1, msg + line(i))

def printArg(line: String) =
  val msg_start = findLineStart(line, 5)
  debugMessage(s"Printing parsing started at $msg_start")
  val msg = getPrintMsg(line, msg_start)
  println(msg)
