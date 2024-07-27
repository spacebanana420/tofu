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

def parseArrayDeclaration(line: String): String =
  val start = findLineStart(line, 5)
  getName(line, start)

def parseArrayAddition(line: String): (String, String) =
  val start = findLineStart(line, 7)
  val parts = line.substring(start).split(",").map(x => x.trim())
  if parts.length < 2 then
    closeTofu(s"Syntax error in array addition: $line\n\nThe array name and/or the new value to append are missing!")
  (parts(0), parts(1))

def parseArrayAccess(line: String): (String, String, Int) =
  val start = findLineStart(line, 7)
  val parts = line.substring(start).split(",").map(x => x.trim())
  if parts.length < 3 then
    closeTofu(s"Syntax error in array access: $line\n\nThe array name, variable and/or the new value to get are missing!")
  (parts(0), parts(1), parts(2).toInt)

def findBlockEnd(s: Seq[String], startk: String, endk: String, i: Int, count: Int): Int =
  if i >= s.length then
    if count == 0 then i else -1 //-1 must not happen!!!!!
  else if count == 0 then i
  else if startsWith_strict(s(i), startk) then
    findBlockEnd(s, startk, endk, i+1, count+1)
  else if startsWith_strict(s(i), endk) then
    findBlockEnd(s, startk, endk, i+1, count-1)
  else findBlockEnd(s, startk, endk, i+1, count)
