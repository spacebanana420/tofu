package tofu.parser

import tofu.{debugMessage, debug_printSeq, closeTofu}
import tofu.runner.conditionElements
import tofu.variables.getVariableProperties

private def verifyCode(script: Seq[String], start_keyword: String, end_keyword: String, start_count: Int = 0, end_count: Int = 0, i: Int = 0): Boolean =
  if i >= script.length then start_count == end_count
  else
    val more_start = if startsWith_strict(script(i), start_keyword) then 1 else 0
    val more_end = if startsWith_strict(script(i), end_keyword) then 1 else 0
    verifyCode(script, start_keyword, end_keyword, start_count + more_start, end_count + more_end, i+1)

def verifyFunctions(script: Seq[String]): Boolean = verifyCode(script, "function", "end")
def verifyIfs(script: Seq[String]): Boolean = verifyCode(script, "if", "endif")
def verifyWhile(script: Seq[String]): Boolean = verifyCode(script, "while", "endwhile")
def verifyFor(script: Seq[String]): Boolean = verifyCode(script, "for", "endfor")

private def verifyCondition(line: String, isIF: Boolean): Boolean =
  val start = if isIF then findLineStart(line, 2) else findLineStart(line, 5) //for while loops
  if start == -1 || conditionElements(line, start).length == 0 then false
  else true

def verifyConditions(script: Seq[String]) =
  for s <- script do
    if startsWith_strict(s, "if") && !verifyCondition(s, true) then
      closeTofu(s"Syntax error! If statement condition at line\n$s\nLacks elements and an operator to compare them to!")

def verifyWhileCondition(script: Seq[String]) =
  for s <- script do
    if startsWith_strict(s, "while") && !verifyCondition(s, false) then
      closeTofu(s"Syntax error! While loop condition at line\n$s\nLacks elements and an operator to compare them to!")

def verifyReadstr(script: Seq[String]) =
  for s <- script do
    if startsWith_strict(s, "readstr") && findLineStart(s, 7) == -1 then
      closeTofu(s"Syntax error! User input read at line\n$s\nLacks a name attribute to give to the variable it declares!")
