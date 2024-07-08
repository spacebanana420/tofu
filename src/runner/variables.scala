package tofu.runner

import tofu.{debugMessage, debug_printSeq}
import tofu.parser.*
import tofu.reader.findLineStart

import scala.sys.process.*
import tofu.reader.readScript
import tofu.closeTofu

private def readVariable(variable: String, i: Int = 0, v: String = ""): String =
  if i >= variable.length || variable(i) == ' ' then
    val var_index = findInList(v, var_name)
    if var_index == -1 then v else var_val(var_index)
  else if i == 0 then readVariable(variable, i+1, v)
  else readVariable(variable, i+1, v + variable(i))

private def mkInt(num: String): Int =
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
    get_nums(line: String, i+1, "", nums :+ mkInt(s))
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
