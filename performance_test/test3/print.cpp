#include <iostream>

void print(int n) {
    std::cout << n;
}

extern "C"
{
    extern void cffi_print(int n)
    {
        return print(n);
    }
}
