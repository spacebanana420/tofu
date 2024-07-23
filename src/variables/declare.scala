package tofu.runner

import tofu.{debugMessage, debug_printSeq}
import tofu.parser.*
import tofu.reader.findLineStart

import scala.sys.process.*
import tofu.reader.readScript
import tofu.closeTofu


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
