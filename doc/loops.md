# Tofu while and for loops

## While loops

On Tofu, you can define the start and end of a while loop block with the keywords `while` and `endwhile`. A while loop runs a block of code until a condition is no longer met:

```
int num, 0

while num < 10
  print $num
  calcint num, $num + 1
endwhile
print It's done!
```

In this piece of code, the variable `num` will be printed and then incremented by one until its value reaches 10.

You can interrupt and cancel a loop execution with the keyword `break`:

```
int c, 0
while $c < 3
  print C: $c
  calcint c, $c + 1
  break
endwhile
```

Only one instance of this loop runs because the keyword `break` cancels futher executions, including any lines inside the loop block that are below it.

## For loops

A foor loop lets you iterate through the elements of an array to process them from start to finish. You can use the `for` keyword and pass an array to it to begin processing your whole array:

```
array a1
arradd a1, text1
arradd a1, text2
arradd a1, text3

for i in a1
  print $i
endfor
```

This script will print "text1", "text2" and "text3".
