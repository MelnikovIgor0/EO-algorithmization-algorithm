import sys
import cffi
import time

ffi = cffi.FFI()
for i in range(1, 17):
    ffi.new(f"struct S{i} *")
for i in range(1, 17):
    ffi.cdef(f"S1 cffi_get_s{i}();")
C = ffi.dlopen("./get_obj.so")

start = time.time()

a = C.cffi_get_s1()

finish = time.time()

print(a)

print(finish - start)
