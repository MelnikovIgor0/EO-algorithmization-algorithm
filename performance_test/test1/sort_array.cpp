int* sort_array(int* a, int n) {
    int i, j, x;
    for (i = 0; i + 1 < n; ++i) {
        for (j = 0; j < n - i - 1; ++j) {
            if (a[j] > a[j + 1]) {
                x = a[j];
                a[j] = a[j + 1];
                a[j + 1] = x;
            }
        }
    }
    return a;
}

extern "C"
{
    extern int* cffi_sort_array(int* a, int n)
    {
        return sort_array(a, n);
    }
}