from random import randint
import sys

n = int(sys.argv[1])

a = [randint(0, 1000) for i in range(n)]

for i in range(n - 1):
    for j in range(n - i - 1):
        if a[j] > a[j + 1]:
            a[j], a[j + 1] = a[j + 1], a[j]
