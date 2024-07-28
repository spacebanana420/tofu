package tofu.runner

import tofu.{debugMessage, debug_printSeq, closeTofu}
import tofu.variables.*
import tofu.parser.*
import tofu.math_parser.evaluateExpression

import scala.sys.process.*

def calculate(strs: Seq[String], line: String): Int =
  debug_printSeq(s"Math string elements (length ${strs.length}):", strs)
  if strs.length < 2 then closeTofu(s"Operator error! Calculation in line\n$line\nRequires at least a variable name and an expression!")
  val expression = strs.tail.mkString(" ")
  evaluateExpression(expression)

def calc_operator(e0: Int, e1: Int, o: String): Int =
  debugMessage(s"Calculating: $e0 $o $e1")
  o match
    case "+" => e0 + e1
    case "-" => e0 - e1
    case "*" => e0 * e1
    case "/" =>
      if e1 == 0 then closeTofu("Division error! You cannot divide a variable by 0!")
      e0 / e1
    case "%" =>
      if e1 == 0 then closeTofu("Division error! You cannot divide a variable by 0!")
      e0 % e1
    case _ => 0

private def calculate_class(e0: VarReader, e1: VarReader, o: VarReader): Int =
  val operator = o.raw_name
  val num0 = if e0.vartype != variable_type.integer then math_mkInt(e0.raw_name) else e0.value_int
  val num1 = if e1.vartype != variable_type.integer then math_mkInt(e1.raw_name) else e1.value_int
  calc_operator(num0, num1, operator)

private def calculate_class(e0: Int, e1: VarReader, o: VarReader): Int =
  val operator = o.raw_name
  val num1 = if e1.vartype != variable_type.integer then math_mkInt(e1.raw_name) else e1.value_int
  calc_operator(e0, num1, operator)

private def calculateSeq(s: Seq[VarReader], finalval: Int = 0, i: Int = 0, first: Boolean = true): Int =
  if i >= s.length then finalval
  else
    val newval =
      if first then
        debugMessage(s"Element 1: ${s(i).raw_name}; Element 2 ${s(i+2).raw_name}; Operator: ${s(i+1).raw_name}")
        calculate_class(s(i), s(i+2), s(i+1))
      else
        debugMessage(s"Element 1: $finalval; Element 2 ${s(i+1).raw_name}; Operator: ${s(i).raw_name}")
        calculate_class(finalval, s(i+1), s(i))
    if first then
      calculateSeq(s, newval, i+3, false)
    else
      calculateSeq(s, newval, i+2, false)

private def getMathStr(line: String, i: Int, math: String = "", copystr: Boolean = false): String =
  if i >= line.length then math
  else if line(i) == ',' then getMathStr(line, findLineStart(line, i+1), math, true)
  else if copystr then getMathStr(line, i+1, math :+ line(i), copystr)
  else getMathStr(line, i+1, math, copystr)

def calculateInt(line: String) =
  val start = findLineStart(line, 7)
  val parts = line.substring(start).split(",", 2).map(_.trim)
  if parts.length != 2 then
    closeTofu(s"Syntax error! Calculation in line\n$line\nRequires a variable name and an expression separated by a comma!")
  val name = parts(0)
  val expression = parts(1)
  
  val result = evaluateExpression(expression)
  declareInt(name, result)

private def math_mkInt(num: String): Int =
  if isInt(num) then num.toInt
  else 0
