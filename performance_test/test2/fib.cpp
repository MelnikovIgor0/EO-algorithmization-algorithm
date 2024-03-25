int fib(int n) {
    if (n == 0 || n == 1) {
        return 1;
    }
    return fib(n - 1) + fib(n - 2);
}

extern "C"
{
    extern int cffi_fib(int n)
    {
        return fib(n);
    }
}
