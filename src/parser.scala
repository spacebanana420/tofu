package tofu.parser

import tofu.reader.findLineStart
import tofu.variables.{readVariable_str_safe, readVariable_int_safe}
import tofu.{debugMessage, debug_printSeq}

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

private def verifyCode(script: Seq[String], start_keyword: String, end_keyword: String, start_count: Int = 0, end_count: Int = 0, i: Int = 0): Boolean =
  if i >= script.length then start_count == end_count
  else
    val more_start = if startsWith_strict(script(i), start_keyword) then 1 else 0
    val more_end = if startsWith_strict(script(i), end_keyword) then 1 else 0
    verifyCode(script, start_keyword, end_keyword, start_count + more_start, end_count + more_end, i+1)

def verifyFunctions(script: Seq[String]): Boolean = verifyCode(script, "function", "end")
def verifyIfs(script: Seq[String]): Boolean = verifyCode(script, "if", "endif")
def verifyWhile(script: Seq[String]): Boolean = verifyCode(script, "while", "endwhile")

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
  val str = mkstr(line, i = start).map(x => readVariable_str_safe(x))
  debug_printSeq(s"From the string:\n$line\nThe parsed sequence is:", str)
  mkstr_raw(str)

def findBlockEnd(s: Seq[String], startk: String, endk: String, i: Int, count: Int): Int =
  if i >= s.length then
    if count == 0 then i else -1 //-1 must not happen!!!!!
  else if count == 0 then i
  else if startsWith_strict(s(i), startk) then
    findBlockEnd(s, startk, endk, i+1, count+1)
  else if startsWith_strict(s(i), endk) then
    findBlockEnd(s, startk, endk, i+1, count-1)
  else findBlockEnd(s, startk, endk, i+1, count)
