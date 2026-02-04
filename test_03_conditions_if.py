print("Test 1: age = 25")
age = 25
if (age >= 18):
    print("Vous êtes majeur")
print("Test 2: age = 15")
age = 15
if (age >= 18):
    print("Vous êtes majeur")
else:
    print("Vous êtes mineur")
print("Test 3: age = 30")
age = 30
if (age < 20):
    print("Moins de 20 ans")
else:
    if (age < 60):
        print("Entre 20 et 60 ans")
    else:
        print("Plus de 60 ans")
