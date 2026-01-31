# 📖 Documentation du Compilateur Pseudo-Code → Python

## Table des matières
1. [Introduction](#introduction)
2. [Installation](#installation)
3. [Utilisation](#utilisation)
4. [Grammaire du Pseudo-Code](#grammaire-du-pseudo-code)
5. [Architecture du Compilateur](#architecture-du-compilateur)
6. [Exemples](#exemples)
7. [Référence des Mots-clés](#référence-des-mots-clés)

---

## Introduction

Ce compilateur transforme du **pseudo-code français** en **code Python** exécutable. Il est conçu pour des fins pédagogiques et permet d'apprendre les bases de la compilation :
- Analyse lexicale (Lexer)
- Analyse syntaxique (Parser)
- Arbre Syntaxique Abstrait (AST)
- Génération de code

### Caractéristiques
- ✅ Langage source : Pseudo-code français
- ✅ Langage cible : Python 3
- ✅ Méthode d'analyse : Descente récursive
- ✅ Code Python généré : Script plat (pas de classes, pas de `try/except`)

---

## Installation

### Prérequis
- **Java 17+** (JDK installé)
- **Windows** (scripts `.bat` fournis)

### Structure du projet
```
compilateur/
├── pom.xml                         # Configuration Maven
├── run.bat                         # Script d'exécution
├── exemple.pso                     # Fichier de test
├── simple.pso                      # Fichier de test simple
├── README.md                       # Ce fichier
└── src/main/java/org/example/
    ├── TokenType.java              # Énumération des types de tokens
    ├── Token.java                  # Classe Token
    ├── Lexer.java                  # Analyseur lexical
    ├── AST.java                    # Arbre Syntaxique Abstrait
    ├── Parser.java                 # Analyseur syntaxique
    ├── PythonGenerator.java        # Générateur Python
    └── Main.java                   # Point d'entrée
```

---

## Utilisation

### Méthode 1 : Script batch (recommandé)
```batch
.\run.bat mon_fichier.pso
```

### Méthode 2 : Ligne de commande Java
```powershell
# Compiler les sources
javac -d target\classes src\main\java\org\example\*.java

# Exécuter
java -cp target\classes org.example.Main mon_fichier.pso
```

### Méthode 3 : Depuis IntelliJ IDEA
1. Ouvrir le projet
2. Configurer les arguments du programme : `exemple.pso`
3. Définir le répertoire de travail : `C:\Users\Victus\Desktop\compilateur`
4. Exécuter `Main.java`

### Sortie
Le compilateur génère un fichier `.py` avec le même nom que le fichier source :
- `exemple.pso` → `exemple.py`

---

## Grammaire du Pseudo-Code

### Structure générale d'un programme
```
ALGORITHME NomDuProgramme

VARIABLES
    variable1 : TYPE
    variable2 : TYPE

DEBUT
    // Instructions ici
FIN
```

### Règles de grammaire (BNF simplifié)
```bnf
<programme>     ::= ALGORITHME <identifiant> VARIABLES <declarations> DEBUT <instructions> FIN

<declarations>  ::= (<declaration>)*
<declaration>   ::= <identifiant> ":" <type>
<type>          ::= ENTIER | TEXTE

<instructions>  ::= (<instruction>)*
<instruction>   ::= <affectation> | <si> | <tantque> | <ecrire> | <lire>

<affectation>   ::= <identifiant> "<-" <expression>

<si>            ::= SI <condition> ALORS <instructions> [SINON <instructions>] FINSI

<tantque>       ::= TANTQUE <condition> FAIRE <instructions> FINTANTQUE

<ecrire>        ::= ECRIRE "(" <expression> ("," <expression>)* ")"

<lire>          ::= LIRE "(" <identifiant> ")"

<condition>     ::= <expression> <op_comparaison> <expression>
<op_comparaison>::= ">" | "<" | "==" | "!=" | ">=" | "<="

<expression>    ::= <terme> (("+" | "-") <terme>)*
<terme>         ::= <facteur> (("*" | "/") <facteur>)*
<facteur>       ::= <nombre> | <chaine> | <identifiant> | "(" <expression> ")"
```

---

## Architecture du Compilateur

### Flux de compilation
```
┌─────────────┐     ┌─────────┐     ┌─────────┐     ┌─────────────┐     ┌─────────┐
│ Fichier.pso │ --> │  Lexer  │ --> │ Tokens  │ --> │   Parser    │ --> │   AST   │
└─────────────┘     └─────────┘     └─────────┘     └─────────────┘     └────┬────┘
                                                                              │
                    ┌─────────────┐     ┌───────────────────┐                 │
                    │ Fichier.py  │ <-- │ PythonGenerator   │ <───────────────┘
                    └─────────────┘     └───────────────────┘
```

### 1. Lexer (Analyse Lexicale)
**Fichier :** `Lexer.java`

Le Lexer transforme le code source en une liste de **tokens** (unités lexicales).

**Exemple :**
```
x <- 5 + 3
```
Devient :
```
[IDENTIFIANT("x"), AFFECTATION("<-"), NOMBRE("5"), PLUS("+"), NOMBRE("3")]
```

### 2. Parser (Analyse Syntaxique)
**Fichier :** `Parser.java`

Le Parser utilise la méthode de **descente récursive** pour construire l'AST.

Chaque règle de grammaire correspond à une méthode :
- `parseProgramme()` → Programme complet
- `parseInstruction()` → Une instruction
- `parseExpression()` → Une expression
- `parseSi()` → Structure SI/ALORS/SINON
- `parseTantQue()` → Boucle TANTQUE

### 3. AST (Arbre Syntaxique Abstrait)
**Fichier :** `AST.java`

Hiérarchie des nœuds :
```
Node (abstraite)
├── ProgrammeNode      - Programme complet
├── BlockNode          - Bloc d'instructions
├── DeclarationNode    - Déclaration de variable
├── AffectationNode    - Affectation (x <- valeur)
├── SiNode             - Structure conditionnelle
├── TantQueNode        - Boucle while
├── EcrireNode         - Instruction print
├── LireNode           - Instruction input
├── ExpressionBinaire  - Opération binaire (a + b)
├── NombreNode         - Nombre littéral
├── ChaineNode         - Chaîne littérale
└── IdentifiantNode    - Variable
```

### 4. Générateur Python
**Fichier :** `PythonGenerator.java`

Le générateur parcourt l'AST et produit du code Python :
- Gère l'indentation (4 espaces par niveau)
- Convertit `LIRE(x)` en `int(input())` pour les entiers
- Génère un script Python procédural (pas de classes)

---

## Exemples

### Exemple 1 : Hello World
**Pseudo-code (`hello.pso`) :**
```
ALGORITHME HelloWorld

VARIABLES

DEBUT
    ECRIRE("Bonjour le monde!")
FIN
```

**Python généré (`hello.py`) :**
```python
# Algorithme: HelloWorld
# Code généré automatiquement à partir du pseudo-code

print("Bonjour le monde!")
```

### Exemple 2 : Calcul avec entrée utilisateur
**Pseudo-code :**
```
ALGORITHME Addition

VARIABLES
    a : ENTIER
    b : ENTIER
    somme : ENTIER

DEBUT
    ECRIRE("Entrez le premier nombre:")
    LIRE(a)
    ECRIRE("Entrez le deuxième nombre:")
    LIRE(b)
    somme <- a + b
    ECRIRE("La somme est: ", somme)
FIN
```

**Python généré :**
```python
# Algorithme: Addition
# Code généré automatiquement à partir du pseudo-code

print("Entrez le premier nombre:")
a = int(input())
print("Entrez le deuxième nombre:")
b = int(input())
somme = (a + b)
print("La somme est: ", somme)
```

### Exemple 3 : Structure conditionnelle
**Pseudo-code :**
```
ALGORITHME PariteNombre

VARIABLES
    n : ENTIER

DEBUT
    ECRIRE("Entrez un nombre:")
    LIRE(n)
    
    SI n > 0 ALORS
        ECRIRE("Le nombre est positif")
    SINON
        SI n < 0 ALORS
            ECRIRE("Le nombre est négatif")
        SINON
            ECRIRE("Le nombre est zéro")
        FINSI
    FINSI
FIN
```

**Python généré :**
```python
print("Entrez un nombre:")
n = int(input())
if (n > 0):
    print("Le nombre est positif")
else:
    if (n < 0):
        print("Le nombre est négatif")
    else:
        print("Le nombre est zéro")
```

### Exemple 4 : Boucle TANTQUE
**Pseudo-code :**
```
ALGORITHME Compteur

VARIABLES
    i : ENTIER

DEBUT
    i <- 1
    TANTQUE i <= 10 FAIRE
        ECRIRE("i = ", i)
        i <- i + 1
    FINTANTQUE
FIN
```

**Python généré :**
```python
i = 1
while (i <= 10):
    print("i = ", i)
    i = (i + 1)
```

---

## Référence des Mots-clés

### Structure du programme
| Mot-clé | Description | Exemple |
|---------|-------------|---------|
| `ALGORITHME` | Déclare le nom du programme | `ALGORITHME MonProg` |
| `VARIABLES` | Section des déclarations | `VARIABLES` |
| `DEBUT` | Début du bloc principal | `DEBUT` |
| `FIN` | Fin du programme | `FIN` |

### Types de données
| Mot-clé | Description | Python équivalent |
|---------|-------------|-------------------|
| `ENTIER` | Nombre entier | `int` |
| `TEXTE` | Chaîne de caractères | `str` |

### Entrées/Sorties
| Mot-clé | Description | Python équivalent |
|---------|-------------|-------------------|
| `ECRIRE(...)` | Affiche à l'écran | `print(...)` |
| `AFFICHER(...)` | Alias de ECRIRE | `print(...)` |
| `LIRE(var)` | Lit une entrée | `var = input()` ou `var = int(input())` |

### Structures de contrôle
| Mot-clé | Description |
|---------|-------------|
| `SI` | Début de condition |
| `ALORS` | Bloc si vrai |
| `SINON` | Bloc si faux (optionnel) |
| `FINSI` | Fin de condition |
| `TANTQUE` | Début de boucle |
| `FAIRE` | Début du corps de boucle |
| `FINTANTQUE` | Fin de boucle |

### Opérateurs
| Opérateur | Description |
|-----------|-------------|
| `<-` | Affectation |
| `+` | Addition |
| `-` | Soustraction |
| `*` | Multiplication |
| `/` | Division |
| `>` | Supérieur à |
| `<` | Inférieur à |
| `>=` | Supérieur ou égal |
| `<=` | Inférieur ou égal |
| `==` | Égal à |
| `!=` | Différent de |

---

## Gestion des erreurs

Le compilateur détecte et signale les erreurs suivantes :

### Erreurs lexicales
- Caractère non reconnu
- Chaîne non fermée

### Erreurs syntaxiques
- Mot-clé manquant (`DEBUT`, `FIN`, `FINSI`, etc.)
- Parenthèse non fermée
- Expression invalide

**Exemple de message d'erreur :**
```
❌ Erreur de compilation: Mot-clé 'FINSI' attendu à la ligne 15, colonne 1
```

---

## Limitations connues

1. **Types limités** : Seulement `ENTIER` et `TEXTE`
2. **Pas de fonctions** : Pas de support pour les procédures/fonctions
3. **Pas de tableaux** : Les tableaux ne sont pas supportés
4. **Opérateurs logiques** : Pas de `ET`, `OU`, `NON`
5. **Nombres décimaux** : Seuls les entiers sont supportés

---

## Licence

Projet académique - Usage éducatif uniquement.

---

*Documentation générée pour le compilateur Pseudo-Code vers Python v1.0*
