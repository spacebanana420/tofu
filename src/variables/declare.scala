package tofu.variables

import tofu.{debugMessage, debug_printSeq}
import tofu.parser.*
import tofu.runner.*

import scala.sys.process.*
import tofu.reader.readScript
import tofu.closeTofu

def declareArray(name: String) =
  val index = findInList(name, var_name)
  if index != -1 then
    replaceArray_i(index, new TofuArray())
  else
    var_name = var_name :+ name
    var_type = var_type :+ variable_type.array
    array_val = array_val :+ new TofuArray()
    var_pointer = var_pointer :+ array_val.length-1
    
def declareString(name: String, value: String) =
  val index = findInList(name, var_name)
  if index != -1 then replaceString_i(index, value)
  else
    var_name = var_name :+ name
    var_type = var_type :+ variable_type.string
    string_val = string_val :+ value
    var_pointer = var_pointer :+ string_val.length-1

def declareInt(name: String, value: Int) =
  val index = findInList(name, var_name)
  if index != -1 then replaceInt_i(index, value)
  else
    var_name = var_name :+ name
    var_type = var_type :+ variable_type.integer
    int_val = int_val :+ value
    var_pointer = var_pointer :+ int_val.length-1

def replaceString(name: String, value: String) =
  val index = findInList(name, var_name)
  if index != -1 then replaceString_i(index, value)

def replaceString_i(i: Int, value: String) =
  if staticTypeSafetyCheck(i, variable_type.string) then
    string_val(var_pointer(i)) = value
  else
    string_val = string_val :+ value
    var_pointer(i) = string_val.length-1

def replaceInt(name: String, value: Int) =
  val index = findInList(name, var_name)
  if index != -1 then replaceInt_i(index, value)

def replaceInt_i(i: Int, value: Int) =
  if staticTypeSafetyCheck(i, variable_type.integer) then
    int_val(var_pointer(i)) = value
  else
    int_val = int_val :+ value
    var_pointer(i) = int_val.length-1

def replaceArray(name: String, value: TofuArray) =
  val index = findInList(name, var_name)
  if index != -1 then replaceArray_i(index, value)

def replaceArray_i(i: Int, value: TofuArray) =
  if staticTypeSafetyCheck(i, variable_type.integer) then
    array_val(var_pointer(i)) = value
  else
    array_val = array_val :+ value
    var_pointer(i) = array_val.length-1

def addToArray(name: String, value: Any) =
  val arrayvar = VarReader(name)
  if arrayvar.vartype == variable_type.array then
    array_val(arrayvar.pointer).add(value)
  else
    closeTofu(s"Error: Array $name not found or not an array type")

def getFromArray(name: String, index: Int): Any =
  val arrayvar = VarReader(name)
  if arrayvar.vartype == variable_type.array then
    array_val(arrayvar.pointer).get(index)
  else
    closeTofu(s"Error: Array $name not found or not an array type")

def replaceInArray(name: String, index: Int, value: Any) =
  val arrayvar = VarReader(name)
  if arrayvar.vartype != variable_type.array then closeTofu(s"Error: Array $name not found or not an array type")
  else
    array_val(arrayvar.pointer).replace

private def staticTypeSafetyCheck(i: Int, correct_type: variable_type): Boolean =
  if var_type(i) == correct_type then true
  else
    var_type(i) match
      case variable_type.string => freeOldString(var_pointer(i))
      case variable_type.integer => freeOldInt(var_pointer(i))
      case variable_type.array => freeOldArray(var_pointer(i))
      case _ => closeTofu("Internal error! Attempted to redeclare a variable of type none!")
    var_type(i) = correct_type
    false
    //closeTofu("Syntax error! Re-declaring a variable of a certain type with a value of another type is not possible!")

private def freeOldString(pointer: Int, new_values: Array[String] = Array(), i: Int = 0): Unit =
  if i >= string_val.length then
    string_val = new_values
  else
    if i == pointer then freeOldString(pointer, new_values, i+1)
    else freeOldString(pointer, new_values :+ string_val(i), i+1)

private def freeOldInt(pointer: Int, new_values: Array[Int] = Array(), i: Int = 0): Unit =
  if i >= int_val.length then
    int_val = new_values
  else
    if i == pointer then freeOldInt(pointer, new_values, i+1)
    else freeOldInt(pointer, new_values :+ int_val(i), i+1)

private def freeOldArray(pointer: Int, new_values: Array[TofuArray] = Array(), i: Int = 0): Unit =
  if i >= array_val.length then
    array_val = new_values
  else
    if i == pointer then freeOldArray(pointer, new_values, i+1)
    else freeOldArray(pointer, new_values :+ array_val(i), i+1)
