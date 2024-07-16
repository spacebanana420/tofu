package tofu.parser

import tofu.reader.findLineStart
import tofu.runner.readVariable_safe
import tofu.debug_printSeq

def startsWith(line: String, keyword: String, tmp: String = "", i: Int = 0): Boolean =
  if i >= line.length || keyword.length == tmp.length then
    tmp == keyword
  else startsWith(line, keyword, tmp + line(i), i+1)

def startsWith_strict(line: String, keyword: String, tmp: String = "", i: Int = 0): Boolean =
  if i >= line.length then
    tmp == keyword
  else if keyword.length == tmp.length then
    tmp == keyword && (line(i) == ' ' || line(i) == '\t')
  else startsWith_strict(line, keyword, tmp + line(i), i+1)

// def exactMatch(line: String, keyword: String, tmp: String = "", i: Int = 0): Boolean =
//   if i >= line.length then
//     tmp == keyword
//   else if line(i) != ' ' && line(i) != '\t' then
//     exactMatch(line, keyword, tmp + line(i), i+1)
//   else exactMatch(line, keyword, tmp, i+1)

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

private def verifyCode(script: Seq[String], start_keyword: String, end_keyword: String, strict: Boolean, start_count: Int = 0, end_count: Int = 0, i: Int = 0): Boolean =
  if i >= script.length then
    if strict then start_count == end_count else start_count <= end_count
  else
    val more_start = if startsWith(script(i), start_keyword) then 1 else 0
    val more_end = if startsWith(script(i), end_keyword) then 1 else 0
    verifyCode(script, start_keyword, end_keyword, strict, start_count + more_start, end_count + more_end, i+1)

def verifyFunctions(script: Seq[String]): Boolean = verifyCode(script, "function", "end", false)
def verifyIfs(script: Seq[String]): Boolean = verifyCode(script, "if", "endif", true)

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
  val str = mkstr(line, i = start).map(x => readVariable_safe(x))
  debug_printSeq(s"From the string:\n$line\nThe parsed sequence is:", str)
  mkstr_raw(str)
