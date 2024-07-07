function encode
  exec ffmpeg -i "/home/space/Pictures/Kirby Shook_square20x.png" -filter:v $ffargs test.png
end

// this is a comment
set doremy, 45
set ffargs, scale=iw/2:ih/2
exec ffplay "/home/space/Pictures/Kirby Shook_square20x.png"
print $doremy
goto encode
