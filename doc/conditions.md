# Tofu conditions and branching

Tofu can do branching with condition instructions, such as if statements and while loops.

### Available operators

* `==` - Equals to
* `!=` - Does not equal to
* `>` - Is bigger than
* `>=` - Is equal or bigger than
* `<` - Is lesser than
* `<=` - Is equal or lesser than
* `exists` - If a variable or value exists
* `!exists` - If a variable or value does not exist
* `contains` - If element A contains the text of element B inside
* `!contains` - If element A does not contain the text of element B inside

## If statements

In Tofu, you can use "if" statements to split the script's outcome into multiple possibilities.

Example:

```
if 3 == 3
  print It's true
endif
```
This will print "It's true", because the if statement is comparing the values of both entries of 3. Since 3 is indeed equal to 3, the code inside the statement runs.

You can also pass variables to if statements:

```
int number, 3
if $number == 4
  print It's true
endif
```
This code won't print anything, because the if statement compares $number to 4, which results in checking if 3 is equal to 4.

You can also nest as many if statements as you wish:

```
int number, 3

if 3 == $number
  if 4 > 5
    print Second
  endif
  print First
endif
```
This will print "First", since 3 is equal to 3, but it will not print "Second", because 4 is not bigger than 5.

### How values are compared

For the operators `==` and `!=`, all passed values are compared whether they are equal or not. However, for the rest of the operators that compare between 2 values, such as `>` and `<=`, the behavior is different. For integer values and variables, they are compared directly, but for string/text portions, their lengths are compared instead

Examples:

```
if 3 > 3
  print Not true
endif
```

Both values being compared are integer numbers, and so their values are compared directly

```
if "big text big text" > "small text"
  print It's comparing lengths
endif
```

This returns true, as the condition is comparing the lenghts of the strings, and not their values.

### Checking if a variable exists

You can use the keyword `exists` to check if a variable exists, or `!exists` to check if it does not exist. Values always exist.

```
int variable, 650

if variable exists
  print It does in fact exist
endif

if fakevariable !exists
  print It does not exist
endif
```

The variable `variable` exists, and so the condition returns true. `fakevariable` does not exist, and so the second condition also returns true.

```
if 3 exists
  print It's not a variable, it will always exist
endif
```

This will always return true because the example checks for a value rather than a variable.


### Checking if a string contains a substring

ou can use the keyword `contains` to check if a string contains another string inside (or equals to it), or `!contains` to check if it doesn't.

```
if abcdef contains abc
  print It does
endif
```
