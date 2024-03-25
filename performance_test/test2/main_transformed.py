import sys
import cffi

ffi = cffi.FFI()
ffi.cdef("int cffi_fib(int n);")
C = ffi.dlopen("./fib.so")

n = int(sys.argv[1])

print(C.cffi_fib(n))
