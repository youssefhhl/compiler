def PrintNumber(message, valeur):
    print(message, valeur)

def CountTo(limite):
    print("Counting to ", limite)

print("Test 1: Appel de procédure simple")
PrintNumber("Nombre: ", 42)
print("Test 2: Appel de procédure avec paramètres")
PrintNumber("Résultat: ", 100)
print("Test 3: Compter jusqu'à 5")
CountTo(5)
