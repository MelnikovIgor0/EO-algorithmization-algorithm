#include <iostream>

struct S16 {
    int a;
};

struct S15 {
    int a;
    S16 left, right;
};

struct S14 {
    int a;
    S15 left, right;
};

struct S13 {
    int a;
    S14 left, right;
};

struct S12 {
    int a;
    S13 left, right;
};

struct S11 {
    int a;
    S12 left, right;
};

struct S10 {
    int a;
    S11 left, right;
};

struct S9 {
    int a;
    S10 left, right;
};

struct S8 {
    int a;
    S9 left, right;
};

struct S7 {
    int a;
    S8 left, right;
};

struct S6 {
    int a;
    S7 left, right;
};

struct S5 {
    int a;
    S6 left, right;
};

struct S4 {
    int a;
    S5 left, right;
};

struct S3 {
    int a;
    S4 left, right;
};

struct S2 {
    int a;
    S3 left, right;
};

struct S1 {
    int a;
    S2 left, right;
};

S1 get_s1() {
    S1 s;
    return s;
}

extern "C"
{
    extern S1 cffi_get_s1() {
        S1 s = get_s1();
        return s;
    }

    extern S2 cffi_get_s2() {
        S1 s = get_s1();
        return s.left;
    }

    extern S3 cffi_get_s3() {
        S1 s = get_s1();
        return s.left.left;
    }

    extern S4 cffi_get_s4() {
        S1 s = get_s1();
        return s.left.left.left;
    }

    extern S5 cffi_get_s5() {
        S1 s = get_s1();
        return s.left.left.left.left;
    }

    extern S6 cffi_get_s6() {
        S1 s = get_s1();
        return s.left.left.left.left.left;
    }

    extern S7 cffi_get_s7() {
        S1 s = get_s1();
        return s.left.left.left.left.left.left;
    }

    extern S8 cffi_get_s8() {
        S1 s = get_s1();
        return s.left.left.left.left.left.left.left;
    }

    extern S9 cffi_get_s9() {
        S1 s = get_s1();
        return s.left.left.left.left.left.left.left.left;
    }

    extern S10 cffi_get_s10() {
        S1 s = get_s1();
        return s.left.left.left.left.left.left.left.left.left;
    }

    extern S11 cffi_get_s11() {
        S1 s = get_s1();
        return s.left.left.left.left.left.left.left.left.left.left;
    }

    extern S12 cffi_get_s12() {
        S1 s = get_s1();
        return s.left.left.left.left.left.left.left.left.left.left.left;
    }

    extern S13 cffi_get_s13() {
        S1 s = get_s1();
        return s.left.left.left.left.left.left.left.left.left.left.left.left;
    }

    extern S14 cffi_get_s14() {
        S1 s = get_s1();
        return s.left.left.left.left.left.left.left.left.left.left.left.left.left;
    }

    extern S15 cffi_get_s15() {
        S1 s = get_s1();
        return s.left.left.left.left.left.left.left.left.left.left.left.left.left.left;
    }

    extern S16 cffi_get_s16() {
        S1 s = get_s1();
        return s.left.left.left.left.left.left.left.left.left.left.left.left.left.left.left;
    }
}
