//Random example for testing tofu features

function encode
  exec ffmpeg -loglevel 0 -i "/home/space/Pictures/Kirby Shook_square20x.png" -filter:v $ffargs test.png
end

// this is a comment
print $doremy
print $0
int doremy, 45
string ffargs, scale=iw/2:ih/2
int doremy2, $doremy

string test, a b c
print $test

exec ffplay -loglevel 0 "/home/space/Pictures/Kirby Shook_square20x.png"

print $doremy
print $doremy2

call encode

if 3 == 3
  print It's true
endif
