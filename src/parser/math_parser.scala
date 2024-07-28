package tofu.math_parser

import tofu.{debugMessage, debug_printSeq}
import tofu.variables.*

// TO-DO BITWISE:
// AND (&) -> XOR (^) -> OR (|)

// Defines a datatype Token with all subclasses defined
sealed trait Token
case class NumberToken(value: Int) extends Token        // 1,2,3,4
case class VariableToken(name: String) extends Token    // variable
case class OperatorToken(op: Char) extends Token        // +,-,etc...
case class ParenToken(paren: Char) extends Token        // ( & )

// "($variable+1)" -> (()+(variable)+(+)+(1)+())
def tokenize(expr: String): List[Token] =
  // main function (recurssive holy shit)
  def tokenizeHelper(remaining: List[Char], current: String, acc: List[Token]): List[Token] = remaining match
    case Nil => //last character of the string
      if (current.nonEmpty) tokenizeHelper(Nil, "", acc :+ parseToken(current)) // parse last token
      else acc // all done!
    case head :: tail if head.isWhitespace => // if current character is whitespace
      if (current.nonEmpty) tokenizeHelper(tail, "", acc :+ parseToken(current)) // parse current token
      else tokenizeHelper(tail, "", acc) // continue to next token
    case head :: tail if "+-*/()".contains(head) => // if current character is operator
      val newAcc = if (current.nonEmpty) acc :+ parseToken(current) else acc // parse current token if available
      tokenizeHelper(tail, "", newAcc :+ parseToken(head.toString)) // continue to next token
    case head :: tail => tokenizeHelper(tail, current + head, acc) // add to current string (maybe number or variable)

  def parseToken(s: String): Token = s match
    case "+" | "-" | "*" | "/" => OperatorToken(s.charAt(0))
    case "(" => ParenToken('(')
    case ")" => ParenToken(')')
    case s if s.startsWith("$") => VariableToken(s)
    case s => NumberToken(s.toInt)

  tokenizeHelper(expr.toList, "", Nil)

// evaluates token to an int, follows PEMDAS!
def parseExpression(tokens: List[Token]): Int =
  def parseExpr(remaining: List[Token]): (Int, List[Token]) = parseAddSub(remaining)

  def parseAddSub(remaining: List[Token]): (Int, List[Token]) =
    // PEDMAS logic
    var (left, newRemaining) = parseMulDiv(remaining)
    var currentRemaining = newRemaining

    // while still parsing add/sub
    while (currentRemaining.headOption.exists {
      case OperatorToken('+') | OperatorToken('-') => true
      case _ => false
    }) do
      val op = currentRemaining.head.asInstanceOf[OperatorToken].op
      val (right, nextRemaining) = parseMulDiv(currentRemaining.tail)
      left = if (op == '+') left + right else left - right
      currentRemaining = nextRemaining
    (left, currentRemaining)

  def parseMulDiv(remaining: List[Token]): (Int, List[Token]) =
    var (left, newRemaining) = parseFactor(remaining)
    var currentRemaining = newRemaining

    // while still parsing mul/div
    while (currentRemaining.headOption.exists {
      case OperatorToken('*') | OperatorToken('/') => true
      case _ => false
    }) do
      val op = currentRemaining.head.asInstanceOf[OperatorToken].op
      val (right, nextRemaining) = parseFactor(currentRemaining.tail)
      left = if (op == '*') left * right else left / right
      currentRemaining = nextRemaining
    (left, currentRemaining)

  // parse variable or immediate value
  def parseFactor(remaining: List[Token]): (Int, List[Token]) =
    remaining match
      case NumberToken(n) :: tail => 
        debugMessage(s"Parsed number: $n")
        (n, tail)
      case VariableToken(name) :: tail => 
        val value = VarReader(name).value_int
        debugMessage(s"Parsed variable: $name with value $value")
        (value, tail)
      case ParenToken('(') :: tail =>
        debugMessage(s"Start of expression in parentheses")
        // if parenthesis, parse new expression
        val (result, afterExpr) = parseExpr(tail)
        afterExpr.headOption match
          case Some(ParenToken(')')) =>
            debugMessage(s"End of expression in parentheses")
            (result, afterExpr.tail)
          case _ => 
            throw new RuntimeException("Mismatched parentheses: no closing parenthesis found")
      case _ => 
        throw new RuntimeException(s"Unexpected token: ${remaining.headOption.getOrElse("None")}")

  // returns the int
  parseExpr(tokens)._1

def evaluateExpression(expr: String): Int =
  val tokens = tokenize(expr)
  debugMessage(s"Tokenized expression: $tokens")
  parseExpression(tokens)
