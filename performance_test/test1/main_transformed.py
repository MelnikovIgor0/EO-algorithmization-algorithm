from random import randint
import sys
import cffi

ffi = cffi.FFI()
ffi.cdef("int* cffi_sort_array(int* a, int n);")
C = ffi.dlopen("./sort_array.so")

n = int(sys.argv[1])

a = [randint(0, 1000) for i in range(n)]

a = C.cffi_sort_array(a, n)
