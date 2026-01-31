# Algorithme: ExempleComplet
# Code généré automatiquement à partir du pseudo-code

print("=== Bienvenue dans le programme de démonstration ===")
print("Quel est votre nom ?")
nom = input()
print("Bonjour ", nom, " !")
x = 10
y = 5
somme = (x + y)
print("La somme de ", x, " et ", y, " est: ", somme)
resultat = ((x * y) - 3)
print("Le résultat de x * y - 3 est: ", resultat)
print("Entrez un nombre:")
x = int(input())
if (x > 0):
    print("Le nombre est positif")
else:
    if (x < 0):
        print("Le nombre est négatif")
    else:
        print("Le nombre est zéro")
print("Comptage de 1 à 5:")
compteur = 1
while (compteur <= 5):
    print("Compteur = ", compteur)
    compteur = (compteur + 1)
if (x == y):
    print("x est égal à y")
else:
    if (x > y):
        print("x est supérieur à y")
    else:
        print("x est inférieur à y")
print("Calcul de factorielle de 5:")
resultat = 1
compteur = 1
while (compteur <= 5):
    resultat = (resultat * compteur)
    compteur = (compteur + 1)
print("5! = ", resultat)
print("=== Fin du programme ===")
