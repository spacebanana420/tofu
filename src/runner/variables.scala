package tofu.runner

import tofu.{debugMessage, debug_printSeq}
import tofu.parser.*
import tofu.reader.findLineStart

import scala.sys.process.*
import tofu.reader.readScript
import tofu.closeTofu

private def tryGlobalVariable(variable: String): String =
  val num = mkInt(variable)
  if num >= 0 && script_args.length > num then script_args(num) else variable

private def readVariable(variable: String, i: Int = 0, v: String = ""): String =
  if i >= variable.length || variable(i) == ' ' then
    val var_index = findInList(v, var_name)
    if var_index == -1 then tryGlobalVariable(v)
    else var_val(var_index)
  else if i == 0 then readVariable(variable, i+1, v)
  else readVariable(variable, i+1, v + variable(i))

private def getVariableProperties(line: String, keyword: String): Vector[String] =
  val start = findLineStart(line, keyword.length)
  val name = getName_variable(line, start)
  val value = findVariableVal(line, start)
  if name.length == 0 then
    closeTofu(s"Syntax error! The variable declaration in line\n$line\nLacks a name!")
  if value.length == 0 then
    closeTofu(s"Syntax error! The variable declaration in line\n$line\nLacks a value!")
  if var_name.contains(name) then
    closeTofu(s"Variable declaration error at line: \n$line\n\nVariable of name $name already exists!")

  debugMessage(s"Assigning new variable of name $name and value $value")
  Vector(name, value)

def setVariable(line: String) =
  val variable = getVariableProperties(line, "set")
  val name = variable(0); val value = variable(1)
  var_name = var_name :+ name

  if value(0) == '$' then
    val realvalue = readVariable(value)
    if realvalue != value then debugMessage(s"Value $value for variable $name points to another variable, the returned value is $realvalue")
    var_val = var_val :+ realvalue
  else
    var_val = var_val :+ value

// def setVariable_int(line: String) =
//   val variable = getVariableProperties(line, "int")
//   val name = variable(0); val value = variable(1)
//   int_name = int_name :+ name
//
//   if value(0) == '$' then
//     val realvalue = readVariable(value)
//     if realvalue != value then debugMessage(s"Value $value for variable $name points to another variable, the returned value is $realvalue")
//     if num == -1 then closeTofu(s"Integer variable declaration error at line: \n$line\n\nValue $num is not an integer number!")
//     val num = mkInt(realvalue)
//   else
//     if num == -1 then closeTofu(s"Integer variable declaration error at line: \n$line\n\nValue $num is not an integer number!")
//     val num = mkInt(value)
//   int_val = int_val :+ num

private def mkInt(num: String): Int =
  try num.toInt
  catch case e: Exception => -1

private def math_mkInt(num: String): Int =
  try num.toInt
  catch case e: Exception => 0

private def sum_nums(nums: Seq[Int], i: Int = 0, sum: Int = 0): Int =
  if i >= nums.length then sum
  else sum_nums(nums, i+1, sum + nums(i))

private def sub_nums(nums: Seq[Int], i: Int = 0, sum: Int = 0): Int =
  if i >= nums.length then sum
  else sum_nums(nums, i+1, sum - nums(i))

private def mp_nums(nums: Seq[Int], i: Int = 0, sum: Int = 0): Int =
  if i >= nums.length then sum
  else sum_nums(nums, i+1, sum * nums(i))

private def div_nums(nums: Seq[Int], i: Int = 0, sum: Int = 0): Int =
  if i >= nums.length then sum
  else sum_nums(nums, i+1, sum / nums(i))

private def pow_nums(nums: Seq[Int], i: Int = 0, sum: Int = 0): Int =
  if i >= nums.length then sum
  else sum_nums(nums, i+1, sum ^ nums(i))

private def get_nums(line: String, i: Int, s: String = "", nums: Vector[Int] = Vector()): Vector[Int] =
  if i >= line.length then nums
  else if line(i) == ' ' then
    get_nums(line: String, i+1, "", nums :+ math_mkInt(s))
  else get_nums(line: String, i+1, s + line(i), nums)

def num_add(line: String): Int =
  val num_start = findLineStart(line, 3)
  val numbers = get_nums(line, num_start)
  sum_nums(numbers)

def num_sub(line: String): Int =
  val num_start = findLineStart(line, 3)
  val numbers = get_nums(line, num_start)
  sub_nums(numbers)

def num_multiply(line: String): Int =
  val num_start = findLineStart(line, 3)
  val numbers = get_nums(line, num_start)
  mp_nums(numbers)

def num_div(line: String): Int =
  val num_start = findLineStart(line, 3)
  val numbers = get_nums(line, num_start)
  div_nums(numbers)

def num_pow(line: String): Int =
  val num_start = findLineStart(line, 3)
  val numbers = get_nums(line, num_start)
  pow_nums(numbers)
