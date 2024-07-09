# Tofu variables

Tofu's specification contains a simple way to store information in memory to be used later, which are variables. You can assign a variable and its value with the instruction `set` ([see instruction documentation](instructions.md)), and it can be read with the keyword $.

Example:

```
set variable1, woah

print $variable1
```

The variable of name "variable1" is declared and given the value "woah". We can access this variable by passing $variable1 on instructions that support reading variables, such as set, print and exec. This script will print "woah".

As of now, you cannot assign a string with whitespaces to a new variable, so trying to assign the value "abc def" will only make Tofu assign "abc". This will change in a future version.

## Passing CLI arguments to variables

On Tofu, you can pass CLI arguments as global variables. Any command-line argument that does not start with the character `-` and is not a path that leads to a readable file will be passed as a global variable to the script.

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
