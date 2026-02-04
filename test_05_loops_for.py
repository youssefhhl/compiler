print("Test 1: Afficher 1 à 10")
for index in range(1, 10 + 1):
    print(index)
print("Test 2: Afficher nombres pairs de 2 à 10")
for index in range(2, 10 + 1):
    if ((index % 2) == 0):
        print(index)
print("Test 3: Factoriel de 5 (1*2*3*4*5)")
nombre = 1
for index in range(1, 5 + 1):
    nombre = (nombre * index)
print("Résultat: ", nombre)
print("Test 4: Somme des carrés de 1 à 5")
somme = 0
for index in range(1, 5 + 1):
    somme = (somme + (index * index))
print("Somme des carrés: ", somme)
