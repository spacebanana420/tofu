function encode
  exec ffmpeg -loglevel 0 -i "/home/space/Pictures/Kirby Shook_square20x.png" -filter:v $ffargs test.png
end

// this is a comment
set doremy, 45
set ffargs, scale=iw/2:ih/2
exec ffplay -loglevel 0 "/home/space/Pictures/Kirby Shook_square20x.png"
print $doremy
goto encode

if 3 == 3
  print It's true
endif
