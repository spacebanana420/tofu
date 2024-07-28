# Tofu's instruction set

Tofu's language style is somewhat inspired by CPU architecture instruction sets. Tofu instructions are pretty straightforward, and are used for when we want the script to perform a task.

### Available instructions

* string
* int
* calcint
* readstr
* array
* arrget
* arradd
* arreplace
* arrlen
* call
* return
* exec
* stop
* sleep
* break
* print
* clear
* color
* locate

## string

`string` assigns a variable of type `string` (text-based) which can be read later by many instructions. It follows the syntax `string NAME, VALUE`. An assigned variable can be read by typing its name, followed by the character '$'.

Example:

```
string message, I love Tofu

print $message
```

The line `print $message` will print "I love Tofu". Variables, just like functions, have no privacy. This means that, once the variable is set, any part of the script from this point onwards can access it.

You can also use the value of an already-existing variable to set the new one:

```
string variable1, baguette
string variable2, $variable1

print $variable2
```

This will print "baguette".

## int

`int` assigns a variable of type `int` (numeric) which can be read later by many instructions. It follows the syntax `int NAME, VALUE`. An assigned variable can be read by typing its name, followed by the character '$'.

Example:

```
int message, 234

print $message
```

The line `print $message` will print "234". Variables, just like functions, have no privacy. This means that, once the variable is set, any part of the script from this point onwards can access it.

You can also use the value of an already-existing variable to set the new one:

```
string variable1, 4235
string variable2, $variable1

print $variable2
```

This will print "4235".

## calcint

Performs arithmetic calculations and assigns the result as a variable

Example:

```
int a, 45
calcint result, 3 + 5 + $a - 1
print $result
```

Available operators:
* `+`
* `-`
* `*`
* `/`
* `%`

## readstr

Reads user input and declares the value as a string variable:

```
readstr variable1
print $variable1
```

## array, arrget, arradd, arreplace and arrlen

You can declare arrays as a container to multiple elements of integer or string value:

```
//Create a new array or replace if one named "info" already exists
array info
//Add an integer element of "45" to info
arradd info, 45
//Add a string element of "random text" to info
arradd info, random text

//Read the element 0 (the first element) from "info" and declare a new variable with its value
arrget info, variable1, 0

//Print variable1, which is an integer of value "45"
print $variable1

//Changes the value of the first element of "info", re-declares variable1 and prints the new value
arreplace info, 500, 0
arrget info, variable1, 0
print $variable1
```

You can also obtain the length of an array with `arrlen`:

```
array emptyarray
arrlen emptyarray, variable1

print $variable1
```

This code obtains the length of the array `emptyarray` and declares it as an integer variable, then prints it. Since the array is created but no element is given to it, its length value is `0`.

The value is determined by the user at runtime, but the name of the variable must be given.

## call

`call` executes a function if it exists. It follows the syntax `call NAME`.

Example:

```
function printstuff
  print Fried tofu is yummy
end

call printstuff
```
This script executes the function "printstuff", and as a result it prints "Fried tofu is yummy".

## return

`return` closes a function earlier, before reaching its `end`.

Example:

```
function printstuff
  print Fried tofu is yummy
  return
  print Baguette
end

call printstuff
```

"Baguette" is never printed, because the line before returns the function, closing it earlier

## exec

`exec` Executes a system process, or command. It follows the syntax `exec PROGRAM ARG1 ARG2 ...`. PROGRAM represents the name of the command or the path to an executable file and is a mandatory argument to this instruction. The rest of the arguments are optional, and represent the arguments that will be passed to the program.

Since a new argument is identified with spaces or tabs, to include an argument that includes spaces, you can use quotation marks.

You can pass variables as individual arguments.

Example:

```
set path, /path/to/file.png

exec ffmpeg -i $path newfile.jpg
exec ffplay "/path/with spaces/image name.png"
```
This executes the commands "ffmpeg -i /path/to/file.png newfile.jpg" and "ffplay /path/with spaces/image name.png". On the second command, the second argument is contained inside quotation marks to indicate that the spaces inside should not represent new arguments.

## stop

`stop` stops the execution of the script immediately and has no additional syntax or arguments.

Example:

```
print Fried tofu
stop
print Oregano
```
This script prints "Fried tofu", but never ends up printing "Oregano", because the script is terminated before that point.

## sleep

Pauses execution of the script for a certain amount, measured in milliseconds

```
sleep 300

print The program paused for 300 milliseconds

sleep 300 300 300

print The program paused for 900 milliseconds
```

Passing strings to this instruction will return a syntax error.

## break

Interrupts a loop execution:

```
int c, 0
while $c < 3
  print C: $c
  calcint c, $c + 1
  break
endwhile
```
Only one instance of this loop runs because the keyword `break` cancels futher executions, including any lines inside the loop block that are below it.

## print

`print` prints a message to the terminal output. It follows the syntax `print MESSAGE`. The message can be infinite in length as long as it remains all in one line. You can read variables and pass them to the message.

Example:

```
set food, tofu

print I really love $food
```
This will print "I really love tofu"

## clear, color and locate

You can use `clear` to clear the terminal screen and line history:

```
print Some text
print More text
print Even more text
clear
print It's clean!
```

You can use `color` to define the foreground and background colors in your terminal:

```
color background, red
color text, green

print This text is green on top of a red background!
```

You can use `locate` to offset the next print message by X and Y lines/characters:

```
locate 20,7

print This message is 20 lines lower and 7 characters to the right!
```
