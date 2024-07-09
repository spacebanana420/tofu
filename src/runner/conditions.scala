package tofu.runner

import tofu.{debugMessage, debug_printSeq}
import tofu.parser.*
import tofu.reader.findLineStart

import scala.sys.process.*
import tofu.reader.readScript
import tofu.closeTofu

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

def findEndIF(script: Seq[String], i: Int): Int =
  if i >= script.length then -1
  else if startsWith(script(i), "endif") then i
  else findEndIF(script, i+1)

def checkCondition(line: String): Boolean =
  val start = findLineStart(line, 2)
  val elements = conditionElements(line, start)
  val e0 = if elements(0)(0) == '$' then readVariable(elements(0)) else elements(0)
  val e1 = if elements(2)(0) == '$' then readVariable(elements(2)) else elements(2)
  debugMessage(s"Running if statement: [element 0] $e0 [element 1] $e1 [operator] ${elements(1)}")
  if elements.length == 1 then true
  else
    val condition = elements(1) match
      case "==" => e0 == e1
      case "!=" => e0 != e1
      case ">" => e0 > e1
      case ">=" => e0 >= e1
      case "<" => e0 < e1
      case "<=" => e0 <= e1
      case _ => false
    debugMessage(s"Condition returned $condition")
    condition
