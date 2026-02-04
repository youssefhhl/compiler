def Factorial(n):
    if (n <= 1):
        return 1
    else:
        return (n * Factorial((n - 1)))

print("=== Test Recursive Factorial ===")
print("Test 1: Factorial(0)")
resultat = Factorial(0)
print("0! = ", resultat)
print("Test 2: Factorial(1)")
resultat = Factorial(1)
print("1! = ", resultat)
print("Test 3: Factorial(3)")
resultat = Factorial(3)
print("3! = ", resultat)
print("Test 4: Factorial(5)")
resultat = Factorial(5)
print("5! = ", resultat)
print("Test 5: Factorial(7)")
resultat = Factorial(7)
print("7! = ", resultat)
print("=== End of tests ===")
