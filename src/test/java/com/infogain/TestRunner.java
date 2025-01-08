package com.infogain;

public class TestRunner {
    public static void main(String[] args) {
        // Example in Java
        System.out.println(factorial(0)); // Outputs 1
    }

    public static int factorial(int n) {
        if (n == 0) {
            return 1; // Base case: 0! = 1
        }
        return n * factorial(n - 1); // Recursive case
    }
}
