import sys

def fib(x):
    if x == 0 or x == 1:
        return 1
    return fib(x - 1) + fib(x - 2)

n = int(sys.argv[1])

print(fib(n))
