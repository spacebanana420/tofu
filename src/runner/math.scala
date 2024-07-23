package tofu.runner

import tofu.{debugMessage, debug_printSeq, closeTofu}
import tofu.parser.*
import tofu.reader.findLineStart

import scala.sys.process.*

def calc_operator(e0: Int, e1: Int, o: String): Int =
  debugMessage(s"Calculating: $e0 $o $e1")
  o match
    case "+" => e0 + e1
    case "-" => e0 - e1
    case "*" => e0 * e1
    case "/" => e0 / e1
    case "%" => e0 % e1
    case _ => 0

private def calculate_class(e0: TofuVar, e1: TofuVar, o: TofuVar): Int =
  val operator = o.input
  val num0 = if e0.vartype != variable_type.integer then math_mkInt(e0.input) else e0.value_int
  val num1 = if e1.vartype != variable_type.integer then math_mkInt(e1.input) else e1.value_int
  calc_operator(num0, num1, operator)

private def calculate_class(e0: Int, e1: TofuVar, o: TofuVar): Int =
  val operator = o.input
  val num1 = if e1.vartype != variable_type.integer then math_mkInt(e1.input) else e1.value_int
  calc_operator(e0, num1, operator)

private def calculateSeq(s: Seq[TofuVar], finalval: Int = 0, i: Int = 0, first: Boolean = true): Int =
  if i >= s.length then finalval
  else
    val newval =
      if first then
        debugMessage(s"Element 1: ${s(i).input}; Element 2 ${s(i+2).input}; Operator: ${s(i+1).input}")
        calculate_class(s(i), s(i+2), s(i+1))
      else
        debugMessage(s"Element 1: $finalval; Element 2 ${s(i+1).input}; Operator: ${s(i).input}")
        calculate_class(finalval, s(i+1), s(i))
    if first then
      calculateSeq(s, newval, i+3, false)
    else
      calculateSeq(s, newval, i+2, false)

def calculate(strs: Seq[String], line: String): Int =
  debug_printSeq(s"Math string elements (length ${strs.length}):", strs)
  if strs.length < 3 then closeTofu(s"Operator error! Calculation in line\n$line\nRequires at least 2 elements and 1 operator!")
  if strs.length % 2 != 1 then closeTofu(s"Operator error! Calculation in line\n$line\nIs missing an element or operator")
  val classes = strs.map(x => readVariable_class_safe(x))
  calculateSeq(classes)

private def getMathStr(line: String, i: Int, math: String = "", copystr: Boolean = false): String =
  if i >= line.length then math
  else if line(i) == ',' then getMathStr(line, findLineStart(line, i+1), math, true)
  else if copystr then getMathStr(line, i+1, math :+ line(i), copystr)
  else getMathStr(line, i+1, math, copystr)

def calculateInt(line: String) =
  val start = findLineStart(line, 7)
  val name = getName_variable(line, i = start)
  val mathstr = getMathStr(line, start)

  val result = calculate(mkstr(mathstr), line)
  declareInt(name, result)

private def math_mkInt(num: String): Int =
  if isInt(num) then num.toInt
  else 0
