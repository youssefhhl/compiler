def Ajouter(val1, val2):
    return (val1 + val2)

def Multiplier(val1, val2):
    return (val1 * val2)

def Puissance(base, exposant):
    return (base * base)

print("Test 1: Ajouter(10, 5)")
resultat = Ajouter(10, 5)
print("Résultat: ", resultat)
print("Test 2: Multiplier(6, 7)")
resultat = Multiplier(6, 7)
print("Résultat: ", resultat)
print("Test 3: Puissance(2, 3) = 8")
resultat = Puissance(2, 3)
print("2^3 = ", resultat)
print("Test 4: Puissance(5, 2) = 25")
resultat = Puissance(5, 2)
print("5^2 = ", resultat)
