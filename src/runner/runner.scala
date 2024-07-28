package tofu.runner

import tofu.{debugMessage, debug_printSeq}
import tofu.variables.*
import tofu.parser.*
import tofu.terminal.TerminalOps

import tofu.reader.readScript
import tofu.closeTofu

var var_name = Vector[String]()
var var_type = Array[variable_type]()
var var_pointer = Array[Int]()

var string_val = Array[String]()
var int_val = Array[Int]()
var array_val: Array[TofuArray] = Array()

var function_stack = Vector[Int]()
var while_stack = Vector[Int]()
var for_stack = Vector[Int]()
var for_amount = Array[Int]()
var for_max = Array[Int]()

def runScript(path: String) =
  val script = readScript(path)

  val i_func = getFuncIndexes(script)
  val name_func = getFuncNames(script, i_func)

  debug_printSeq("Script in memory:", addLineIndicator(script))
  debug_printSeq("Function names:", name_func)

  runStartupChecks(script)
  loopScript(script, i_func, name_func)

private def loopScript(s: Seq[String], ifunc: Seq[Int], nfunc: Seq[String], i: Int = 0): Unit =
  if i < s.length then
    if for_stack.length > 0 && startsWith_strict(s(i), "endfor") then
      val new_i = for_stack(for_stack.length-1)
      debugMessage(s"For loop block ended, returning to line $new_i")
      loopScript(s, ifunc, nfunc, new_i)
    else if while_stack.length > 0 && startsWith_strict(s(i), "endwhile") then
      val new_i = while_stack(while_stack.length-1)
      while_stack = removeLastPointer(while_stack)
      debugMessage(s"While block ended, returning to line $new_i")
      loopScript(s, ifunc, nfunc, new_i)
    else if function_stack.length > 0 && (startsWith_strict(s(i), "end") || startsWith(s(i), "return")) then
      val new_i = function_stack(function_stack.length-1)
      //debugMessage(s"Found the end or return of a function, returning to line $new_i")
      function_stack = removeLastPointer(function_stack)
      loopScript(s, ifunc, nfunc, new_i)
    else
      val linetype = lineType(s(i))
      if linetype == "stop" then closeTofu()
      else linetype match
        case "function" =>
          val afterfunc = skipFunction(s, i)
          debugMessage(s"Found function at line $i, found end of the block at line $afterfunc")
          loopScript(s, ifunc, nfunc, afterfunc)
        case "break" =>
          if while_stack.length > 0 then
            while_stack = removeLastPointer(while_stack)
            val end = findEndWhile(s, i)
            debugMessage(s"Found break, cancelling loop execution, moving to line $end")
            loopScript(s, ifunc, nfunc, end)
        case "breakfor" =>
          if for_stack.length > 0 then
            for_stack = removeLastPointer(for_stack)
            for_amount = removeLastPointer_a(for_amount)
            for_max = removeLastPointer_a(for_max)
            val end = findEndFor(s, i)
            debugMessage(s"Found break, cancelling loop execution, moving to line $end")
            loopScript(s, ifunc, nfunc, end)
        case "int" =>
          setVariable_int(s(i))
          loopScript(s, ifunc, nfunc, i+1)
        case "calcint" =>
          calculateInt(s(i))
          loopScript(s, ifunc, nfunc, i+1)
        case "string" =>
          setVariable_str(s(i))
          loopScript(s, ifunc, nfunc, i+1)
        case "readstr" =>
          read_string(s(i))
          loopScript(s, ifunc, nfunc, i+1)
        case "array" =>
          val name = parseArrayDeclaration(s(i))
          declareArray(name)
          loopScript(s, ifunc, nfunc, i+1)
        case "arradd" =>
          val (name, value) = parseArrayAddition(s(i))
          val tvar = VarReader(value)
          if tvar.is_int then
            addToArray(name, tvar.value_int)
          else addToArray(name, tvar.value_str)
          loopScript(s, ifunc, nfunc, i+1)
        case "arrget" =>
          val (name, variable, index) = parseArrayAccess(s(i), 7)
          getFromArray(name, index) match
            case intValue: Int =>
              declareInt(variable, intValue)
            case strValue: String =>
              declareString(variable, strValue)
            case other =>
              closeTofu(s"Type error! Value '$other' from array '$name' at index $index is not an Int or String!")
          loopScript(s, ifunc, nfunc, i+1)
        case "arreplace" =>
          val (name, value, index) = parseArrayAccess(s(i), 9)
          val tvar = VarReader(value)
          if tvar.is_int then replaceInArray(name, index, tvar.value_int)
          else replaceInArray(name, index, tvar.value_str)
          loopScript(s, ifunc, nfunc, i+1)
        case "arrlen" =>
          val (name, variablename) = parseArrayAddition(s(i))
          val v = VarReader(name)
          if v.vartype != variable_type.array then
            closeTofu(s"Array error! The array $name at line\n${s(i)}\nDoes not exist!")
          declareInt(variablename, array_val(v.pointer).size())
          loopScript(s, ifunc, nfunc, i+1)
        case "exec" =>
          exec(s(i))
          loopScript(s, ifunc, nfunc, i+1)
        case "call" =>
          function_stack = function_stack :+ (i+1)
          loopScript(s, ifunc, nfunc, goToFunc(s(i), ifunc, nfunc))
        case "print" =>
          printArg(s(i))
          loopScript(s, ifunc, nfunc, i+1)
        case "clear" =>
          print(TerminalOps.clearScreen())
          loopScript(s, ifunc, nfunc, i+1)
        case "locate" =>
          val (x, y) = parseLocate(s(i))
          print(TerminalOps.locate(x, y))
          loopScript(s, ifunc, nfunc, i+1)
        case "color" =>
          val (colorType, color) = parseColor(s(i))
          debugMessage(s"Changing color of '$colorType' to '$color'")
          colorType match {
            case "text" => print(TerminalOps.getColor(color))
            case "background" => print(TerminalOps.getBackgroundColor(color))
            case _ => closeTofu(s"Syntax error! Unknown color type '$colorType'")
          }
          loopScript(s, ifunc, nfunc, i+1)
        case "sleep" =>
          runSleep(s(i))
          loopScript(s, ifunc, nfunc, i+1)
        case "while" =>
          val condition = checkCondition(s(i), false)
          val end = findEndWhile(s, i)
          debugMessage(s"Found while at line $i, found end of the block at line $end")
          if condition then
            while_stack = while_stack :+ i
            loopScript(s, ifunc, nfunc, i+1)
          else
            loopScript(s, ifunc, nfunc, end)
        case "for" =>
          val end = findEndFor(s, i)
          debugMessage(s"End of loop block: $end")
          if for_stack.length > 0 && i == for_stack(for_stack.length-1) then //SAME FOR LOOP
            if for_amount(for_amount.length-1) == 0 then //END OF LOOP
              debugMessage("Loop has ended")
              for_stack = removeLastPointer(for_stack)
              for_amount = removeLastPointer_a(for_amount)
              for_max = removeLastPointer_a(for_max)
              loopScript(s, ifunc, nfunc, end)
            else //keep going
              forloop_nextline(s(i))
              loopScript(s, ifunc, nfunc, i+1)
          else //NEW LOOP
            if forloop_create(s(i), i) then
              loopScript(s, ifunc, nfunc, i+1)
            else
              loopScript(s, ifunc, nfunc, end)
        case "if" =>
          val condition = checkCondition(s(i), true)
          val endif = findEndIF(s, i)
          debugMessage(s"Found if statement at line $i, found end of the block at line $endif")
          if condition then
            loopScript(s, ifunc, nfunc, i+1)
          else
            loopScript(s, ifunc, nfunc, endif)
        case _ =>
          loopScript(s, ifunc, nfunc, i+1)

