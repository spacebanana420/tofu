package tofu.runner

import tofu.{debugMessage, debug_printSeq}
import tofu.parser.*
import tofu.reader.findLineStart

import scala.sys.process.*
import tofu.reader.readScript
import tofu.closeTofu

enum variable_type:
  case string, integer, none

class TofuVar(name: String):
  val input = name
  val index = findInList(name, var_name)
  val vartype = getType()
  val pointer = getPointer()

  val value_str = if vartype == variable_type.string then getValue_str() else ""
  val value_int = if vartype == variable_type.integer then getValue_int() else 0
  val value =
    if vartype == variable_type.none then ""
    else if vartype == variable_type.integer then value_int
    else value_str

  def valueToString(): String =
    if vartype == variable_type.string then value_str
    else value_int.toString()

  private def getType(): variable_type =
    if index == -1 then variable_type.none else var_type(index)

  private def getPointer(): Int =
    if index == -1 then -1 else var_pointer(index)


  private def getValue_str(): String = string_val(pointer)
  private def getValue_int(): Int = int_val(pointer)
end TofuVar

private def getVariableProperties(line: String, keyword: String): Vector[String] =
  val start = findLineStart(line, keyword.length)
  val name = getName_variable(line, start)
  val value = findVariableVal(line, start)
  if name.length == 0 then
    closeTofu(s"Syntax error! The variable declaration in line\n$line\nLacks a name!")
  if value.length == 0 then
    closeTofu(s"Syntax error! The variable declaration in line\n$line\nLacks a value!")
//   if var_name.contains(name) then
//     closeTofu(s"Variable declaration error at line: \n$line\n\nVariable of name $name already exists!")
  Vector(name, value)

// def setVariable(line: String) =
//   val variable = getVariableProperties(line, "set")
//   val name = variable(0); val value = variable(1)
//
//   debugMessage(s"Assigning new variable of name $name and value $value")
//   var_name = var_name :+ name
//   var_val = var_val :+ value

def setVariable_str(line: String) =
  val variable = getVariableProperties(line, "string")
  val name = variable(0); val value = variable(1)

  debugMessage(s"Assigning new variable of name $name and value $value")
  declareString(name, value)

def setVariable_int(line: String) =
  val variable = getVariableProperties(line, "int")
  val name = variable(0); val value = variable(1)
  val value_num = mkInt(value)

  if value_num == -1 && value != "-1" then closeTofu(s"Syntax error! Assigning a string value on an integer variable declaration at line\n$line\nIs not possible!")
  debugMessage(s"Assigning new variable of name $name and value $value")
  declareInt(name, value_num)

def isInt(value: String, i: Int = 0): Boolean =
  val digits = Vector('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
  if i >= value.length then true
  else if !digits.contains(value(i)) && (i != 0 || value(i) != '-') then false
  else isInt(value, i+1)

def mkInt(num: String): Int =
  if isInt(num) then num.toInt
  else -1
