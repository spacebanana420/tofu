package tofu.parser

import tofu.variables.VarReader
import tofu.{debugMessage, debug_printSeq}
import tofu.closeTofu

def findLineStart(line: String, i: Int = 0): Int =
  if i >= line.length then -1
  else if line(i) != ' ' && line(i) != '\t' then i
  else findLineStart(line, i+1)

def findInList(find: String, list: Seq[String], i: Int = 0): Int =
  if i >= list.length then -1
  else if find == list(i) then i
  else findInList(find, list, i+1)

def startsWith(line: String, keyword: String, tmp: String = "", i: Int = 0): Boolean =
  if i >= line.length || keyword.length == tmp.length then
    tmp == keyword
  else startsWith(line, keyword, tmp + line(i), i+1)

def startsWith_strict(line: String, keyword: String, tmp: String = "", i: Int = 0): Boolean =
  if i >= line.length then
    tmp == keyword
  else if keyword == tmp && (line(i) == ' ' || line(i) == '\t') then
    true
  else startsWith_strict(line, keyword, tmp + line(i), i+1)

def getName(line: String, i: Int, s: String = ""): String =
  if i >= line.length || line(i) == ' ' || line(i) == '\t' then s
  else getName(line, i+1, s + line(i))

def readRaw(line: String, i: Int, s: String = ""): String =
  if i >= line.length then s
  else readRaw(line, i+1, s + line(i))

def getFuncIndexes(script: Seq[String], indexes: Vector[Int] = Vector(), i: Int = 0): Vector[Int] =
  if i >= script.length then indexes
  else if startsWith(script(i), "function") then getFuncIndexes(script, indexes :+ i, i+1)
  else getFuncIndexes(script, indexes, i+1)

def getFuncNames(script: Seq[String], indexes: Seq[Int], names: Vector[String] = Vector(), i: Int = 0): Vector[String] =
  if i >= indexes.length then names
  else
    val li = indexes(i); val line = script(li)
    val name_start = findLineStart(line, 8)
    val name = getName(line, name_start)
    getFuncNames(script, indexes, names :+ name, i+1)

def mkstr_raw(in: Seq[String], str: String = "", i: Int = 0): String =
  if i >= in.length then str
  else if i == in.length-1 then mkstr_raw(in, str + s"${in(i)}", i+1)
  else mkstr_raw(in, str + s"${in(i)} ", i+1)

private def add_to_seq(str: String, s: Vector[String]): Vector[String] =
  if str.length == 0 then s else s :+ str

def mkstr(line: String, s_seq: Vector[String] = Vector(), arg: String = "", i: Int = 0, ignore_spaces: Boolean = false): Vector[String] =
  if i >= line.length then
    val final_seq = add_to_seq(arg, s_seq)
    final_seq
  else if line(i) == '"' then
    mkstr(line, s_seq, arg, i+1, !ignore_spaces)
  else if line(i) == ' ' || line(i) == '\t' && !ignore_spaces then
    mkstr(line, add_to_seq(arg, s_seq), "", i+1, ignore_spaces)
  else
    mkstr(line, s_seq, arg + line(i), i+1, ignore_spaces)

def parseString(line: String, start: Int): String =
  val str = mkstr(line, i = start).map(x => VarReader(x).valueToString())
  debug_printSeq(s"From the string:\n$line\nThe parsed sequence is:", str)
  mkstr_raw(str)

def parseString_raw(line: String, start: Int): String =
  val str = mkstr(line, i = start)
  debug_printSeq(s"From the string:\n$line\nThe parsed sequence is:", str)
  mkstr_raw(str)

def parseLocate(line: String): (Int, Int) =
  val start = findLineStart(line, 6)
  val parts = line.substring(start).split(",").map(_.trim)
  if parts.length != 2 then
    closeTofu(s"Syntax error in locate: $line\n\nExpected format: locate x, y")
  (parts(0).toInt, parts(1).toInt)

def parseColor(line: String): (String, String) =
  val start = findLineStart(line, 5)
  val parts = line.substring(start).split(",").map(_.trim)
  if parts.length != 2 then
    closeTofu(s"Syntax error in color: $line\n\nExpected format: color text/background, color")
  (parts(0), parts(1))
