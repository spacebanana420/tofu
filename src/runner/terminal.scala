package tofu.terminal

import scala.io.AnsiColor

object TerminalOps:
  def locate(x: Int, y: Int): String = s"\u001b[${y};${x}H"
  
  def clearScreen(): String = s"\u001B[3J\u001B[1J\u001B[H"
  
  def getColor(color: String): String = color match
    case "red" => AnsiColor.RED
    case "green" => AnsiColor.GREEN
    case "blue" => AnsiColor.BLUE
    case "yellow" => AnsiColor.YELLOW
    case "magenta" => AnsiColor.MAGENTA
    case "cyan" => AnsiColor.CYAN
    case "white" => AnsiColor.WHITE
    case "black" => AnsiColor.BLACK
    case "blink" => AnsiColor.BLINK
    case "bold" => AnsiColor.BOLD
    case "invisible" => AnsiColor.INVISIBLE
    case "reset" => AnsiColor.RESET
    case "reversed" => AnsiColor.REVERSED
    case "underlined" => AnsiColor.UNDERLINED
    case _ => throw new IllegalArgumentException(s"Unknown color '$color'")
  
  def getBackgroundColor(color: String): String = color match
    case "red" => AnsiColor.RED_B
    case "green" => AnsiColor.GREEN_B
    case "blue" => AnsiColor.BLUE_B
    case "yellow" => AnsiColor.YELLOW_B
    case "magenta" => AnsiColor.MAGENTA_B
    case "cyan" => AnsiColor.CYAN_B
    case "white" => AnsiColor.WHITE_B
    case "black" => AnsiColor.BLACK_B
    case "blink" => AnsiColor.BLINK
    case "bold" => AnsiColor.BOLD
    case "invisible" => AnsiColor.INVISIBLE
    case "reset" => AnsiColor.RESET
    case "reversed" => AnsiColor.REVERSED
    case "underlined" => AnsiColor.UNDERLINED
    case _ => throw new IllegalArgumentException(s"Unknown background color '$color'")
