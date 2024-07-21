if $0 !exists
  print You must at least pass an argument do define the CPU clock speed!
  print For example: 2.5g for setting the speed to 2.5GHz
  stop
endif

if $1 !exists
  print Setting CPU's min and max clock speeds to $0
  exec sudo cpupower frequency-set -d $0
  exec sudo cpupower frequency-set -u $0
  stop
endif

print Setting minimum clock speed to $0
print Setting maximum clock speed to $1

exec sudo cpupower frequency-set -d $0
exec sudo cpupower frequency-set -u $1

if $2 exists
  print Setting CPU governor to $2
  exec sudo cpupower frequency-set -g $2
endif
