# Algorithme: Test06Arrays
# Code généré automatiquement à partir du pseudo-code

nombres = [0] * 10
print("Test 1: Remplir tableau avec carrés")
for index in range(0, 9 + 1):
    nombres[index] = (index * index)
print("Test 2: Afficher tableau")
for index in range(0, 9 + 1):
    print("Index ", index, " = ", nombres[index])
print("Test 3: Calculer somme du tableau")
somme = 0
for index in range(0, 9 + 1):
    somme = (somme + nombres[index])
print("Somme totale: ", somme)
print("Test 4: Trouver le maximum")
nombres[0] = 15
nombres[1] = 23
nombres[2] = 8
nombres[3] = 42
nombres[4] = 16
for index in range(0, 4 + 1):
    print("Valeur: ", nombres[index])
