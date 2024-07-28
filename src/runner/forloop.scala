package tofu.runner

import tofu.variables.*, tofu.parser.*
import tofu.closeTofu
import tofu.debugMessage
import tofu.debug_printSeq

def processForLoop(line: String): (String, VarReader) =
  val start = findLineStart(line, 3)
  val elements = mkstr(line, i = start)
  if elements.length != 3 || elements(1) != "in" then
    closeTofu(s"Syntax error! For loop at line \n$line\nIs improperly written!")
  val list = VarReader(elements(2))
  val index = elements(0)
  if list.vartype != variable_type.array then
    closeTofu(s"Syntax error! Array ${elements(2)} in for loop at line \n$line\nIs not a declared array!")
  (index, list)

def forloop_create(line: String, linenum: Int): Boolean =
  val (index_name, forarray) = processForLoop(line)
  debugMessage(s"Creating for loop with index name of $index_name and array name of ${forarray.raw_name}")
  if array_val(forarray.pointer).size() == 0 then false
  else
    for_stack = for_stack :+ linenum
    for_amount = for_amount :+ array_val(forarray.pointer).size()
    for_max = for_max :+ array_val(forarray.pointer).size()

    val element = array_val(forarray.pointer).get(for_amount(for_amount.length-1) - for_max(for_max.length-1))
    element match
      case str: String => declareString(index_name, str)
      case integer: Int => declareInt(index_name, integer)
    for_amount(for_amount.length-1) -= 1
    true

def forloop_nextline(line: String) =
  debugMessage("Running another instance of the for loop")
  val (index_name, forarray) = processForLoop(line)
  val element = array_val(forarray.pointer).get(for_max(for_max.length-1) - for_amount(for_amount.length-1))
  element match
    case str: String => declareString(index_name, str)
    case integer: Int => declareInt(index_name, integer)
  for_amount(for_amount.length-1) -= 1
