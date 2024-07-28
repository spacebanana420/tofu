array a1
arradd a1, 324
arradd a1, 3
arradd a1, 45

array a2
arradd a2, Print 1
arradd a2, Print 2

for i in a1
  for c in a2
    print $c
  endfor
  print $i
endfor
print It's done!
