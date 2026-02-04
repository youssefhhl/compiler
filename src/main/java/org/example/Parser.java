package org.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Analyseur syntaxique (Parser) utilisant la méthode de descente récursive.
 * Transforme la liste de tokens en un Arbre Syntaxique Abstrait (AST).
 *
 * =====================================================
 * RÈGLES LEXICALES (Tokens reconnus par le Lexer)
 * =====================================================
 *
 * MOTS-CLÉS:
 *   - Structure: ALGORITHME, VARIABLES, DEBUT, FIN
 *   - Fonctions: FONCTION, PROCEDURE, RETOURNE, FINFONCTION, FINPROCEDURE
 *   - Types: ENTIER, REEL, TEXTE
 *   - E/S: ECRIRE, AFFICHER (alias), LIRE
 *   - Contrôle: SI, ALORS, SINON, FINSI
 *   - Boucles: TANTQUE, FAIRE, FINTANTQUE, POUR, DE, A, FINPOUR
 *   - Switch: CAS, DEFAUT, FINCAS
 *   - Logique: ET, OU, NON
 *
 * OPÉRATEURS:
 *   - Affectation: <-
 *   - Arithmétique: +, -, *, /, %
 *   - Comparaison: ==, !=, <, >, <=, >=
 *   - Logiques: ET, OU, NON
 *
 * SYMBOLES:
 *   - Parenthèses: (, )
 *   - Crochets: [, ]
 *   - Ponctuation: :, ,
 *
 * LITTÉRAUX:
 *   - Nombres: 0-9+ (NOMBRE)
 *   - Nombres réels: 0-9+.0-9+ (NOMBRE_REEL)
 *   - Chaînes: "..." (CHAINE)
 *   - Identifiants: [a-zA-Z_][a-zA-Z0-9_]* (IDENTIFIANT)
 *
 * =====================================================
 * RÈGLES SYNTAXIQUES (Grammaire complète)
 * =====================================================
 *
 * programme           -> ALGORITHME identifiant VARIABLES declarations DEBUT bloc_principal FIN
 *
 * declarations        -> (declaration)*
 * declaration         -> (array_declaration | var_declaration)
 * array_declaration   -> identifiant[nombre]: type
 * var_declaration     -> identifiant: type
 * type                -> ENTIER | REEL | TEXTE
 *
 * bloc_principal      -> (instruction)*
 *
 * fonction            -> (FONCTION | PROCEDURE) identifiant(parametres) (RETOURNE type)? corps_fonction
 * parametres          -> (param (VIRGULE param)*)?
 * param               -> identifiant: type
 * corps_fonction      -> DEBUT (instruction | RETOURNE expression)* FINFONCTION | FINPROCEDURE
 *
 * instruction         -> affectation
 *                      | si
 *                      | tantque
 *                      | pour
 *                      | cas
 *                      | ecrire
 *                      | lire
 *                      | retour
 *
 * affectation         -> identifiant <- expression
 *                      | identifiant[expression] <- expression
 *                      | identifiant(arguments)
 *
 * si                  -> SI condition ALORS bloc_instructions (SINON bloc_instructions)? FINSI
 *
 * tantque             -> TANTQUE condition FAIRE bloc_instructions FINTANTQUE
 *
 * pour                -> POUR identifiant DE expression A expression FAIRE bloc_instructions FINPOUR
 *
 * cas                 -> CAS expression FAIRE (cas_item)* (DEFAUT: bloc_instructions)? FINCAS
 * cas_item            -> nombre: bloc_instructions
 *
 * ecrire              -> ECRIRE(expression (VIRGULE expression)*)
 *
 * lire                -> LIRE(identifiant | identifiant[expression])
 *
 * retour              -> RETOURNE expression
 *
 * bloc_instructions   -> (instruction)*
 *
 * condition           -> expression
 *
 * expression          -> expr_logique
 * expr_logique        -> expr_comparaison ((ET | OU) expr_comparaison)*
 * expr_comparaison    -> expr_additive ((== | != | < | > | <= | >=) expr_additive)?
 * expr_additive       -> expr_multiplicative ((+ | -) expr_multiplicative)*
 * expr_multiplicative -> expr_unaire ((* | / | %) expr_unaire)*
 * expr_unaire         -> (NON)? expr_postfixe
 * expr_postfixe       -> facteur (appel_fonction | acces_tableau)*
 * facteur             -> nombre
 *                      | nombre_reel
 *                      | chaine
 *                      | identifiant
 *                      | PARENTHESE_G expression PARENTHESE_D
 *
 * appel_fonction      -> PARENTHESE_G (expression (VIRGULE expression)*)? PARENTHESE_D
 * acces_tableau       -> CROCHET_G expression CROCHET_D
 *
 * =====================================================
 * RÈGLES SÉMANTIQUES (Validation et type-checking)
 * =====================================================
 *
 * 1. DÉCLARATIONS:
 *    - Chaque variable/tableau doit être déclaré avant utilisation
 *    - Les noms de variables doivent être uniques dans le même scope
 *    - La taille d'un tableau doit être > 0
 *
 * 2. TYPES:
 *    - ENTIER: nombres entiers (affichés en Python comme int)
 *    - REEL: nombres à virgule flottante (affichés en Python comme float)
 *    - TEXTE: chaînes de caractères (affichés en Python comme str)
 *    - Types doivent être cohérents dans les affectations
 *
 * 3. OPÉRATEURS:
 *    - Arithmétique (+, -, *, /, %): uniquement avec ENTIER/REEL
 *    - Comparaison (==, !=, <, >, <=, >=): types doivent être compatibles
 *    - Logiques (ET, OU, NON): pour conditions booléennes
 *
 * 4. FONCTIONS:
 *    - Les appels de fonction doivent correspondre à la signature déclarée
 *    - Les fonctions avec RETOURNE type doivent retourner une valeur
 *    - Les procédures ne retournent rien
 *
 * 5. TABLEAUX:
 *    - Accès par index: array[indice] où indice >= 0 et indice < taille
 *    - Affectation: array[indice] <- valeur avec type cohérent
 *
 * 6. SWITCH/CAS:
 *    - Expression switch doit être valide
 *    - Cas doivent être des entiers constants
 *    - Chaque cas peut contenir plusieurs instructions
 *    - Cas DEFAUT est optionnel
 */
