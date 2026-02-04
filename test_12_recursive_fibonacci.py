# Algorithme: TestRecursiveFibonacci
# Code généré automatiquement à partir du pseudo-code

def Fibonacci(n):
    if (n <= 1):
        return n
    else:
        return (Fibonacci((n - 1)) + Fibonacci((n - 2)))

print("=== Test Recursive Fibonacci ===")
print("Test 1: Fibonacci(0)")
resultat = Fibonacci(0)
print("Fib(0) = ", resultat)
print("Test 2: Fibonacci(1)")
resultat = Fibonacci(1)
print("Fib(1) = ", resultat)
print("Test 3: Fibonacci(5)")
resultat = Fibonacci(5)
print("Fib(5) = ", resultat)
print("Test 4: Fibonacci(7)")
resultat = Fibonacci(7)
print("Fib(7) = ", resultat)
print("Test 5: Fibonacci(10)")
resultat = Fibonacci(10)
print("Fib(10) = ", resultat)
print("=== End of tests ===")
