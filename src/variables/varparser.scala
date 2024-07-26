package tofu.variables

import tofu.{debugMessage, debug_printSeq}
import tofu.parser.*
import tofu.reader.findLineStart
import tofu.runner.*

import scala.sys.process.*
import tofu.reader.readScript
import tofu.closeTofu

def getName_variable(line: String, i: Int, s: String = ""): String =
  if i >= line.length then
    closeTofu("Syntax error! Variable declaration at line\n$line\n lacks a colon \",\" to separate the name and value of the variable to be declared!")
    ""
  else if line(i) == ' ' || line(i) == '\t' || line(i) == ',' then s
  else getName_variable(line, i+1, s + line(i))

private def findValStart(line: String, i: Int): Int =
  if i >= line.length then -1
  else if line(i) == ',' then findLineStart(line, i+1)
  else findValStart(line, i+1)

def findVariableVal(line: String, i: Int): String =
  val valstart = findValStart(line, i)
  parseString(line, valstart)

def readVariable_class_safe(str: String): TofuVar = if str(0) == '$' then readVariable_class(str) else TofuVar(str)

def readVariable_str_safe(str: String): String = if str(0) == '$' then readVariable_str(str) else str

def readVariable_int_safe(str: String): Int = if str(0) == '$' then readVariable_int(str) else mkInt(str)

def readVariable_class(variable: String): TofuVar =
  val parsedvar = readvariable_generic(variable)
  TofuVar(parsedvar)

def readVariable_str(variable: String): String =
  val parsedvar = readvariable_generic(variable)
  val tofuvar = TofuVar(parsedvar)
  if tofuvar.vartype == variable_type.none then variable
  else
    val sval = tofuvar.valueToString()
    debugMessage(s"The string portion $variable points to a real variable, the returned value is $sval")
    sval

def readVariable_int(variable: String): Int =
  val tofuvar = TofuVar(readvariable_generic(variable))
  if tofuvar.vartype == variable_type.none then 0
  else
    val sval = tofuvar.value_int
    debugMessage(s"The string portion $variable points to a real variable, the returned value is $sval")
    sval

private def readvariable_generic(variable: String, i: Int = 0, v: String = ""): String =
  if i >= variable.length || variable(i) == ' ' then //maybe remove the whitespace check
    v
  else if i == 0 then readvariable_generic(variable, i+1, v)
  else readvariable_generic(variable, i+1, v + variable(i))
