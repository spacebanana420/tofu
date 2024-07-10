package tofu.parser

import tofu.reader.findLineStart

def startsWith(line: String, keyword: String, tmp: String = "", i: Int = 0): Boolean =
  if i >= line.length || keyword.length == tmp.length then
    tmp == keyword
  else startsWith(line, keyword, tmp + line(i), i+1)

def getName(line: String, i: Int, s: String = ""): String =
  if i >= line.length || line(i) == ' ' || line(i) == '\t' then s
  else getName(line, i+1, s + line(i))

// def getFullString(line: String, i: Int, s: String = ""): String =
//   if i >= line.length || line(i) == ' ' || line(i) == '\t' then s
//   else getName(line, i+1, s + line(i))

def getName_variable(line: String, i: Int, s: String = ""): String =
  if i >= line.length || line(i) == ' ' || line(i) == '\t' || line(i) == ',' then s
  else getName_variable(line, i+1, s + line(i))

private def findValStart(line: String, i: Int): Int =
  if i >= line.length then -1
  else if line(i) == ',' then findLineStart(line, i+1)
  else findValStart(line, i+1)

def findVariableVal(line: String, i: Int): String =
  val valstart = findValStart(line, i)
  getName(line, valstart)

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

// def getCheckpoints(script: Seq[String], c: Vector[Int] = Vector(), i: Int = 0): Vector[Int] =
//   if i >= script.length then c
//   else
//     val name = getName(script(i), 0)
//     if name(name.length-1) == ':' then
//       getCheckpoints(script, c :+ i, i+1)
//     else
//       getCheckpoints(script, c, i+1)
//
//
// def getCheckpointNames(script: Seq[String], indexes: Seq[Int], names: Vector[String] = Vector(), i: Int = 0): Vector[String] =
// if i >= indexes.length then names
// else
//   val li = indexes(i); val line = script(li)
//   val name_start = findLineStart(line, 4)
//   val name = getName(line, name_start)
//   getCheckpointNames(script, indexes, names :+ name, i+1)


def getVariables(script: Seq[String], indexes: Vector[Int] = Vector(), i: Int = 0): Vector[Int] =
  if i >= script.length then indexes
  else if startsWith(script(i), "set") then getVariables(script, indexes :+ i, i+1)
  else getVariables(script, indexes, i+1)

def getVariableNames(script: Seq[String], indexes: Seq[Int], names: Vector[String] = Vector(), i: Int = 0): Vector[String] =
  if i >= indexes.length then names
  else
    val li = indexes(i); val line = script(li)
    val name_start = findLineStart(line, 3)
    val name = getName_variable(line, name_start)
    getVariableNames(script, indexes, names :+ name, i+1)

def getVariableValues(script: Seq[String], indexes: Seq[Int]): Seq[String] =
  indexes.map(x => findVariableVal(script(x), 0))

private def verifyCode(script: Seq[String], start_keyword: String, end_keyword: String, strict: Boolean, start_count: Int = 0, end_count: Int = 0, i: Int = 0): Boolean =
  if i >= script.length then
    if strict then start_count == end_count else start_count <= end_count
  else
    val more_start = if startsWith(script(i), start_keyword) then 1 else 0
    val more_end = if startsWith(script(i), end_keyword) then 1 else 0
    verifyCode(script, start_keyword, end_keyword, strict, start_count + more_start, end_count + more_end, i+1)

def verifyFunctions(script: Seq[String]): Boolean = verifyCode(script, "function", "end", false)
def verifyIfs(script: Seq[String]): Boolean = verifyCode(script, "if", "endif", true)
