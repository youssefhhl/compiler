# Algorithme: Test09SwitchCase
# Code généré automatiquement à partir du pseudo-code

print("Test 1: Jours de la semaine")
for operation in range(1, 7 + 1):
    if operation == 1:
        print("Lundi")
    elif operation == 2:
        print("Mardi")
    elif operation == 3:
        print("Mercredi")
    elif operation == 4:
        print("Jeudi")
    elif operation == 5:
        print("Vendredi")
    elif operation == 6:
        print("Samedi")
    elif operation == 7:
        print("Dimanche")
    else:
        print("Jour invalide")
print("Test 2: Calcul simple par opération")
for operation in range(1, 5 + 1):
    resultat = 0
    if operation == 1:
        resultat = (10 + 5)
        print("Addition: 10 + 5 = ", resultat)
    elif operation == 2:
        resultat = (10 - 5)
        print("Soustraction: 10 - 5 = ", resultat)
    elif operation == 3:
        resultat = (10 * 5)
        print("Multiplication: 10 * 5 = ", resultat)
    elif operation == 4:
        resultat = (10 / 5)
        print("Division: 10 / 5 = ", resultat)
    else:
        print("Opération inconnue")
print("Test 3: Grades")
for operation in range(1, 4 + 1):
    if operation == 1:
        print("Grade A: Excellent")
    elif operation == 2:
        print("Grade B: Bon")
    elif operation == 3:
        print("Grade C: Acceptable")
    else:
        print("Grade non reconnu")
