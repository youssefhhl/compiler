# Documentation Complète du Compilateur Pseudo-code vers Python

## Table des matières
1. [Lexique](#lexique)
2. [Règles Lexicales](#règles-lexicales)
3. [Règles Syntaxiques](#règles-syntaxiques)
4. [Règles Sémantiques](#règles-sémantiques)

---

## Lexique

### Mots-clés Réservés

#### Structure du Programme
| Mot-clé | Description | Exemple |
|---------|-------------|---------|
| `ALGORITHME` | Début du programme | `ALGORITHME MonAlgorithme` |
| `VARIABLES` | Section de déclaration des variables | `VARIABLES` |
| `DEBUT` | Début du bloc principal ou fonction | `DEBUT` |
| `FIN` | Fin du programme | `FIN` |

#### Définition de Fonctions
| Mot-clé | Description | Exemple |
|---------|-------------|---------|
| `FONCTION` | Définition d'une fonction avec retour | `FONCTION Add(a: ENTIER) RETOURNE ENTIER` |
| `PROCEDURE` | Définition d'une procédure (sans retour) | `PROCEDURE PrintMsg(msg: TEXTE)` |
| `RETOURNE` | Déclaration du type de retour | `RETOURNE ENTIER` |
| `FINFONCTION` | Fin de fonction | `FINFONCTION` |
| `FINPROCEDURE` | Fin de procédure | `FINPROCEDURE` |

#### Types de Données
| Mot-clé | Type | Description | Exemple Python |
|---------|------|-------------|-----------------|
| `ENTIER` | Integer | Nombre entier | `int` |
| `REEL` | Float | Nombre à virgule flottante | `float` |
| `TEXTE` | String | Chaîne de caractères | `str` |
| `BOOLEEN` | Boolean | Valeur booléenne | `bool` |

#### Valeurs Booléennes
| Mot-clé | Valeur | Python |
|---------|--------|--------|
| `VRAI` | True | `True` |
| `FAUX` | False | `False` |

#### Entrées/Sorties
| Mot-clé | Description | Alias |
|---------|-------------|-------|
| `ECRIRE` | Affichage à l'écran | `AFFICHER` |
| `LIRE` | Lecture d'entrée utilisateur | — |

#### Structures Conditionnelles
| Mot-clé | Description | Structure |
|---------|-------------|-----------|
| `SI` | Condition if | `SI condition ALORS ... FINSI` |
| `ALORS` | Début du bloc then | — |
| `SINON` | Bloc else | — |
| `FINSI` | Fin de la condition | — |

#### Boucles
| Mot-clé | Type | Structure |
|---------|------|-----------|
| `TANTQUE` | While | `TANTQUE condition FAIRE ... FINTANTQUE` |
| `FAIRE` | Début du bloc | — |
| `FINTANTQUE` | Fin while | — |
| `POUR` | For | `POUR i DE 1 A 10 FAIRE ... FINPOUR` |
| `DE` | From (for) | — |
| `A` | To (for) | — |
| `FINPOUR` | Fin for | — |

#### Switch/Case
| Mot-clé | Description | Structure |
|---------|-------------|-----------|
| `CAS` | Switch statement | `CAS expr FAIRE ... FINCAS` |
| `DEFAUT` | Default case | — |
| `FINCAS` | Fin du switch | — |

#### Opérateurs Logiques
| Mot-clé | Opérateur | Python |
|---------|-----------|--------|
| `ET` | AND (logical and) | `and` |
| `OU` | OR (logical or) | `or` |
| `NON` | NOT (logical not) | `not` |

### Opérateurs

#### Opérateurs Arithmétiques
| Opérateur | Opération | Exemple | Résultat |
|-----------|-----------|---------|----------|
| `+` | Addition | `5 + 3` | `8` |
| `-` | Soustraction | `5 - 3` | `2` |
| `*` | Multiplication | `5 * 3` | `15` |
| `/` | Division | `6 / 2` | `3` |
| `%` | Modulo | `7 % 3` | `1` |

#### Opérateurs de Comparaison
| Opérateur | Description | Python | Résultat |
|-----------|-------------|--------|----------|
| `==` | Égal à | `==` | BOOLEEN |
| `!=` | Non égal à | `!=` | BOOLEEN |
| `<` | Inférieur à | `<` | BOOLEEN |
| `>` | Supérieur à | `>` | BOOLEEN |
| `<=` | Inférieur ou égal | `<=` | BOOLEEN |
| `>=` | Supérieur ou égal | `>=` | BOOLEEN |

#### Opérateurs Spéciaux
| Opérateur | Description | Contexte |
|-----------|-------------|----------|
| `<-` | Affectation | Assignation de valeurs |
| `[` `]` | Accès tableau | Indexing d'arrays |
| `(` `)` | Parenthèses | Groupement, appels fonction |
| `:` | Deux-points | Déclaration de type |
| `,` | Virgule | Séparation d'arguments |

---

## Règles Lexicales

### 1. Tokens Élémentaires

#### Identifiants
- **Définition**: Noms de variables, fonctions, procédures
- **Syntaxe**: `[a-zA-Z_][a-zA-Z0-9_]*`
- **Exemples**: `x`, `nombre`, `_temp`, `moyenne_notes`
- **Remarque**: Sensible à la casse (case-sensitive pour les identifiants)

#### Nombres Entiers
- **Définition**: Constantes numériques entières
- **Syntaxe**: `[0-9]+`
- **Exemples**: `0`, `42`, `1000`, `999999`
- **Type**: `ENTIER`

#### Nombres Réels
- **Définition**: Constantes numériques avec partie décimale
- **Syntaxe**: `[0-9]+\.[0-9]+`
- **Exemples**: `3.14`, `0.5`, `2.718`
- **Type**: `REEL`

#### Chaînes de Caractères
- **Définition**: Texte entre guillemets doubles
- **Syntaxe**: `"[^"]*"`
- **Exemples**: `"Bonjour"`, `"Message d'erreur"`, `"123"`
- **Type**: `TEXTE`

#### Commentaires
- **Définition**: Texte ignoré par le compilateur
- **Syntaxe**: `//` suivi du texte jusqu'à fin de ligne
- **Exemples**: `// Ceci est un commentaire`
- **Action**: Ignoré complètement par le lexer

### 2. Symboles et Ponctuation

#### Symboles Simples
| Symbole | Nom | Utilisation |
|---------|-----|-------------|
| `(` | Parenthèse gauche | Appels fonction, groupement |
| `)` | Parenthèse droite | Appels fonction, groupement |
| `[` | Crochet gauche | Accès tableau |
| `]` | Crochet droit | Accès tableau |
| `:` | Deux-points | Déclaration de type |
| `,` | Virgule | Séparation arguments |

#### Symboles Composés
| Symbole | Nom | Utilisation |
|---------|-----|-------------|
| `<-` | Affectation | Assignation de valeur |
| `==` | Égalité | Comparaison |
| `!=` | Différence | Comparaison |
| `<=` | Inférieur ou égal | Comparaison |
| `>=` | Supérieur ou égal | Comparaison |

### 3. Espaces et Nouvelle Lignes

- **Espaces**: Ignorés (servent de séparateurs de tokens)
- **Tabulations**: Ignorées (équivalentes à des espaces)
- **Nouvelle ligne**: Token `NOUVELLE_LIGNE` (parfois ignorée)
- **Indentation**: Utilisée pour la lisibilité, non significative pour la compilation

### 4. Sensibilité de la Casse

- **Mots-clés**: Insensibles à la casse
  - `ALGORITHME` = `algorithme` = `Algorithme`
  - `VARIABLES` = `variables` = `Variables`
- **Identifiants**: Sensibles à la casse
  - `x` ≠ `X`
  - `nombre` ≠ `Nombre`
- **Chaînes**: Sensibles à la casse
  - `"Hello"` ≠ `"hello"`

---

## Règles Syntaxiques

### 1. Structure Générale du Programme

```ebnf
programme ::= ALGORITHME identifiant
              VARIABLES declarations
              (FONCTION | PROCEDURE)*
              DEBUT bloc_principal
              FIN
```

**Exemple**:
```
ALGORITHME MonProgram
VARIABLES
    x: ENTIER
    y: TEXTE

FONCTION Add(a: ENTIER, b: ENTIER) RETOURNE ENTIER
    RETOURNE (a + b)
FINFONCTION

DEBUT
    x <- 10
    y <- "Résultat"
    ECRIRE(Add(3, 5))
FIN
```

### 2. Déclarations de Variables

```ebnf
declarations ::= (declaration)*

declaration ::= variable_simple | array_declaration

variable_simple ::= IDENTIFIANT : type

array_declaration ::= IDENTIFIANT [ NOMBRE ] : type

type ::= ENTIER | REEL | TEXTE | BOOLEEN
```

**Exemples**:
```
VARIABLES
    nombre: ENTIER                    // Variable simple
    prix: REEL                        // Variable réelle
    message: TEXTE                    // Chaîne de caractères
    actif: BOOLEEN                    // Booléen
    notes[10]: ENTIER                 // Tableau d'entiers
    points[5]: REEL                   // Tableau de réels
```

### 3. Définitions de Fonctions et Procédures

#### Fonction avec Retour
```ebnf
fonction ::= FONCTION IDENTIFIANT ( parametres ) RETOURNE type
             corps_fonction
             FINFONCTION

corps_fonction ::= instruction*
```

**Exemple**:
```
FONCTION Maximum(a: ENTIER, b: ENTIER) RETOURNE ENTIER
    SI (a > b) ALORS
        RETOURNE a
    SINON
        RETOURNE b
    FINSI
FINFONCTION
```

#### Procédure sans Retour
```ebnf
procedure ::= PROCEDURE IDENTIFIANT ( parametres )
              corps_procedure
              FINPROCEDURE
```

**Exemple**:
```
PROCEDURE PrintMsg(msg: TEXTE)
    ECRIRE("Message: ", msg)
FINPROCEDURE
```

#### Paramètres
```ebnf
parametres ::= (parametre (VIRGULE parametre)*)?

parametre ::= IDENTIFIANT : type
```

### 4. Bloc d'Instructions

```ebnf
bloc_instructions ::= instruction*

instruction ::= affectation
               | conditionnelle
               | boucle_while
               | boucle_for
               | switch
               | ecrire
               | lire
               | retour
               | appel_fonction
```

### 5. Instructions Élémentaires

#### Affectation
```ebnf
affectation ::= IDENTIFIANT <- expression
               | IDENTIFIANT [ expression ] <- expression
```

**Exemples**:
```
x <- 42                    // Affectation simple
prix <- 19.99              // Affectation réelle
tableau[0] <- 100          // Affectation tableau
actif <- VRAI              // Affectation booléenne
```

#### ECRIRE (Affichage)
```ebnf
ecrire ::= ECRIRE ( expression (VIRGULE expression)* )
```

**Exemples**:
```
ECRIRE("Bonjour")
ECRIRE("Nombre: ", x)
ECRIRE("Résultat: ", a + b)
ECRIRE(tableau[0], " ", tableau[1])
```

#### LIRE (Entrée)
```ebnf
lire ::= LIRE ( IDENTIFIANT )
        | LIRE ( IDENTIFIANT [ expression ] )
```

**Exemples**:
```
LIRE(x)                    // Lire une variable
LIRE(tableau[i])           // Lire dans un tableau
```

### 6. Structures Conditionnelles

#### IF/THEN/ELSE
```ebnf
si ::= SI condition ALORS
       bloc_instructions
       (SINON bloc_instructions)?
       FINSI

condition ::= expression
```

**Exemple**:
```
SI (age >= 18) ALORS
    ECRIRE("Majeur")
SINON
    ECRIRE("Mineur")
FINSI
```

#### SWITCH/CASE
```ebnf
switch ::= CAS expression FAIRE
           (cas_item)*
           (DEFAUT DEUX_POINTS bloc_instructions)?
           FINCAS

cas_item ::= NOMBRE DEUX_POINTS bloc_instructions
```

**Exemple**:
```
CAS jour FAIRE
    1: ECRIRE("Lundi")
    2: ECRIRE("Mardi")
    3: ECRIRE("Mercredi")
    DEFAUT: ECRIRE("Autre jour")
FINCAS
```

### 7. Boucles

#### TANTQUE (While)
```ebnf
tantque ::= TANTQUE condition FAIRE
            bloc_instructions
            FINTANTQUE
```

**Exemple**:
```
i <- 1
TANTQUE (i <= 10) FAIRE
    ECRIRE(i)
    i <- (i + 1)
FINTANTQUE
```

#### POUR (For)
```ebnf
pour ::= POUR IDENTIFIANT DE expression A expression FAIRE
         bloc_instructions
         FINPOUR
```

**Exemple**:
```
POUR i DE 1 A 10 FAIRE
    ECRIRE(i)
FINPOUR
```

### 8. Expressions

```ebnf
expression ::= expr_logique

expr_logique ::= expr_comparaison ((ET | OU) expr_comparaison)*

expr_comparaison ::= expr_additive ((== | != | < | > | <= | >=) expr_additive)?

expr_additive ::= expr_multiplicative ((PLUS | MOINS) expr_multiplicative)*

expr_multiplicative ::= expr_unaire ((MULTIPLIE | DIVISE | MODULO) expr_unaire)*

expr_unaire ::= (NON)? expr_postfixe

expr_postfixe ::= facteur (appel_fonction | acces_tableau)*

facteur ::= NOMBRE
          | NOMBRE_REEL
          | CHAINE
          | VRAI
          | FAUX
          | IDENTIFIANT
          | appel_fonction
          | PARENTHESE_G expression PARENTHESE_D

appel_fonction ::= IDENTIFIANT ( arguments )

arguments ::= (expression (VIRGULE expression)*)?

acces_tableau ::= CROCHET_G expression CROCHET_D
```

### 9. Priorité des Opérateurs

De la plus basse à la plus haute priorité:

| Priorité | Opérateurs | Associativité |
|----------|-----------|---------------|
| 1 (basse) | `OU` | Gauche |
| 2 | `ET` | Gauche |
| 3 | `==`, `!=` | Gauche |
| 4 | `<`, `>`, `<=`, `>=` | Gauche |
| 5 | `+`, `-` | Gauche |
| 6 | `*`, `/`, `%` | Gauche |
| 7 (haute) | `NON`, `()` | Droite |

---

## Règles Sémantiques

### 1. Déclaration et Utilisation de Variables

#### Règle 1.1: Déclaration avant utilisation
- **Énoncé**: Toute variable doit être déclarée dans la section `VARIABLES` avant d'être utilisée.
- **Violation**: Erreur "Variable non déclarée"
- **Exemple valide**:
```
VARIABLES
    x: ENTIER
DEBUT
    x <- 42    // OK: x est déclaré
```
- **Exemple invalide**:
```
DEBUT
    y <- 42    // ERREUR: y n'est pas déclaré
```

#### Règle 1.2: Unicité des noms
- **Énoncé**: Aucune variable ne peut être déclarée deux fois dans le même scope.
- **Violation**: Erreur "Variable déjà déclarée"
- **Exemple invalide**:
```
VARIABLES
    x: ENTIER
    x: ENTIER  // ERREUR: x déjà déclaré
```

#### Règle 1.3: Scope des variables
- **Énoncé**: Les variables déclarées dans `VARIABLES` sont globales.
- **Portée**: Accessibles partout dans le programme et ses fonctions
- **Paramètres de fonction**: Locaux à la fonction

### 2. Système de Types

#### Règle 2.1: Compatibilité des types
- **Énoncé**: Le type de la valeur affectée doit être compatible avec le type de la variable.
- **Violat**: Erreur "Incompatibilité de types"
- **Exemples**:
```
VARIABLES
    x: ENTIER
    y: TEXTE
DEBUT
    x <- 42        // OK: ENTIER <- ENTIER
    y <- "Bonjour" // OK: TEXTE <- TEXTE
    x <- "test"    // ERREUR: ENTIER <- TEXTE
    y <- 100       // ERREUR: TEXTE <- ENTIER
```

#### Règle 2.2: Types d'expressions booléennes
- **Énoncé**: Les opérateurs de comparaison retournent des valeurs `BOOLEEN`.
- **Opérateurs**: `==`, `!=`, `<`, `>`, `<=`, `>=`
- **Exemples**:
```
VARIABLES
    cond: BOOLEEN
DEBUT
    cond <- (5 > 3)     // OK: booléen
    cond <- (10 == 10)  // OK: booléen
    cond <- VRAI        // OK: littéral booléen
```

#### Règle 2.3: Opérateurs arithmétiques
- **Énoncé**: Les opérateurs `+`, `-`, `*`, `/`, `%` ne s'appliquent qu'à `ENTIER` ou `REEL`.
- **Résultat**: `REEL` si au moins un opérande est `REEL`, sinon `ENTIER`
- **Exemples valides**:
```
x <- 5 + 3         // OK: 8 (ENTIER)
y <- 5.0 + 3       // OK: 8.0 (REEL)
z <- 5.0 + 3.0     // OK: 8.0 (REEL)
```
- **Exemples invalides**:
```
w <- "texte" + 5   // ERREUR: opérateur + non applicable à TEXTE
```

#### Règle 2.4: Opérateurs logiques
- **Énoncé**: Les opérateurs `ET`, `OU` s'appliquent à des expressions booléennes.
- **Résultat**: `BOOLEEN`
- **Exemples**:
```
SI ((x > 5) ET (y < 10)) ALORS
    ECRIRE("Condition true")
FINSI
```

### 3. Conditions et Structures de Contrôle

#### Règle 3.1: Expression dans SI
- **Énoncé**: La condition du `SI` doit être une expression valide (type n'importe lequel).
- **Évaluation**: En Python, toute expression est évaluée comme booléenne
- **Exemple**:
```
SI (x) ALORS           // x traité comme booléen
    ECRIRE("OK")
FINSI
```

#### Règle 3.2: Boucles POUR
- **Énoncé**: Les expressions de début et fin doivent être numériques (`ENTIER` ou `REEL`).
- **Violation**: Erreur "Type non valide pour boucle FOR"
- **Exemple valide**:
```
POUR i DE 1 A 10 FAIRE
    ECRIRE(i)
FINPOUR
```
- **Exemple invalide**:
```
POUR i DE "a" A "z" FAIRE   // ERREUR: TEXTE invalide
```

#### Règle 3.3: Variable de boucle POUR
- **Énoncé**: La variable de boucle peut être n'importe quel identifiant.
- **Scope**: Existant pour toute la boucle

#### Règle 3.4: Switch/CAS
- **Énoncé**: Les cas doivent être des constantes entières.
- **Expression switch**: Toute expression valide
- **Exemple valide**:
```
CAS jour FAIRE
    1: ECRIRE("Lundi")
    2: ECRIRE("Mardi")
    DEFAUT: ECRIRE("Autre")
FINCAS
```

### 4. Fonctions et Procédures

#### Règle 4.1: Signature de fonction
- **Énoncé**: La signature comprend nom, paramètres et type de retour.
- **Signature unique**: Aucune redéfinition
- **Exemple**:
```
FONCTION Add(a: ENTIER, b: ENTIER) RETOURNE ENTIER
    RETOURNE (a + b)
FINFONCTION
```

#### Règle 4.2: Appel de fonction
- **Énoncé**: Le nombre et les types d'arguments doivent correspondre aux paramètres.
- **Ordre**: Les arguments sont passés dans l'ordre
- **Exemple**:
```
resultat <- Add(3, 5)      // OK: 2 arguments ENTIER
resultat <- Add(3)         // ERREUR: 1 argument au lieu de 2
resultat <- Add("a", "b")  // ERREUR: types invalides
```

#### Règle 4.3: Type de retour
- **Énoncé**: Le type de la valeur retournée doit correspondre au type déclaré.
- **Fonction**: Doit avoir au moins un `RETOURNE`
- **Procédure**: Pas de `RETOURNE` (ou `RETOURNE` sans valeur)
- **Exemple**:
```
FONCTION GetAge() RETOURNE ENTIER
    RETOURNE 25        // OK: retourne ENTIER
FINFONCTION

PROCEDURE PrintAge()
    ECRIRE(25)         // OK: pas de retour
FINPROCEDURE
```

#### Règle 4.4: Récursivité
- **Énoncé**: Une fonction peut s'appeler elle-même (récursion).
- **Condition d'arrêt**: Doit être présente pour éviter les boucles infinies
- **Exemple**:
```
FONCTION Factorial(n: ENTIER) RETOURNE ENTIER
    SI (n <= 1) ALORS
        RETOURNE 1
    SINON
        RETOURNE (n * Factorial((n - 1)))
    FINSI
FINFONCTION
```

### 5. Tableaux

#### Règle 5.1: Taille du tableau
- **Énoncé**: La taille du tableau doit être un nombre positif > 0.
- **Syntaxe**: `nom[taille]: type`
- **Exemple valide**:
```
VARIABLES
    notes[10]: ENTIER      // OK: taille 10
    scores[1]: REEL        // OK: taille 1
```
- **Exemple invalide**:
```
VARIABLES
    notes[0]: ENTIER       // ERREUR: taille 0
    scores[-5]: REEL       // ERREUR: taille négative
```

#### Règle 5.2: Accès au tableau
- **Énoncé**: L'indice d'accès doit être une expression numérique valide.
- **Syntaxe**: `nom[indice]`
- **Exemple**:
```
x <- tableau[0]        // OK
x <- tableau[i]        // OK si i est défini
x <- tableau["abc"]    // ERREUR: indice doit être numérique
```

#### Règle 5.3: Affectation aux éléments du tableau
- **Énoncé**: Le type de la valeur doit correspondre au type du tableau.
- **Syntaxe**: `nom[indice] <- expression`
- **Exemple**:
```
VARIABLES
    notes[5]: ENTIER
DEBUT
    notes[0] <- 85         // OK: ENTIER
    notes[1] <- 92         // OK: ENTIER
    notes[2] <- "note"     // ERREUR: TEXTE au lieu de ENTIER
```

### 6. Expressions Valides

#### Règle 6.1: Expressions dans ECRIRE
- **Énoncé**: ECRIRE accepte n'importe quelle expression valide.
- **Exemple**:
```
ECRIRE(42)
ECRIRE("Texte")
ECRIRE(x + 5)
ECRIRE(tableau[0])
```

#### Règle 6.2: Expressions dans conditions
- **Énoncé**: Les conditions acceptent n'importe quelle expression valide.
- **Évaluation**: Convertie en booléen pour la décision

### 7. Conversion de Types Implicite

#### Règle 7.1: Pas de conversion implicite
- **Énoncé**: Les types doivent correspondre exactement (pas de coercion).
- **Exception**: Les opérateurs arithmétiques promeuvent ENTIER en REEL si nécessaire
- **Exemple**:
```
x: ENTIER
y: REEL
x <- y       // ERREUR: pas de conversion REEL -> ENTIER
y <- x       // OK: promotion ENTIER -> REEL possible en théorie, mais nous l'interdisons
```

### 8. Validité Sémantique Globale

#### Règle 8.1: Analyse sémantique avant génération
- **Énoncé**: Les erreurs sémantiques empêchent la génération de code Python.
- **Ordre**: Lexique → Syntaxe → Sémantique → Génération

#### Règle 8.2: Messages d'erreur sémantiques
- **Format**: `[Erreur sémantique] Description à la ligne X`
- **Arrêt**: Le compilateur s'arrête à la première erreur sémantique majeure

---

## Résumé des Validations

### Avant Génération de Code

1. ✅ **Lexicale**: Tous les tokens sont reconnus
2. ✅ **Syntaxique**: La structure suit la grammaire
3. ✅ **Sémantique**:
   - Toutes les variables sont déclarées
   - Les types sont compatibles
   - Les fonctions sont appelées correctement
   - Les tableaux sont utilisés validement

### Lors de la Génération

- Conversion appropriée des opérateurs vers Python
- Indentation correcte du code Python
- Noms valides en Python
- Structures de contrôle correctes

---

## Exemples Complets

### Exemple 1: Programmation Basique
```
ALGORITHME Exemple1
VARIABLES
    x: ENTIER
    y: ENTIER
    resultat: ENTIER
DEBUT
    ECRIRE("Entrez x:")
    LIRE(x)
    ECRIRE("Entrez y:")
    LIRE(y)
    resultat <- (x + y)
    ECRIRE("Somme: ", resultat)
FIN
```

### Exemple 2: Fonctions et Récursivité
```
ALGORITHME Exemple2
VARIABLES
    n: ENTIER

FONCTION Factorial(num: ENTIER) RETOURNE ENTIER
    SI (num <= 1) ALORS
        RETOURNE 1
    SINON
        RETOURNE (num * Factorial((num - 1)))
    FINSI
FINFONCTION

DEBUT
    ECRIRE("Factorial(5) = ", Factorial(5))
FIN
```

### Exemple 3: Tableaux et Boucles
```
ALGORITHME Exemple3
VARIABLES
    notes[5]: ENTIER
    i: ENTIER
    somme: ENTIER

DEBUT
    somme <- 0
    POUR i DE 0 A 4 FAIRE
        ECRIRE("Note ", (i + 1), ": ")
        LIRE(notes[i])
        somme <- (somme + notes[i])
    FINPOUR
    ECRIRE("Moyenne: ", (somme / 5))
FIN
```

### Exemple 4: Switch et Conditions
```
ALGORITHME Exemple4
VARIABLES
    jour: ENTIER

DEBUT
    ECRIRE("Quel jour? (1-7)")
    LIRE(jour)
    CAS jour FAIRE
        1: ECRIRE("Lundi")
        2: ECRIRE("Mardi")
        3: ECRIRE("Mercredi")
        4: ECRIRE("Jeudi")
        5: ECRIRE("Vendredi")
        6: ECRIRE("Samedi")
        7: ECRIRE("Dimanche")
        DEFAUT: ECRIRE("Invalide")
    FINCAS
FIN
```

---

## Version

- **Date**: 2026-02-04
- **Compiler Version**: 1.0
- **Status**: Documentation Complète
- **Features**: Lexer, Parser, Semantic Analyzer, Python Code Generator
