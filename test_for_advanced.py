# Algorithme: TestForAdvanced
# Code généré automatiquement à partir du pseudo-code

print("Multiplication Table (2x2):")
for i in range(1, 3 + 1):
    for j in range(1, 3 + 1):
        print((i * j), " ")
    print("")
print("Numbers divisible by 3 (1-20):")
for i in range(1, 20 + 1):
    if ((i % 3) == 0):
        print(i)
sum = 0
counter = 0
for i in range(1, 10 + 1):
    sum = (sum + i)
    if (i > 5):
        counter = (counter + 1)
print("Sum of 1-10: ", sum)
print("Numbers greater than 5: ", counter)
avg = 0.0
for i in range(1, 5 + 1):
    avg = (avg + i)
print("Average of 1-5: ", (avg / 5))
