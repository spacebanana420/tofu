package tofu.runner

import tofu.{debugMessage, debug_printSeq}
import tofu.parser.*
import tofu.reader.findLineStart

import scala.sys.process.*
import tofu.reader.readScript
import tofu.closeTofu

private def conditionElements(line: String, i: Int, elements: Vector[String] = Vector(), s: String = "", ignore_spaces: Boolean = false): Vector[String] =
  if i >= line.length || elements.length == 3 then elements
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
  if elements.length == 1 then true
  else elements(1) match
    case "==" => elements(0) == elements(2)
    case "!=" => elements(0) != elements(2)
    case ">" => elements(0) > elements(2)
    case ">=" => elements(0) >= elements(2)
    case "<=" => elements(0) <= elements(2)
    case "<" => elements(0) < elements(2)
    case _ => false
