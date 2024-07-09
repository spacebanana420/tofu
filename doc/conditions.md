# Tofu conditions and branching

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
set number, 3
if $number == 4
  print It's true
endif
```
This code won't print anything, because the if statement compares $number to 4, which results in checking if 3 is equal to 4.

You can also nest as many if statements as you wish:

```
set number, 3

if 3 == $number
  if 4 > 5
    print Second
  endif
  print First
endif
```
This will print "First", since 3 is equal to 3, but it will not print "Second", because 4 is not bigger than 5.

### Available operators

* == - Equals to
* != - Does not equal to
* > - Is bigger than
* >= - Is equal or bigger than
* < - Is lesser than
* <= - Is equal or lesser than
