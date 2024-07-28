package tofu.variables

import tofu.{debugMessage, debug_printSeq}
import tofu.parser.*
import tofu.runner.*

import scala.sys.process.*
import tofu.reader.readScript
import tofu.closeTofu

def getName_variable(line: String, i: Int, s: String = ""): String =
  if i >= line.length then
    closeTofu("Syntax error! Variable declaration at line\n$line\n lacks a colon \",\" to separate the name and value of the variable to be declared!")
    ""
  else if line(i) == ' ' || line(i) == '\t' || line(i) == ',' then s
  else getName_variable(line, i+1, s + line(i))

private def findValStart(line: String, i: Int): Int =
  if i >= line.length then -1
  else if line(i) == ',' then findLineStart(line, i+1)
  else findValStart(line, i+1)

def findVariableVal(line: String, i: Int): String =
  val valstart = findValStart(line, i)
  parseString(line, valstart)

def parseArrayDeclaration(line: String): String =
  val start = findLineStart(line, 5)
  getName(line, start)

def parseArrayAddition(line: String): (String, String) =
  val start = findLineStart(line, 7)
  val parts = line.substring(start).split(",").map(x => x.trim())
  if parts.length < 2 then
    closeTofu(s"Syntax error in array addition: $line\n\nThe array name and/or the new value to append are missing!")
  (parts(0), parts(1))

def parseArrayAccess(line: String, keyword_length: Int): (String, String, Int) =
  val start = findLineStart(line, keyword_length)
  val parts = line.substring(start).split(",").map(x => x.trim())
  if parts.length < 3 then
    closeTofu(s"Syntax error in array access: $line\n\nThe array name, variable and/or the new value to get are missing!")
  (parts(0), parts(1), parts(2).toInt)

def findBlockEnd(s: Seq[String], startk: String, endk: String, i: Int, count: Int): Int =
  if i >= s.length then
    if count == 0 then i else -1 //-1 must not happen!!!!!
  else if count == 0 then i
  else if startsWith_strict(s(i), startk) then
    findBlockEnd(s, startk, endk, i+1, count+1)
  else if startsWith_strict(s(i), endk) then
    findBlockEnd(s, startk, endk, i+1, count-1)
  else findBlockEnd(s, startk, endk, i+1, count)
