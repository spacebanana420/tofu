# Tofu's instruction set

Tofu's language style is somewhat inspired by CPU architecture instruction sets. Tofu instructions are pretty straightforward, and are used for when we want the script to perform a task.

### Available instructions

* set
* print
* goto
* return
* exec
* stop


## set

`set` assigns a variable which can be read later by some instructions. It follows the syntax `set NAME, VALUE`. An assigned variable can be read by typing its name, followed by the character '$'.

Example:

```
set message, I love Tofu

print $message
```

The line `print $message` will print "I love Tofu". Variables, just like functions, have no privacy. This means that, once the variable is set, any part of the script from this point onwards can access it.

At the moment, variables can only be of the string type, and so they are merely text. Numerical variables are a planned feature for a future version.

You can also use the value of an already-existing variable to set the new one:

```
set variable1, 234
set variable2, $variable1

print $variable2
```

This will print "234".

## print

`print` prints a message to the terminal output. It follows the syntax `print MESSAGE`. The message can be infinite in length as long as it remains all in one line. You can read variables and pass them to the message.

Example:

```
set food, tofu

print I really love $food
```
This will print "I really love tofu"

## goto

`goto` executes a function if it exists. It follows the syntax `goto NAME`.

Example:

```
function printstuff
  print Fried tofu is yummy
end

goto printstuff
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

goto printstuff
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

