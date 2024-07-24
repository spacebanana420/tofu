package tofu.runner

import tofu.{debugMessage, debug_printSeq}
import tofu.variables.*
import tofu.parser.*
import tofu.reader.findLineStart

import scala.sys.process.*
import tofu.reader.readScript
import tofu.closeTofu

def findEndIF(script: Seq[String], i: Int): Int =
  if i >= script.length then -1
  else if startsWith(script(i), "endif") then i
  else findEndIF(script, i+1)

def findEndWhile(script: Seq[String], i: Int): Int =
  if i >= script.length then -1
  else if startsWith(script(i), "endwhile") then i
  else findEndWhile(script, i+1)


def checkCondition(line: String, isIF: Boolean): Boolean =
  val start = if isIF then findLineStart(line, 2) else findLineStart(line, 5) //for while loops
  val elements = conditionElements(line, start)
  elements.length match
    case 0 =>
      closeTofu(s"Syntax error! Condition at line\n$line\nlacks elements and an operator to compare them to!")
      false
    case 1 =>
      debugMessage("If statement has only 1 element, returning true if variable exists or is not a variable")
      val variable = readVariable_class_safe(elements(0))
      elements(0)(0) != '$' || variable.vartype != variable_type.none
    case 2 =>
      if elements(1) == "exists" then
        readVariable_class_safe(elements(0)).vartype != variable_type.none
      else if elements(1) == "!exists" then
        readVariable_class_safe(elements(0)).vartype == variable_type.none
      else false
    case _ =>
      val e0 = readVariable_class_safe(elements(0))
      val e1 = readVariable_class_safe(elements(2))
      debugMessage(s"Running if statement: [element 0] ${elements(0)} [element 1] ${elements(2)} [operator] ${elements(1)}")
      val condition = elements(1) match
        case "==" => compare_str(e0, e1, true)
        case "!=" => compare_str(e0, e1, false)
        case "contains" => compare_str_contains(e0, e1, true)
        case "!contains" => compare_str_contains(e0, e1, false)
        case _ => compare_int(e0, e1, elements(1))
      debugMessage(s"Condition returned $condition")
      condition

private def addElement(x: Vector[String], y: String): Vector[String] =
  if y.length == 0 then x else x:+y

private def conditionElements(line: String, i: Int, elements: Vector[String] = Vector(), s: String = "", ignore_spaces: Boolean = false): Vector[String] =
  if i >= line.length || elements.length == 3 then addElement(elements, s)
  else if line(i) == ' ' || line(i) == '\t' && !ignore_spaces then
    conditionElements(line, i+1, elements :+ s, "", ignore_spaces)
  else if line(i) == '"' then
    conditionElements(line, i+1, elements, s, !ignore_spaces)
  else
    conditionElements(line, i+1, elements, s + line(i), ignore_spaces)

private def compare_str_contains(e0: TofuVar, e1: TofuVar, equals: Boolean): Boolean =
  val str0 =
    if e0.vartype == variable_type.none then e0.input
    else e0.valueToString()

  val str1 =
    if e1.vartype == variable_type.none then e1.input
    else e1.valueToString()

  if equals then str0.contains(str1)
  else !str0.contains(str1)

private def compare_str(e0: TofuVar, e1: TofuVar, equals: Boolean): Boolean =
  val str0 =
    if e0.vartype == variable_type.none then e0.input
    else e0.valueToString()

  val str1 =
    if e1.vartype == variable_type.none then e1.input
    else e1.valueToString()

  if equals then str0 == str1
  else str0 != str1

private def compare_int(e0: TofuVar, e1: TofuVar, operator: String): Boolean =
  val int0 =
    if e0.vartype == variable_type.none then condition_mkint(e0.input)
    else if e0.vartype == variable_type.integer then
      e0.value_int
    else e0.valueToString().length
  val int1 =
    if e1.vartype == variable_type.none then condition_mkint(e1.input)
    else if e1.vartype == variable_type.integer then
      e1.value_int
    else e1.valueToString().length

  operator match
    case ">" => int0 > int1
    case ">=" => int0 >= int1
    case "<" => int0 < int1
    case "<=" => int0 <= int1
    case _ => false

private def condition_mkint(in: String): Int =
  val i = mkInt(in)
  if i == -1 && in != "-1" then in.length else i
