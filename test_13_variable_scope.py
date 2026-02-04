def fonctionTest(param):
    print("  Dans fonction: param=", param, " global x=", x)

x = 10
y = "global"
print("Avant appel: x=", x, " y=", y)
fonctionTest(5)
print("Apres appel: x=", x, " y=", y)
