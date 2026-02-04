# Algorithme: Test10ComplexProgram
# Code généré automatiquement à partir du pseudo-code

notes = [0] * 5
def CalculerMoyenne(total, count):
    return (total / count)

def DeterminerGrade(moyenne):
    if (moyenne >= 90):
        return 1
    else:
        if (moyenne >= 80):
            return 2
        else:
            if (moyenne >= 70):
                return 3
            else:
                if (moyenne >= 60):
                    return 4
                else:
                    return 5

def ShowGrade(note):
    if note == 1:
        print("Grade: A (Excellent)")
    elif note == 2:
        print("Grade: B (Bon)")
    elif note == 3:
        print("Grade: C (Acceptable)")
    elif note == 4:
        print("Grade: D (Passable)")
    else:
        print("Grade: F (Échoué)")

print("=== Programme Gestion des Notes ===")
print("Remplissage des notes:")
notes[0] = 85
notes[1] = 92
notes[2] = 78
notes[3] = 88
notes[4] = 95
print("Notes:")
somme = 0
for index in range(0, 4 + 1):
    print("Note ", (index + 1), ": ", notes[index])
    somme = (somme + notes[index])
print("Calcul de la moyenne:")
moyenne = CalculerMoyenne(somme, 5)
print("Total: ", somme)
print("Nombre de notes: 5")
print("Moyenne: ", moyenne)
grade = DeterminerGrade(moyenne)
ShowGrade(grade)
print("=== Fin du programme ===")
