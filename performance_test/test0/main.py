import cffi

ffi = cffi.FFI()
ffi.cdef("void cffi_compare(int a, int b);")
C = ffi.dlopen("./libcompare.so")

a = int(input())
b = int(input())
C.cffi_compare(a, b)
