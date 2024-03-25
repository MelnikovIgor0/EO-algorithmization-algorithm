#include <iostream>

void compare(int a, int b)
{
    if (a > b) {
        std::cout << "first is more";
    } else {
        std::cout << "second is more";
    }
}

extern "C"
{
    extern void cffi_compare(int a, int b)
    {
        return compare(a, b);
    }
}