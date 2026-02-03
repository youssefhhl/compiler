# Algorithme: TestLogicalComplex
# Code généré automatiquement à partir du pseudo-code

a = 7
b = 3
c = 5
if (not (not (a > b))):
    print("Double NOT works correctly")
if (((a > b) and (b > c)) or (a == c)):
    print("First complex condition passed")
if ((not (a < b)) and (c > 0)):
    print("Second complex condition passed")
if (((a > 0) and (b > 0)) and (c > 0)):
    print("All three variables are positive")
if ((a == 7) or ((b == 2) and (c == 5))):
    print("Precedence test passed")
