package tofu.runner

import tofu.{debugMessage, debug_printSeq}
import tofu.parser.*
import tofu.reader.readScript
import tofu.variables.*
import tofu.closeTofu

import scala.sys.process.*
import scala.io.StdIn.readLine

private def mkPrintStr(line: String, start: Int): String = //what if the user really wants multiple spaces?
  debugMessage(s"Parsing print line:\n$line")
  parseString(line, start)

def printArg(line: String) =
  val msg_start = findLineStart(line, 5)
  debugMessage(s"Printing parsing started at $msg_start")
  val msg = mkPrintStr(line, msg_start)
  println(msg)

private def read_generic(line: String, keyword_length: Int): (String, String) =
  val start = findLineStart(line, keyword_length)
  val name = parseString_raw(line, start)
  val in = readLine()
  (name, in)

def read_string(line: String) =
  val properties = read_generic(line, 7) //readstr
  declareString(properties(0), properties(1))