public class Parser {
    private final List<Token> tokens;
    private int position;

    /**
     * Constructeur du Parser.
     * @param tokens Liste de tokens provenant du Lexer
     */
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
    }

    /**
     * Lance l'analyse syntaxique et retourne l'AST.
     * @return Le nœud racine du programme
     */
    public AST.ProgrammeNode analyser() {
        return parseProgramme();
    }


    /**
     * Retourne le token actuel sans avancer.
     */
    private Token tokenActuel() {
        if (position >= tokens.size()) {
            return tokens.get(tokens.size() - 1); // Retourne EOF
        }
        return tokens.get(position);
    }

    /**
     * Avance au token suivant et retourne l'ancien.
     */
    private Token avancer() {
        Token token = tokenActuel();
        position++;
        while (position < tokens.size() &&
               tokens.get(position).getType() == TokenType.NOUVELLE_LIGNE) {
            position++;
        }
        return token;
    }

    /**
     * Vérifie si le token actuel est du type attendu.
     */
    private boolean verifier(TokenType type) {
        return tokenActuel().getType() == type;
    }

    /**
     * Consomme le token si c'est le type attendu, sinon lance une erreur.
     */
    private Token consommer(TokenType type, String messageErreur) {
        while (verifier(TokenType.NOUVELLE_LIGNE)) {
            position++;
        }

        if (verifier(type)) {
            return avancer();
        }

        Token t = tokenActuel();
        throw new RuntimeException(messageErreur + " à la ligne " + t.getLigne()
                + ", colonne " + t.getColonne() + ". Token trouvé: " + t.getType());
    }

    /**
     * Ignore les tokens de nouvelle ligne.
     */
    private void ignorerNouvellesLignes() {
        while (verifier(TokenType.NOUVELLE_LIGNE)) {
            position++;
        }
    }

    // ==================== RÈGLES DE GRAMMAIRE ====================

    /**
     * Parse le programme complet.
     * programme -> ALGORITHME identifiant VARIABLES declarations (FONCTION|PROCEDURE)* DEBUT instructions FIN
     */
    private AST.ProgrammeNode parseProgramme() {
        ignorerNouvellesLignes();

        consommer(TokenType.ALGORITHME, "Mot-clé 'ALGORITHME' attendu au début du programme");
        String nomAlgorithme = consommer(TokenType.IDENTIFIANT, "Nom de l'algorithme attendu").getValeur();

        ignorerNouvellesLignes();

        List<AST.Node> declarations = new ArrayList<>();
        if (verifier(TokenType.VARIABLES)) {
            avancer();
            declarations = parseDeclarations();
        }

        ignorerNouvellesLignes();

        List<AST.FunctionNode> fonctions = new ArrayList<>();
        while (verifier(TokenType.FONCTION) || verifier(TokenType.PROCEDURE)) {
            fonctions.add(parseFunction());
            ignorerNouvellesLignes();
        }

        ignorerNouvellesLignes();

        consommer(TokenType.DEBUT, "Mot-clé 'DEBUT' attendu");
        AST.BlockNode corps = parseInstructions();
        consommer(TokenType.FIN, "Mot-clé 'FIN' attendu à la fin du programme");

        return new AST.ProgrammeNode(nomAlgorithme, declarations, fonctions, corps);
    }

    /**
     * Parse les déclarations de variables et de tableaux.
     * declarations -> (declaration)*
     */
    private List<AST.Node> parseDeclarations() {
        List<AST.Node> declarations = new ArrayList<>();
        ignorerNouvellesLignes();

        while (verifier(TokenType.IDENTIFIANT)) {
            declarations.add(parseDeclaration());
            ignorerNouvellesLignes();
        }

        return declarations;
    }

    /**
     * Parse une déclaration de variable ou de tableau.
     * declaration -> identifiant : type
     * array_declaration -> identifiant[taille] : type
     */
    private AST.Node parseDeclaration() {
        String nom = consommer(TokenType.IDENTIFIANT, "Nom de variable attendu").getValeur();

        // Vérifier si c'est une déclaration de tableau
        if (verifier(TokenType.CROCHET_G)) {
            avancer();
            Token tailleToken = consommer(TokenType.NOMBRE, "Nombre attendu pour la taille du tableau");
            int taille = Integer.parseInt(tailleToken.getValeur());
            consommer(TokenType.CROCHET_D, "']' attendu après la taille du tableau");
            consommer(TokenType.DEUX_POINTS, "':' attendu après la taille du tableau");

            String type;
            if (verifier(TokenType.ENTIER)) {
                type = "ENTIER";
                avancer();
            } else if (verifier(TokenType.REEL)) {
                type = "REEL";
                avancer();
            } else if (verifier(TokenType.TEXTE)) {
                type = "TEXTE";
                avancer();
            } else if (verifier(TokenType.BOOLEEN)) {
                type = "BOOLEEN";
                avancer();
            } else {
                throw new RuntimeException("Type attendu (ENTIER, REEL, TEXTE ou BOOLEEN) à la ligne "
                        + tokenActuel().getLigne());
            }

            return new AST.ArrayDeclarationNode(nom, taille, type);
        }

        // Sinon, c'est une déclaration de variable simple
        consommer(TokenType.DEUX_POINTS, "':' attendu après le nom de variable");

        String type;
        if (verifier(TokenType.ENTIER)) {
            type = "ENTIER";
            avancer();
        } else if (verifier(TokenType.REEL)) {
            type = "REEL";
            avancer();
        } else if (verifier(TokenType.TEXTE)) {
            type = "TEXTE";
            avancer();
        } else if (verifier(TokenType.BOOLEEN)) {
            type = "BOOLEEN";
            avancer();
        } else {
            throw new RuntimeException("Type attendu (ENTIER, REEL, TEXTE ou BOOLEEN) à la ligne "
                    + tokenActuel().getLigne());
        }

        return new AST.DeclarationNode(nom, type);
    }

    /**
     * Parse une définition de fonction.
     * fonction -> FONCTION identifiant(parametres) [RETOURNE type] ... FINFONCTION
     * ou procedure -> PROCEDURE identifiant(parametres) ... FINPROCEDURE
     */
    private AST.FunctionNode parseFunction() {
        boolean estProcedure = verifier(TokenType.PROCEDURE);
        avancer();

        String nomFonction = consommer(TokenType.IDENTIFIANT, "Nom de fonction attendu").getValeur();
        consommer(TokenType.PARENTHESE_G, "'(' attendu après le nom de fonction");

        List<AST.ParameterNode> parametres = new ArrayList<>();
        if (!verifier(TokenType.PARENTHESE_D)) {
            parametres = parseParametres();
        }

        consommer(TokenType.PARENTHESE_D, "')' attendu après les paramètres");

        ignorerNouvellesLignes();

        String typeRetour = null;
        if (!estProcedure && verifier(TokenType.RETOURNE)) {
            avancer();
            if (verifier(TokenType.ENTIER)) {
                typeRetour = "ENTIER";
                avancer();
            } else if (verifier(TokenType.REEL)) {
                typeRetour = "REEL";
                avancer();
            } else if (verifier(TokenType.TEXTE)) {
                typeRetour = "TEXTE";
                avancer();
            } else if (verifier(TokenType.BOOLEEN)) {
                typeRetour = "BOOLEEN";
                avancer();
            } else {
                throw new RuntimeException("Type de retour attendu à la ligne " + tokenActuel().getLigne());
            }
        }

        ignorerNouvellesLignes();

        AST.BlockNode corps = parseFunctionBody();

        if (estProcedure) {
            consommer(TokenType.FINPROCEDURE, "Mot-clé 'FINPROCEDURE' attendu");
        } else {
            consommer(TokenType.FINFONCTION, "Mot-clé 'FINFONCTION' attendu");
        }

        return new AST.FunctionNode(nomFonction, parametres, typeRetour, corps);
    }

    /**
     * Parse les paramètres d'une fonction.
     * parametres -> param (, param)*
     * param -> identifiant : type
     */
    private List<AST.ParameterNode> parseParametres() {
        List<AST.ParameterNode> parametres = new ArrayList<>();

        String nomParam = consommer(TokenType.IDENTIFIANT, "Nom de paramètre attendu").getValeur();
        consommer(TokenType.DEUX_POINTS, "':' attendu après le nom du paramètre");

        String typeParam;
        if (verifier(TokenType.ENTIER)) {
            typeParam = "ENTIER";
            avancer();
        } else if (verifier(TokenType.REEL)) {
            typeParam = "REEL";
            avancer();
        } else if (verifier(TokenType.TEXTE)) {
            typeParam = "TEXTE";
            avancer();
        } else {
            throw new RuntimeException("Type attendu pour le paramètre");
        }

        parametres.add(new AST.ParameterNode(nomParam, typeParam));

        while (verifier(TokenType.VIRGULE)) {
            avancer();
            nomParam = consommer(TokenType.IDENTIFIANT, "Nom de paramètre attendu").getValeur();
            consommer(TokenType.DEUX_POINTS, "':' attendu après le nom du paramètre");

            if (verifier(TokenType.ENTIER)) {
                typeParam = "ENTIER";
                avancer();
            } else if (verifier(TokenType.REEL)) {
                typeParam = "REEL";
                avancer();
            } else if (verifier(TokenType.TEXTE)) {
                typeParam = "TEXTE";
                avancer();
            } else {
                throw new RuntimeException("Type attendu pour le paramètre");
            }

            parametres.add(new AST.ParameterNode(nomParam, typeParam));
        }

        return parametres;
    }

    /**
     * Parse le corps d'une fonction (comme parseInstructions mais avec RETOURNE).
     */
    private AST.BlockNode parseFunctionBody() {
        AST.BlockNode bloc = new AST.BlockNode();
        ignorerNouvellesLignes();

        while (!verifier(TokenType.FINFONCTION) &&
               !verifier(TokenType.FINPROCEDURE) &&
               !verifier(TokenType.EOF)) {

            AST.Node instruction = parseInstruction();
            if (instruction != null) {
                bloc.ajouter(instruction);
            }
            ignorerNouvellesLignes();
        }

        return bloc;
    }

    /**
     * Parse un bloc d'instructions.
     * Retourne un BlockNode contenant toutes les instructions jusqu'à un mot-clé de fin.
     */
    private AST.BlockNode parseInstructions() {
        AST.BlockNode bloc = new AST.BlockNode();
        ignorerNouvellesLignes();

        while (!verifier(TokenType.FIN) &&
               !verifier(TokenType.SINON) &&
               !verifier(TokenType.FINSI) &&
               !verifier(TokenType.FINTANTQUE) &&
               !verifier(TokenType.FINPOUR) &&
               !verifier(TokenType.FINCAS) &&
               !verifier(TokenType.DEFAUT) &&
               !verifier(TokenType.NOMBRE) &&
               !verifier(TokenType.EOF)) {

            AST.Node instruction = parseInstruction();
            if (instruction != null) {
                bloc.ajouter(instruction);
            }
            ignorerNouvellesLignes();
        }

        return bloc;
    }

    /**
     * Parse une instruction unique.
     * instruction -> affectation | si | tantque | pour | cas | ecrire | lire | retourne
     */
    private AST.Node parseInstruction() {
        ignorerNouvellesLignes();

        if (verifier(TokenType.RETOURNE)) {
            return parseRetour();
        }

        if (verifier(TokenType.SI)) {
            return parseSi();
        }

        if (verifier(TokenType.CAS)) {
            return parseSwitch();
        }

        if (verifier(TokenType.TANTQUE)) {
            return parseTantQue();
        }

        if (verifier(TokenType.POUR)) {
            return parsePour();
        }

        if (verifier(TokenType.ECRIRE)) {
            return parseEcrire();
        }

        if (verifier(TokenType.LIRE)) {
            return parseLire();
        }

        if (verifier(TokenType.IDENTIFIANT)) {
            return parseAffectationOuAppel();
        }

        Token t = tokenActuel();
        if (t.getType() != TokenType.EOF &&
            t.getType() != TokenType.FIN &&
            t.getType() != TokenType.FINSI &&
            t.getType() != TokenType.FINTANTQUE &&
            t.getType() != TokenType.FINPOUR &&
            t.getType() != TokenType.FINFONCTION &&
            t.getType() != TokenType.FINPROCEDURE &&
            t.getType() != TokenType.FINCAS &&
            t.getType() != TokenType.DEFAUT &&
            t.getType() != TokenType.NOMBRE) {
            throw new RuntimeException("Instruction inattendue: " + t.getValeur()
                    + " à la ligne " + t.getLigne());
        }

        return null;
    }

    /**
     * Parse une instruction RETOURNE.
     * retourne -> RETOURNE expression
     */
    private AST.ReturnNode parseRetour() {
        consommer(TokenType.RETOURNE, "Mot-clé 'RETOURNE' attendu");
        AST.Node valeur = parseExpression();
        return new AST.ReturnNode(valeur);
    }

    /**
     * Parse une affectation, un accès tableau, ou un appel de fonction comme instruction.
     * affectation -> identifiant <- expression
     * array_affectation -> identifiant[indice] <- expression
     * appel_fonction -> identifiant(arguments)
     */
    private AST.Node parseAffectationOuAppel() {
        String identifiant = consommer(TokenType.IDENTIFIANT, "Identifiant attendu").getValeur();

        // Vérifier si c'est un appel de fonction
        if (verifier(TokenType.PARENTHESE_G)) {
            avancer();
            List<AST.Node> arguments = new ArrayList<>();
            if (!verifier(TokenType.PARENTHESE_D)) {
                arguments.add(parseExpression());
                while (verifier(TokenType.VIRGULE)) {
                    avancer();
                    arguments.add(parseExpression());
                }
            }
            consommer(TokenType.PARENTHESE_D, "')' attendu après les arguments de la fonction");
            // Retourner une affectation fictive car les appels de fonction purs n'ont pas de nœud dédié
            // On va encapsuler le FunctionCallNode dans une affectation à une variable temporaire
            return new AST.AffectationNode("_call", new AST.FunctionCallNode(identifiant, arguments));
        }

        // Vérifier si c'est une affectation à un élément de tableau
        if (verifier(TokenType.CROCHET_G)) {
            avancer();
            AST.Node indice = parseExpression();
            consommer(TokenType.CROCHET_D, "']' attendu après l'indice du tableau");
            consommer(TokenType.AFFECTATION, "Opérateur '<-' attendu");
            AST.Node valeur = parseExpression();

            // Créer une affectation spéciale à un élément de tableau
            // On utilise ArrayAccessNode comme variable fictive
            return new AST.AffectationNode(identifiant, new AST.ExpressionBinaire(
                new AST.ArrayAccessNode(identifiant, indice),
                "array_set",
                valeur
            ));
        }

        // Sinon, c'est une affectation simple
        consommer(TokenType.AFFECTATION, "Opérateur '<-' attendu");
        AST.Node valeur = parseExpression();

        return new AST.AffectationNode(identifiant, valeur);
    }

    /**
     * Parse une structure conditionnelle SI.
     * si -> SI condition ALORS instructions (SINON instructions)? FINSI
     */
    private AST.SiNode parseSi() {
        consommer(TokenType.SI, "Mot-clé 'SI' attendu");
        AST.Node condition = parseCondition();
        consommer(TokenType.ALORS, "Mot-clé 'ALORS' attendu après la condition");

        AST.BlockNode blocAlors = parseInstructions();

        AST.BlockNode blocSinon = null;
        if (verifier(TokenType.SINON)) {
            avancer();
            blocSinon = parseInstructions();
        }

        consommer(TokenType.FINSI, "Mot-clé 'FINSI' attendu");
        return new AST.SiNode(condition, blocAlors, blocSinon);
    }

    /**
     * Parse une boucle TANTQUE.
     * tantque -> TANTQUE condition FAIRE instructions FINTANTQUE
     */
    private AST.TantQueNode parseTantQue() {
        consommer(TokenType.TANTQUE, "Mot-clé 'TANTQUE' attendu");
        AST.Node condition = parseCondition();
        consommer(TokenType.FAIRE, "Mot-clé 'FAIRE' attendu après la condition");

        AST.BlockNode corps = parseInstructions();
        consommer(TokenType.FINTANTQUE, "Mot-clé 'FINTANTQUE' attendu");

        return new AST.TantQueNode(condition, corps);
    }

    /**
     * Parse une boucle POUR.
     * pour -> POUR identifiant DE expression A expression FAIRE instructions FINPOUR
     */
    private AST.PourNode parsePour() {
        consommer(TokenType.POUR, "Mot-clé 'POUR' attendu");
        String variable = consommer(TokenType.IDENTIFIANT, "Nom de variable attendu").getValeur();
        consommer(TokenType.DE, "Mot-clé 'DE' attendu après le nom de variable");

        AST.Node debut = parseExpression();
        consommer(TokenType.A, "Mot-clé 'A' attendu après l'expression de début");
        AST.Node fin = parseExpression();
        consommer(TokenType.FAIRE, "Mot-clé 'FAIRE' attendu après l'expression de fin");

        AST.BlockNode corps = parseInstructions();
        consommer(TokenType.FINPOUR, "Mot-clé 'FINPOUR' attendu");

        return new AST.PourNode(variable, debut, fin, corps);
    }

    /**
     * Parse une instruction CAS (switch).
     * cas -> CAS expression FAIRE (cas)* [DEFAUT : instructions] FINCAS
     */
    private AST.SwitchNode parseSwitch() {
        consommer(TokenType.CAS, "Mot-clé 'CAS' attendu");
        AST.Node expression = parseExpression();
        consommer(TokenType.FAIRE, "Mot-clé 'FAIRE' attendu après l'expression du switch");

        List<AST.CaseNode> cases = new ArrayList<>();
        AST.BlockNode caseDefaut = null;

        ignorerNouvellesLignes();

        while (!verifier(TokenType.FINCAS) && !verifier(TokenType.EOF)) {
            if (verifier(TokenType.DEFAUT)) {
                avancer();
                consommer(TokenType.DEUX_POINTS, "':' attendu après DEFAUT");
                ignorerNouvellesLignes();
                caseDefaut = parseInstructions();
                ignorerNouvellesLignes();
                break;
            } else if (verifier(TokenType.NOMBRE)) {
                Token valeurToken = avancer();
                int valeur = Integer.parseInt(valeurToken.getValeur());
                consommer(TokenType.DEUX_POINTS, "':' attendu après la valeur du cas");
                ignorerNouvellesLignes();

                AST.BlockNode caseBody = new AST.BlockNode();
                while (!verifier(TokenType.NOMBRE) && !verifier(TokenType.DEFAUT) &&
                       !verifier(TokenType.FINCAS) && !verifier(TokenType.EOF)) {
                    AST.Node instruction = parseInstruction();
                    if (instruction != null) {
                        caseBody.ajouter(instruction);
                    }
                    ignorerNouvellesLignes();
                }

                cases.add(new AST.CaseNode(valeur, caseBody));
                ignorerNouvellesLignes();
            } else {
                ignorerNouvellesLignes();
            }
        }

        consommer(TokenType.FINCAS, "Mot-clé 'FINCAS' attendu à la fin du switch");
        return new AST.SwitchNode(expression, cases, caseDefaut);
    }

    /**
     * Parse une instruction ECRIRE.
     * ecrire -> ECRIRE ( expression (, expression)* )
     */
    private AST.EcrireNode parseEcrire() {
        consommer(TokenType.ECRIRE, "Mot-clé 'ECRIRE' attendu");
        consommer(TokenType.PARENTHESE_G, "'(' attendu après ECRIRE");

        List<AST.Node> expressions = new ArrayList<>();
        expressions.add(parseExpression());

        while (verifier(TokenType.VIRGULE)) {
            avancer();
            expressions.add(parseExpression());
        }

        consommer(TokenType.PARENTHESE_D, "')' attendu après les expressions");
        return new AST.EcrireNode(expressions);
    }

    /**
     * Parse une instruction LIRE.
     * lire -> LIRE ( identifiant )
     */
    private AST.LireNode parseLire() {
        consommer(TokenType.LIRE, "Mot-clé 'LIRE' attendu");
        consommer(TokenType.PARENTHESE_G, "'(' attendu après LIRE");
        String variable = consommer(TokenType.IDENTIFIANT, "Nom de variable attendu").getValeur();
        consommer(TokenType.PARENTHESE_D, "')' attendu après la variable");
        return new AST.LireNode(variable);
    }

    // ==================== EXPRESSIONS ====================

    /**
     * Parse une condition (expression avec opérateurs logiques et de comparaison).
     * condition -> disjunction
     * disjunction -> conjunction (OU conjunction)*
     * conjunction -> comparaison (ET comparaison)*
     * comparaison -> expression (op_comparaison expression)?
     */
    private AST.Node parseCondition() {
        return parseDisjunction();
    }

    /**
     * Disjonction (OU) - lowest precedence of logical operators
     * disjunction -> conjunction (OU conjunction)*
     */
    private AST.Node parseDisjunction() {
        AST.Node gauche = parseConjunction();

        while (verifier(TokenType.OU)) {
            String operateur = tokenActuel().getValeur();
            avancer();
            AST.Node droite = parseConjunction();
            gauche = new AST.ExpressionBinaire(gauche, operateur, droite);
        }

        return gauche;
    }

    /**
     * Conjonction (ET) - higher precedence than OU
     * conjunction -> comparaison (ET comparaison)*
     */
    private AST.Node parseConjunction() {
        AST.Node gauche = parseComparaison();

        while (verifier(TokenType.ET)) {
            String operateur = tokenActuel().getValeur();
            avancer();
            AST.Node droite = parseComparaison();
            gauche = new AST.ExpressionBinaire(gauche, operateur, droite);
        }

        return gauche;
    }

    /**
     * Comparaison (>, <, ==, !=, >=, <=)
     * comparaison -> expression (op_comparaison expression)?
     */
    private AST.Node parseComparaison() {
        AST.Node gauche = parseExpression();

        if (verifier(TokenType.SUPERIEUR) || verifier(TokenType.INFERIEUR) ||
            verifier(TokenType.EGAL) || verifier(TokenType.DIFFERENT) ||
            verifier(TokenType.SUP_EGAL) || verifier(TokenType.INF_EGAL)) {

            String operateur = tokenActuel().getValeur();
            avancer();
            AST.Node droite = parseExpression();
            return new AST.ExpressionBinaire(gauche, operateur, droite);
        }

        return gauche;
    }

    /**
     * Parse une expression (addition et soustraction).
     * expression -> terme ((+ | -) terme)*
     */
    private AST.Node parseExpression() {
        AST.Node gauche = parseTerme();

        while (verifier(TokenType.PLUS) || verifier(TokenType.MOINS)) {
            String operateur = tokenActuel().getValeur();
            avancer();
            AST.Node droite = parseTerme();
            gauche = new AST.ExpressionBinaire(gauche, operateur, droite);
        }

        return gauche;
    }

    /**
     * Parse un terme (multiplication, division et modulo).
     * terme -> facteur ((* | / | %) facteur)*
     */
    private AST.Node parseTerme() {
        AST.Node gauche = parseFacteur();

        while (verifier(TokenType.MULTIPLIE) || verifier(TokenType.DIVISE) || verifier(TokenType.MODULO)) {
            String operateur = tokenActuel().getValeur();
            avancer();
            AST.Node droite = parseFacteur();
            gauche = new AST.ExpressionBinaire(gauche, operateur, droite);
        }

        return gauche;
    }

    /**
     * Parse un facteur (élément de base d'une expression).
     * facteur -> NON facteur | nombre | nombre_reel | chaine | identifiant | ( condition )
     */
    private AST.Node parseFacteur() {
        if (verifier(TokenType.NON)) {
            String operateur = tokenActuel().getValeur();
            avancer();
            AST.Node operande = parseFacteur();
            return new AST.ExpressionUnaire(operateur, operande);
        }

        if (verifier(TokenType.NOMBRE)) {
            int valeur = Integer.parseInt(tokenActuel().getValeur());
            avancer();
            return new AST.NombreNode(valeur);
        }

        if (verifier(TokenType.NOMBRE_REEL)) {
            double valeur = Double.parseDouble(tokenActuel().getValeur());
            avancer();
            return new AST.NombreReelNode(valeur);
        }

        if (verifier(TokenType.CHAINE)) {
            String valeur = tokenActuel().getValeur();
            avancer();
            return new AST.ChaineNode(valeur);
        }

        if (verifier(TokenType.VRAI)) {
            avancer();
            return new AST.BooleanNode(true);
        }

        if (verifier(TokenType.FAUX)) {
            avancer();
            return new AST.BooleanNode(false);
        }

        if (verifier(TokenType.IDENTIFIANT)) {
            String nom = tokenActuel().getValeur();
            avancer();

            // Vérifier si c'est un appel de fonction
            if (verifier(TokenType.PARENTHESE_G)) {
                avancer();
                List<AST.Node> arguments = new ArrayList<>();
                if (!verifier(TokenType.PARENTHESE_D)) {
                    arguments.add(parseExpression());
                    while (verifier(TokenType.VIRGULE)) {
                        avancer();
                        arguments.add(parseExpression());
                    }
                }
                consommer(TokenType.PARENTHESE_D, "')' attendu après les arguments de la fonction");
                return new AST.FunctionCallNode(nom, arguments);
            }

            // Vérifier si c'est un accès à un tableau
            if (verifier(TokenType.CROCHET_G)) {
                avancer();
                AST.Node indice = parseExpression();
                consommer(TokenType.CROCHET_D, "']' attendu après l'indice du tableau");
                return new AST.ArrayAccessNode(nom, indice);
            }

            return new AST.IdentifiantNode(nom);
        }

        if (verifier(TokenType.PARENTHESE_G)) {
            avancer();
            AST.Node expression = parseCondition();
            consommer(TokenType.PARENTHESE_D, "')' attendu");
            return expression;
        }

        Token t = tokenActuel();
        throw new RuntimeException("Expression attendue à la ligne " + t.getLigne()
                + ", colonne " + t.getColonne() + ". Token trouvé: " + t);
    }
}
