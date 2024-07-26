int i, 0

function test
  if 2 > 3
    print $a
  endif
  print $b
end

call test
print $c

while $i < 3
  print I: $i
  calcint i, $i + 1

  int c, 0
  while $c < 3
    print C: $c
    calcint c, $c + 1
  endwhile
endwhile
