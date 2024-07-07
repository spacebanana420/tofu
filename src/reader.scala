package tofu.reader

import scala.io.Source

private def isComment(line: String): Boolean = line.length > 1 && line(0) == '/' && line(1) == '/'

private def removeStartingSpaces(line: String): String =
  val i = findLineStart(line)
  if i == -1 then line else mkstring(line, i)

private def mkstring(s: String, i: Int, news: String = ""): String =
  if i >= s.length then news
  else mkstring(s, i+1, news + s(i))

def findLineStart(line: String, i: Int = 0): Int =
  if i >= line.length then -1
  else if line(i) != ' ' && line(i) != '\t' then i
  else findLineStart(line, i+1)

def readScript(path: String): Vector[String] =
  val src = Source.fromFile(path)
  val cfg = src
    .getLines()
    .map(x => removeStartingSpaces(x))
    .filter(x => x.length > 0 && !isComment(x))
    .toVector
  src.close()
  cfg
