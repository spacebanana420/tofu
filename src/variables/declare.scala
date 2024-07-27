package tofu.variables

import tofu.{debugMessage, debug_printSeq}
import tofu.parser.*
import tofu.reader.findLineStart
import tofu.runner.*

import scala.sys.process.*
import tofu.reader.readScript
import tofu.closeTofu

def declareArray(name: String) =
  val index = findInList(name, var_name)
  if index != -1 then
    replaceArray(index, new TofuArray())
  else
    var_name = var_name :+ name
    var_type = var_type :+ variable_type.array
    array_val = array_val :+ new TofuArray()
    var_pointer = var_pointer :+ array_val.length-1
    
def declareString(name: String, value: String) =
  val index = findInList(name, var_name)
  if index != -1 then replaceString(index, value)
  else
    var_name = var_name :+ name
    var_type = var_type :+ variable_type.string
    string_val = string_val :+ value
    var_pointer = var_pointer :+ string_val.length-1

def declareInt(name: String, value: Int) =
  val index = findInList(name, var_name)
  if index != -1 then replaceInt(index, value)
  else
    var_name = var_name :+ name
    var_type = var_type :+ variable_type.integer
    int_val = int_val :+ value
    var_pointer = var_pointer :+ int_val.length-1

def replaceString(name: String, value: String) =
  val index = findInList(name, var_name)
  if index != -1 then
    string_val(var_pointer(index)) = value

def replaceString(i: Int, value: String) = string_val(var_pointer(i)) = value

def replaceInt(name: String, value: Int) =
  val index = findInList(name, var_name)
  if index != -1 then
    int_val(var_pointer(index)) = value

def replaceInt(i: Int, value: Int) = int_val(var_pointer(i)) = value

def replaceArray(name: String, value: TofuArray) =
  val index = findInList(name, var_name)
  if index != -1 then
    array_val(var_pointer(index)) = value

def replaceArray(i: Int, value: TofuArray) = array_val(var_pointer(i)) = value

def addToArray(name: String, value: Any) =
  val arrayvar = TofuVar(name)
  if arrayvar.vartype == variable_type.array then
    array_val(arrayvar.pointer).add(value)
  else
    closeTofu(s"Error: Array $name not found or not an array type")

def getFromArray(name: String, index: Int): Any =
  val arrayvar = TofuVar(name)
  if arrayvar.vartype == variable_type.array then
    array_val(arrayvar.pointer).get(index)
  else
    closeTofu(s"Error: Array $name not found or not an array type")

def replaceInArray(name: String, index: Int, value: Any) =
  val arrayvar = TofuVar(name)
  if arrayvar.vartype != variable_type.array then closeTofu(s"Error: Array $name not found or not an array type")
  else
    array_val(arrayvar.pointer).replace(value, index)

