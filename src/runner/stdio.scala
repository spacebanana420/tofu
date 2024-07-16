package tofu.runner

import tofu.{debugMessage, debug_printSeq}
import tofu.parser.*
import tofu.reader.findLineStart

import scala.sys.process.*
import tofu.reader.readScript
import tofu.closeTofu

private def mkPrintStr(line: String, start: Int): String = //what if the user really wants multiple spaces?
  debugMessage(s"Parsing print line:\n$line")
  parseString(line, start)

// private def getPrintMsg(line: String, i: Int, msg: String = ""): String = //variable check should be done at every whitespace
//   if i >= line.length then
//     if msg(0) == '$' then readVariable(msg)
//     else msg
//   else getPrintMsg(line, i+1, msg + line(i))

def printArg(line: String) =
  val msg_start = findLineStart(line, 5)
  debugMessage(s"Printing parsing started at $msg_start")
//   val msg = getPrintMsg(line, msg_start)
  val msg = mkPrintStr(line, msg_start)
  println(msg)

// def read(line: String) =
