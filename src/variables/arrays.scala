package tofu.variables

import tofu.{debugMessage, debug_printSeq}
import tofu.parser.*
import tofu.runner.*

import scala.sys.process.*
import tofu.reader.readScript
import tofu.closeTofu

class TofuArray:
  private var elements: Array[Any] = Array()

  def add(value: Any) = elements = elements :+ value
  def get(index: Int): Any =
    if index >= elements.length then closeTofu(s"Array read error! Array of size ${elements.length} is too small for index $index!")
    elements(index)
  def replace(value: Any, i: Int) =
    if i >= elements.length then closeTofu(s"Array read error! Array of size ${elements.length} is too small for index $i!")
    elements(i) = value

  def size(): Int = elements.size
  def toArray(): Array[Any] = elements

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
