# Tofu variables

Tofu's specification contains a simple way to store information in memory to be used later, which are variables. You can assign a variable and its value with the instructions `string` and `int` ([see instruction documentation](instructions.md)), and it can be read with the keyword $.

### Variable types

* **string** - For text variables, such as "tofu"
* **int** - For numerical variables, such as 24

Example:

```
string variable1, woah

print $variable1
```

The variable of name "variable1" is declared and given the value "woah", and type `string`. We can access this variable by passing $variable1 on instructions that support reading variables, such as set, print and exec. This script will print "woah".

```
int variable1, 45
if $variable1 > 10
  print 45 truly is bigger than 10
endif
```

The variable is declared as an `int`, and so you can pass number values to it rather than text.

## Changing the value of a variable

To change the value of a variable, you can re-declare it:

```
int a, 35
print $a

int a, 45
print $a
```

This code will print "35" and then "45"

## Passing CLI arguments to variables

On Tofu, you can pass CLI arguments as global variables of type `string`. Any command-line argument that does not start with the character `-` and is not a path that leads to a readable file will be passed as a global variable to the script.

Let's assume you have a script named `script.tofu` and you want to run it and pass "linguine" as a variable right when launching the script, you can do so by running the command:

```
tofu script.tofu linguine
```
"linguine" is now a global variable during script execution. Global variables are read by passing the number of their order (starting from 0). Since "linguine" is the first (and only, in this case) global variable, you can read it by calling `$0`

```
print $0
```

This will print "linguine".

You can pass as many arguments as you want, but note that they will be global variables to all scripts you run at once as well.

```
tofu script1.tofu script2.tofu "i like tofu" "kasane teto" "baguette"
```

For both `script1.tofu` and `script2.tofu`, their global variables `$0`, `$1` and `$2` are respectively "i like tofu", "kasane teto" and "baguette".

If an argument variable does not exist, attempting to read the variable will only return its name. For example, reading $7 when there aren't 8 argument variables will give you the value $7 instead. You can use this to check for global variables:

```
if $0 exists
  print The argument $0 has been passed as a variable!
endif
```

You can use the keywords `exists` and `!exists` to check if a variable exists or doesn't, respectively. In this example, this condition returns true if the variable of name "0" exists.


## Arithmetic calculations for integers

With the instruction `calcint`, you can perform arithmetic calculations from left to right and assign the result as a variable which you can use later. The syntax is similar to `int`, as it requires a name and value to declare a variable, but the value is the result of the performed calculations.

Available operators:
* `+`
* `-`
* `*`
* `/`
* `%`

Example:

```
int a, 45
calcint result, 3 + 5 + $a - 1
print $result
```
