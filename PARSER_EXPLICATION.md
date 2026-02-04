# COMPRENDRE LE PARSER - CONCEPT ET EXÉCUTION

## TABLE DES MATIÈRES
1. [Concept: Qu'est-ce qu'un Parser?](#concept)
2. [Comment fonctionne notre Parser](#fonctionnement)
3. [La forme d'arbre (AST) obtenue](#forme-arbre)
4. [Exemples complets avec visualisations](#exemples)

---

## CONCEPT: QU'EST-CE QU'UN PARSER?

### Définition Simple
Un **parser** (analyseur syntaxique) est un programme qui:
1. **Reçoit** une liste de tokens (du Lexer)
2. **Vérifie** que les tokens suivent les règles grammaticales
3. **Construit** une structure arborescente (AST) représentant le code

### Analogie: Construction d'une Phrase

```
Tokens = Mots individuels
Parser = Grammaire français
AST = Structure grammaticale
```

**Exemple en français:**
```
Tokens: [Le] [chat] [noir] [saute] [sur] [le] [mur]

Parser applique les règles:
- ✅ Article + Adjectif + Nom = Sujet valide
- ✅ Verbe = Action valide
- ✅ Préposition + Article + Nom = Complément valide

Résultat: Phrase valide ✅
```

### Notre Parser: Tokens → AST

```
ENTRÉE (Tokens):
[ALGORITHME] [TestSum] [VARIABLES] [x] [:] [ENTIER] [DEBUT] [x] [<-] [5] [FIN]

Parser applique les règles:
- Reconnaît le mot-clé ALGORITHME
- Récupère le nom TestSum
- Reconnaît VARIABLES
- Parse la déclaration x: ENTIER
- Reconnaît DEBUT
- Parse l'affectation x <- 5
- Reconnaît FIN

SORTIE (AST):
ProgrammeNode (TestSum)
├── Déclarations: [x: ENTIER]
└── Corps: BlockNode
    └── AffectationNode(x ← 5)
```

---

## COMMENT FONCTIONNE NOTRE PARSER

### Architecture Générale

```
Parser
├── Entrée: List<Token>
├── État: position (index courant)
└── Méthodes principales:
    ├── parseProgramme()      → Entrée du programme
    ├── parseDeclarations()   → Variables
    ├── parseInstructions()   → Bloc d'instructions
    ├── parseInstruction()    → Une instruction
    ├── parseAffectation()    → x <- expression
    ├── parseSi()             → SI...ALORS...FINSI
    ├── parseTantQue()        → TANTQUE...FINTANTQUE
    ├── parsePour()           → POUR...FINPOUR
    ├── parseEcrire()         → ECRIRE(...)
    ├── parseLire()           → LIRE(...)
    └── parseExpression()     → Expressions arithmétiques
```

### Méthodes Principales du Parser

#### 1. `analyser()` - Point d'Entrée
```java
public AST.ProgrammeNode analyser() {
    return parseProgramme();
}
```

C'est le **point d'entrée unique**. Elle appelle `parseProgramme()` qui débute l'analyse complète.

#### 2. `parseProgramme()` - Programme Complet

```java
private AST.ProgrammeNode parseProgramme() {
    // 1. Attendre ALGORITHME
    consommer(TokenType.ALGORITHME, "Mot-clé 'ALGORITHME' attendu");

    // 2. Récupérer le nom
    String nomAlgorithme = consommer(TokenType.IDENTIFIANT,
                                     "Nom de l'algorithme attendu").getValeur();

    // 3. Attendre VARIABLES et parser les déclarations
    List<AST.DeclarationNode> declarations = new ArrayList<>();
    if (verifier(TokenType.VARIABLES)) {
        avancer();
        declarations = parseDeclarations();
    }

    // 4. Attendre DEBUT
    consommer(TokenType.DEBUT, "Mot-clé 'DEBUT' attendu");

    // 5. Parser les instructions du corps
    AST.BlockNode corps = parseInstructions();

    // 6. Attendre FIN
    consommer(TokenType.FIN, "Mot-clé 'FIN' attendu à la fin du programme");

    // 7. Retourner le nœud racine
    return new AST.ProgrammeNode(nomAlgorithme, declarations, corps);
}
```

**Grammaire**:
```
programme → ALGORITHME identifiant VARIABLES declarations DEBUT instructions FIN
```

#### 3. Méthodes de Déplacement dans les Tokens

```java
// Obtenir le token actuel (sans avancer)
private Token tokenActuel() {
    if (position >= tokens.size()) return tokens.get(tokens.size() - 1); // EOF
    return tokens.get(position);
}

// Vérifier le type du token actuel
private boolean verifier(TokenType type) {
    return tokenActuel().getType() == type;
}

// Avancer au token suivant
private Token avancer() {
    Token token = tokenActuel();
    position++;  // ← Déplacer le pointeur
    return token;
}

// Consommer: vérifier + avancer
private Token consommer(TokenType type, String messageErreur) {
    if (verifier(type)) {
        return avancer();  // OK, avancer
    }
    // Sinon, lancer une erreur
    throw new RuntimeException(messageErreur + " à la ligne " +
                               tokenActuel().getLigne());
}
```

**Visualisation du déplacement**:
```
tokens: [ALGORITHME] [TestSum] [VARIABLES] [x] [:] [ENTIER] [DEBUT] ... [FIN]
position: 0

1. parseProgramme() appelle consommer(ALGORITHME)
   → position: 1

2. consommer(IDENTIFIANT)
   → position: 2

3. consommer(VARIABLES)
   → position: 3

Et ainsi de suite...
```

#### 4. `parseInstructions()` - Bloc d'Instructions

```java
private AST.BlockNode parseInstructions() {
    AST.BlockNode bloc = new AST.BlockNode();

    // Boucler jusqu'à un mot-clé de fin
    while (!verifier(TokenType.FIN) &&          // Fin du programme
           !verifier(TokenType.FINSI) &&        // Fin du SI
           !verifier(TokenType.FINTANTQUE) &&   // Fin du TANTQUE
           !verifier(TokenType.FINPOUR) &&      // Fin du POUR
           !verifier(TokenType.EOF)) {          // Fin du fichier

        AST.Node instruction = parseInstruction();
        if (instruction != null) {
            bloc.ajouter(instruction);  // Ajouter à la liste
        }
    }

    return bloc;  // Retourner un bloc contenant toutes les instructions
}
```

**Le bloc collecte toutes les instructions** jusqu'à ce qu'il rencontre un mot-clé terminal.

#### 5. `parseInstruction()` - Une Instruction

```java
private AST.Node parseInstruction() {
    // Dispatcher vers la bonne méthode selon le token

    if (verifier(TokenType.SI)) {
        return parseSi();           // SI...FINSI
    }
    if (verifier(TokenType.TANTQUE)) {
        return parseTantQue();      // TANTQUE...FINTANTQUE
    }
    if (verifier(TokenType.POUR)) {
        return parsePour();         // POUR...FINPOUR
    }
    if (verifier(TokenType.ECRIRE)) {
        return parseEcrire();       // ECRIRE(...)
    }
    if (verifier(TokenType.LIRE)) {
        return parseLire();         // LIRE(...)
    }
    if (verifier(TokenType.IDENTIFIANT)) {
        return parseAffectation();  // x <- ...
    }

    return null;  // Pas une instruction reconnue
}
```

#### 6. Exemple: `parseAffectation()` - Affectation Simple

```java
private AST.AffectationNode parseAffectation() {
    // 1. Récupérer le nom de la variable
    String variable = consommer(TokenType.IDENTIFIANT,
                               "Identifiant attendu").getValeur();

    // 2. Attendre l'opérateur <-
    consommer(TokenType.AFFECTATION, "Opérateur '<-' attendu");

    // 3. Parser l'expression (valeur à affecter)
    AST.Node valeur = parseExpression();

    // 4. Retourner le nœud d'affectation
    return new AST.AffectationNode(variable, valeur);
}
```

**Grammaire**:
```
affectation → IDENTIFIANT <- expression
```

**Exemple de parsing**:
```
Tokens: [x] [<-] [5]

1. consommer(IDENTIFIANT) → variable = "x"
2. consommer(AFFECTATION) → OK, c'est <-
3. parseExpression() → Retourne NombreNode(5)
4. Retourner AffectationNode("x", NombreNode(5))
```

### Précédence des Opérateurs (Très Important!)

Le parser utilise **6 niveaux de précédence** pour évaluer correctement les expressions:

```
Niveau 1: Disjunction (OU)        ← Moins prioritaire
Niveau 2: Conjunction (ET)
Niveau 3: Comparaison (>, <, ==, !=, >=, <=)
Niveau 4: Expression (+, -)
Niveau 5: Terme (*, /, %)
Niveau 6: Facteur (NON, nombre, variable, parenthèses)  ← Plus prioritaire
```

**Méthodes correspondantes**:
```java
parseCondition()      → parseDisjunction()
                      → parseConjunction()
                      → parseComparaison()
                      → parseExpression()
                      → parseTerme()
                      → parseFacteur()
```

**Exemple: Évaluation de `2 + 3 * 4`**

```
parseExpression()
├── parseTerme() → 2
├── operator: +
└── parseTerme()
    ├── parseFacteur() → 3
    ├── operator: *
    └── parseFacteur() → 4

Résultat: ExpressionBinaire(2, +, ExpressionBinaire(3, *, 4))
        = 2 + (3 * 4)  ✅ Correct!
```

---

## LA FORME D'ARBRE (AST) OBTENUE

### Structure Générale

L'AST est un **arbre hiérarchique** où:
- **Racine** = Programme entier
- **Nœuds internes** = Structures (SI, POUR, blocs)
- **Feuilles** = Valeurs atomiques (nombres, variables, chaînes)

### Les 15 Types de Nœuds

```
Node (classe abstraite)
├── Nœuds de structure
│   ├── ProgrammeNode        → Programme complet
│   ├── BlockNode            → Bloc d'instructions
│   └── DeclarationNode      → Déclaration variable
├── Nœuds d'instructions
│   ├── AffectationNode      → x <- valeur
│   ├── SiNode               → SI...ALORS...SINON...FINSI
│   ├── TantQueNode          → TANTQUE...FINTANTQUE
│   ├── PourNode             → POUR...FINPOUR
│   ├── EcrireNode           → ECRIRE(...)
│   └── LireNode             → LIRE(...)
├── Nœuds d'expressions
│   ├── ExpressionBinaire    → a + b, x > y, etc.
│   └── ExpressionUnaire     → NON condition
└── Nœuds de valeurs
    ├── NombreNode           → 123
    ├── NombreReelNode       → 3.14
    ├── ChaineNode           → "Hello"
    └── IdentifiantNode      → x
```

### 1. ProgrammeNode - Racine de l'Arbre

```java
class ProgrammeNode {
    String nom;                          // "Fibonacci"
    List<DeclarationNode> declarations;  // Variables
    BlockNode corps;                     // Instructions du DEBUT...FIN
}
```

**Représentation**:
```
ProgrammeNode
├── nom: "Fibonacci"
├── déclarations: List[DeclarationNode]
│   ├── n: ENTIER
│   ├── a: ENTIER
│   └── b: ENTIER
└── corps: BlockNode
    └── instructions: List[Node]
        ├── AffectationNode(...)
        ├── LireNode(...)
        ├── PourNode(...)
        └── ...
```

### 2. BlockNode - Conteneur d'Instructions

```java
class BlockNode {
    List<Node> instructions;  // Liste des instructions
}
```

Un bloc peut contenir:
- AffectationNode
- SiNode
- TantQueNode
- PourNode
- EcrireNode
- LireNode

### 3. AffectationNode - Assignement

```java
class AffectationNode {
    String variable;  // "x"
    Node valeur;      // L'expression à droite de <-
}
```

**Exemple**: `x <- (5 + 3)`
```
AffectationNode
├── variable: "x"
└── valeur: ExpressionBinaire
    ├── gauche: NombreNode(5)
    ├── opérateur: "+"
    └── droite: NombreNode(3)
```

### 4. SiNode - Conditionnelle

```java
class SiNode {
    Node condition;          // La condition
    BlockNode blocAlors;     // Instructions du ALORS
    BlockNode blocSinon;     // Instructions du SINON (peut être null)
}
```

**Exemple**:
```
SI x > 0 ALORS
    ECRIRE("positif")
SINON
    ECRIRE("négatif")
FINSI
```

**Arbre**:
```
SiNode
├── condition: ExpressionBinaire
│   ├── gauche: IdentifiantNode("x")
│   ├── opérateur: ">"
│   └── droite: NombreNode(0)
├── blocAlors: BlockNode
│   └── EcrireNode([ChaineNode("positif")])
└── blocSinon: BlockNode
    └── EcrireNode([ChaineNode("négatif")])
```

### 5. PourNode - Boucle FOR

```java
class PourNode {
    String variable;     // "i"
    Node debut;          // Expression de début (1)
    Node fin;            // Expression de fin (10)
    BlockNode corps;     // Instructions
}
```

**Exemple**:
```
POUR i DE 1 A 10 FAIRE
    ECRIRE(i)
FINPOUR
```

**Arbre**:
```
PourNode
├── variable: "i"
├── debut: NombreNode(1)
├── fin: NombreNode(10)
└── corps: BlockNode
    └── EcrireNode([IdentifiantNode("i")])
```

### 6. ExpressionBinaire - Opération Binaire

```java
class ExpressionBinaire {
    Node gauche;         // Opérande gauche
    String operateur;    // "+", "-", "*", "/", "%", ">", "<", "==", "ET", "OU", etc.
    Node droite;         // Opérande droite
}
```

**Exemple**: `a + b * c`

```
ExpressionBinaire
├── gauche: IdentifiantNode("a")
├── opérateur: "+"
└── droite: ExpressionBinaire
    ├── gauche: IdentifiantNode("b")
    ├── opérateur: "*"
    └── droite: IdentifiantNode("c")

Interprétation: a + (b * c)  ✅
```

### 7. EcrireNode - Sortie

```java
class EcrireNode {
    List<Node> expressions;  // Les expressions à afficher
}
```

**Exemple**: `ECRIRE("Result: ", x, y + 5)`

```
EcrireNode
└── expressions: List[Node]
    ├── ChaineNode("Result: ")
    ├── IdentifiantNode("x")
    └── ExpressionBinaire
        ├── gauche: IdentifiantNode("y")
        ├── opérateur: "+"
        └── droite: NombreNode(5)
```

---

## EXEMPLES COMPLETS AVEC VISUALISATIONS

### EXEMPLE 1: Affectation Simple

**Code source**:
```
ALGORITHME Simple

VARIABLES
    x: ENTIER

DEBUT
    x <- 42
FIN
```

**Tokens (du Lexer)**:
```
0: ALGORITHME
1: IDENTIFIANT: "Simple"
2: VARIABLES
3: IDENTIFIANT: "x"
4: DEUX_POINTS: ":"
5: ENTIER
6: DEBUT
7: IDENTIFIANT: "x"
8: AFFECTATION: "<-"
9: NOMBRE: "42"
10: FIN
11: EOF
```

**Parsing étape par étape**:

```
parseProgramme() [position 0]
├── consommer(ALGORITHME) → position 1
├── consommer(IDENTIFIANT) → "Simple", position 2
├── consommer(VARIABLES) → position 3
├── parseDeclarations()
│   └── parseDeclaration()
│       ├── consommer(IDENTIFIANT) → "x", position 4
│       ├── consommer(DEUX_POINTS) → position 5
│       ├── consommer(ENTIER) → position 6
│       └── Retourner DeclarationNode("x", "ENTIER")
├── consommer(DEBUT) → position 7
├── parseInstructions() [position 7]
│   └── parseInstruction() [position 7]
│       └── verifier(IDENTIFIANT) → TRUE
│           └── parseAffectation() [position 7]
│               ├── consommer(IDENTIFIANT) → "x", position 8
│               ├── consommer(AFFECTATION) → position 9
│               ├── parseExpression() [position 9]
│               │   └── parseTerme() [position 9]
│               │       └── parseFacteur() [position 9]
│               │           ├── verifier(NOMBRE) → TRUE
│               │           ├── Créer NombreNode(42)
│               │           └── position 10
│               │   └── Retourner NombreNode(42)
│               └── Retourner AffectationNode("x", NombreNode(42))
├── consommer(FIN) → position 11
└── Retourner ProgrammeNode("Simple", [DeclarationNode("x", "ENTIER")], BlockNode([AffectationNode(...)]))
```

**Arbre Syntaxique Abstrait (AST) Résultant**:

```
ProgrammeNode ("Simple")
│
├── Déclarations: [
│   └── DeclarationNode
│       ├── nom: "x"
│       └── type: "ENTIER"
│   ]
│
└── Corps: BlockNode
    └── Instructions: [
        └── AffectationNode
            ├── variable: "x"
            └── valeur: NombreNode(42)
        ]
```

---

### EXEMPLE 2: Boucle FOR Complète

**Code source**:
```
ALGORITHME Boucle

VARIABLES
    i: ENTIER
    sum: ENTIER

DEBUT
    sum <- 0
    POUR i DE 1 A 5 FAIRE
        sum <- (sum + i)
    FINPOUR
    ECRIRE("Total: ", sum)
FIN
```

**Arbre Syntaxique Complet**:

```
ProgrammeNode ("Boucle")
│
├── Déclarations:
│   ├── DeclarationNode("i", "ENTIER")
│   └── DeclarationNode("sum", "ENTIER")
│
└── Corps: BlockNode
    └── Instructions:
        ├─ [1] AffectationNode
        │       ├── variable: "sum"
        │       └── valeur: NombreNode(0)
        │
        ├─ [2] PourNode
        │       ├── variable: "i"
        │       ├── debut: NombreNode(1)
        │       ├── fin: NombreNode(5)
        │       └── corps: BlockNode
        │           └── Instructions:
        │               └─ AffectationNode
        │                   ├── variable: "sum"
        │                   └── valeur: ExpressionBinaire
        │                       ├── gauche: IdentifiantNode("sum")
        │                       ├── opérateur: "+"
        │                       └── droite: IdentifiantNode("i")
        │
        └─ [3] EcrireNode
                └── expressions:
                    ├── ChaineNode("Total: ")
                    └── IdentifiantNode("sum")
```

**Visualisation graphique**:

```
                    Programme
                  /    |     \
            "Boucle"  Décl.  Corps
                      / \      |
                     i  sum   BlockNode
                              /  |  \
                            Aff Boucle Ecrire
                            |     |      |
                         sum←0   Pour   "Total:"
                                  |      sum
                               i:1-5
                                  |
                              BlockNode
                                 |
                              Aff(sum←sum+i)
```

---

### EXEMPLE 3: Condition Complexe

**Code source**:
```
ALGORITHME Condition

VARIABLES
    x: ENTIER
    y: ENTIER

DEBUT
    SI (x > 5 ET y < 10) OU x == 0 ALORS
        ECRIRE("Condition vraie")
    FINSI
FIN
```

**Arbre de la condition**:

```
ExpressionBinaire (OU)
├── gauche: ExpressionBinaire (ET)
│   ├── gauche: ExpressionBinaire (>)
│   │   ├── gauche: IdentifiantNode("x")
│   │   ├── opérateur: ">"
│   │   └── droite: NombreNode(5)
│   ├── opérateur: "ET"
│   └── droite: ExpressionBinaire (<)
│       ├── gauche: IdentifiantNode("y")
│       ├── opérateur: "<"
│       └── droite: NombreNode(10)
├── opérateur: "OU"
└── droite: ExpressionBinaire (==)
    ├── gauche: IdentifiantNode("x")
    ├── opérateur: "=="
    └── droite: NombreNode(0)
```

**Représentation textuelle de la priorité**:
```
              OU (priorité 1 - basse)
             /  \
           ET(2) ==
          / \    / \
        > < x   0
       / \ / \
      x 5 y 10

Priorités:
1. OU     (le moins lié)
2. ET
3. Comparaison (>, <, ==)
4. (aucun +/- dans cet exemple)
5. (aucun */% dans cet exemple)
6. Facteurs (variables, nombres)
```

---

### EXEMPLE 4: Expression Arithmétique Complexe

**Code source**:
```
ALGORITHME Math

VARIABLES
    result: REEL

DEBUT
    result <- 2 + 3 * 4 - 1
FIN
```

**Parsing de l'expression `2 + 3 * 4 - 1`**:

```
parseExpression() [2 + 3 * 4 - 1]
├── parseTerme() [2]
│   └── parseFacteur() → NombreNode(2)
├── opérateur: +
├── parseTerme() [3 * 4]
│   ├── parseFacteur() → NombreNode(3)
│   ├── opérateur: *
│   └── parseFacteur() → NombreNode(4)
│   → ExpressionBinaire(3, *, 4)
├── opérateur: -
└── parseTerme() [1]
    └── parseFacteur() → NombreNode(1)

Résultat:
ExpressionBinaire(ExpressionBinaire(2, +, ExpressionBinaire(3, *, 4)), -, 1)
```

**Arbre**:
```
            ExpressionBinaire (-)
           /                    \
    ExpressionBinaire (+)    NombreNode(1)
    /                  \
 Nombre(2)   ExpressionBinaire (*)
              /                 \
          Nombre(3)          Nombre(4)

Évaluation:
2 + (3 * 4) - 1
= 2 + 12 - 1
= 14 - 1
= 13  ✅
```

---

## RÉSUMÉ: PARSER EN ACTION

### Flux Complet

```
ÉTAPE 1: Lexer
Entrée: Chaîne de caractères
        "ALGORITHME Test VARIABLES x: ENTIER DEBUT x <- 5 FIN"
Sortie: Tokens
        [ALGORITHME, IDENTIFIANT, VARIABLES, IDENTIFIANT, :, ENTIER, DEBUT, ...]

ÉTAPE 2: Parser.analyser()
Entrée: Tokens
Processus:
  - parseProgramme() commence
  - Consomme ALGORITHME
  - Récupère le nom du programme
  - Consomme VARIABLES
  - Parse les déclarations
  - Consomme DEBUT
  - Parse les instructions (recursivement)
  - Consomme FIN
  - Retourne le ProgrammeNode racine

Sortie: AST (Arbre)
ProgrammeNode
├── nom: "Test"
├── déclarations: [x: ENTIER]
└── corps: BlockNode[x <- 5]

ÉTAPE 3: SemanticAnalyzer
Entrée: AST
Processus: Valide la sémantique
Sortie: Validation (OK/Erreurs)

ÉTAPE 4: PythonGenerator
Entrée: AST validé
Sortie: Code Python
        x = 0
        x = 5
        print(x)
```

### Méthodes Clés du Parser Rappel

| Méthode | Récurrence? | Crée quel nœud? |
|---------|-----------|-----------------|
| `parseProgramme()` | NON | ProgrammeNode |
| `parseDeclarations()` | OUI (pour chaque var) | DeclarationNode |
| `parseInstructions()` | OUI (pour chaque instr) | BlockNode |
| `parseInstruction()` | NON (dispatch) | Selon type |
| `parseAffectation()` | NON | AffectationNode |
| `parseSi()` | OUI (récurse corps) | SiNode |
| `parseTantQue()` | OUI (récurse corps) | TantQueNode |
| `parsePour()` | OUI (récurse corps) | PourNode |
| `parseEcrire()` | NON | EcrireNode |
| `parseExpression()` | OUI (récurse) | ExpressionBinaire |
| `parseTerme()` | OUI (récurse) | ExpressionBinaire |
| `parseFacteur()` | OUI (pour NON) | Nombre/Chaîne/Identif/Expr |

### Récursivité: Comment ça Marche?

**Récursion descendante** (Recursive Descent):
- Chaque nœud non-terminal peut contenir d'autres nœuds
- Les méthodes s'appellent récursivement pour construire l'arbre

**Exemple de récursion**:
```
parseInstructions()
├── while (not end) {
│   ├── parseInstruction()  ← Peut créer un SI
│   └── SI appelle parseInstructions()  ← Récursion!
│       └── while (not FINSI) {
│           └── parseInstruction()
│           └── ...
│           }
│   }
└── Retourner BlockNode avec toutes les instructions
```

---

## CONCLUSION

### Le Parser en 3 Points

1. **Entrée**: Une liste ordonnée de tokens (du Lexer)
2. **Processus**: Parcourt les tokens et construit une structure arborescente
3. **Sortie**: Un AST (Arbre Syntaxique Abstrait) représentant le code source

### L'Arbre (AST) C'est Quoi?

- **Structure hiérarchique** où chaque nœud représente une partie du code
- **Feuilles**: Valeurs atomiques (nombres, variables, chaînes)
- **Nœuds internes**: Structures (instructions, opérations)
- **Racine**: Programme complet

### Ensuite?

Après le Parser:
1. **SemanticAnalyzer** valide que l'arbre a du sens (types corrects, variables déclarées, etc.)
2. **PythonGenerator** parcourt l'arbre et génère du code Python exécutable

C'est la puissance de l'AST: il permet à chaque phase de comprendre et transformer le code de manière structurée!