private def lineType(line: String, types: Vector[String] =
Vector(
"string", "readstr", "while", "for", "break", "breakfor", "sleep", "calcint", "int",
"print", "clear", "locate", "color", "if", "function", "exec", "call",
"stop", "array", "arradd", "arrget", "arreplace", "arrlen"),
i: Int = 0): String =
  if i >= types.length then "none"
  else if startsWith_strict(line, types(i)) then types(i)
  else lineType(line, types, i+1)

private def addLineIndicator(lines: Vector[String], n: Vector[String] = Vector(), i: Int = 0): Vector[String] =
  if i >= lines.length then n
  else addLineIndicator(lines, n :+ s"[$i] ${lines(i)}", i+1)

private def skipFunction(s: Seq[String], i: Int): Int = findBlockEnd(s, "function", "end", i+1, 1)

private def removeLastPointer(stack: Vector[Int], newstack: Vector[Int] = Vector(), i: Int = 0): Vector[Int] =
  if i >= stack.length-1 then newstack
  else removeLastPointer(stack, newstack :+ stack(i), i+1)

private def removeLastPointer_a(stack: Array[Int], newstack: Array[Int] = Array(), i: Int = 0): Array[Int] =
  if i >= stack.length-1 then newstack
  else removeLastPointer_a(stack, newstack :+ stack(i), i+1)

private def sleep_increment(nums: Seq[Int], finalnum: Int = 0, i: Int = 0): Int =
  if i >= nums.length then finalnum
  else sleep_increment(nums, finalnum + nums(i), i+1)

def runSleep(line: String) =
  val start = findLineStart(line, 5)
  val sleep = mkstr(line, i = start)
  for num <- sleep do
    if !isInt(num) then closeTofu(s"Syntax error! Sleep instruction at line\n$line\nIncludes non-numeric elements!")
  val sleep_delay = sleep_increment(sleep.map(x => mkInt(x)))
  Thread.sleep(sleep_delay)

//for later: process function and arguments all in a vector, similar to exec
def goToFunc(line: String, fi: Seq[Int], fn: Seq[String]): Int =
  val name_start = findLineStart(line, 4)
  val name = getName(line, name_start)
  if name == "" then closeTofu(s"Syntax error! Function at line:\n$line\nDoes not have a name!")
  val i = findInList(name, fn)
  if i == -1 then
    closeTofu(s"Syntax error! Function of name '$name' at line:\n$line\nDoes not exist!")
  debugMessage(s"Calling function '$name', moved to line ${fi(i)+1}")
  fi(i)+1

def runStartupChecks(script: Seq[String]) =
  if !verifyFunctions(script) then closeTofu("Syntax error! All functions must be followed by the \"end\" keyword to define where they end!")
  if !verifyIfs(script) then closeTofu("Syntax error! All if statements must be followed by the \"endif\" keyword to define where they end!")
  if !verifyWhile(script) then closeTofu("Syntax error! All while loops must be followed by the \"endwhile\" keyword to define where they end!")
  if !verifyFor(script) then closeTofu("Syntax error! All foor loops must be followed by the \"endfor\" keyword to define where they end!")

  verifyConditions(script)
  verifyWhileCondition(script)
  verifyReadstr(script)
