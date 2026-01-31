package org.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Analyseur syntaxique (Parser) utilisant la méthode de descente récursive.
 * Transforme la liste de tokens en un Arbre Syntaxique Abstrait (AST).
 *
 * Grammaire du pseudo-code (simplifiée) :
 *
 * programme     -> ALGORITHME identifiant VARIABLES declarations DEBUT instructions FIN
 * declarations  -> (declaration)*
 * declaration   -> identifiant : type
 * type          -> ENTIER | TEXTE
 * instructions  -> (instruction)*
 * instruction   -> affectation | si | tantque | ecrire | lire
 * affectation   -> identifiant <- expression
 * si            -> SI condition ALORS instructions (SINON instructions)? FINSI
 * tantque       -> TANTQUE condition FAIRE instructions FINTANTQUE
 * ecrire        -> ECRIRE ( expression (, expression)* )
 * lire          -> LIRE ( identifiant )
 * condition     -> expression (op_comparaison expression)?
 * expression    -> terme ((+ | -) terme)*
 * terme         -> facteur ((* | /) facteur)*
 * facteur       -> nombre | chaine | identifiant | ( expression )
 */
public class Parser {
    private final List<Token> tokens;  // Liste des tokens à analyser
    private int position;               // Position actuelle dans la liste

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

    // ==================== MÉTHODES UTILITAIRES ====================

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
        // Ignorer les nouvelles lignes pendant l'avancement
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
        // Ignorer les nouvelles lignes avant de consommer
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
     * programme -> ALGORITHME identifiant VARIABLES declarations DEBUT instructions FIN
     */
    private AST.ProgrammeNode parseProgramme() {
        ignorerNouvellesLignes();

        // ALGORITHME nom_algorithme
        consommer(TokenType.ALGORITHME, "Mot-clé 'ALGORITHME' attendu au début du programme");
        String nomAlgorithme = consommer(TokenType.IDENTIFIANT, "Nom de l'algorithme attendu").getValeur();

        ignorerNouvellesLignes();

        // VARIABLES (optionnel - peut être absent)
        List<AST.DeclarationNode> declarations = new ArrayList<>();
        if (verifier(TokenType.VARIABLES)) {
            avancer(); // Consommer VARIABLES
            declarations = parseDeclarations();
        }

        ignorerNouvellesLignes();

        // DEBUT
        consommer(TokenType.DEBUT, "Mot-clé 'DEBUT' attendu");

        // Instructions
        AST.BlockNode corps = parseInstructions();

        // FIN
        consommer(TokenType.FIN, "Mot-clé 'FIN' attendu à la fin du programme");

        return new AST.ProgrammeNode(nomAlgorithme, declarations, corps);
    }

    /**
     * Parse les déclarations de variables.
     * declarations -> (declaration)*
     */
    private List<AST.DeclarationNode> parseDeclarations() {
        List<AST.DeclarationNode> declarations = new ArrayList<>();

        ignorerNouvellesLignes();

        // Tant qu'on trouve des identifiants (déclarations), on les parse
        while (verifier(TokenType.IDENTIFIANT)) {
            declarations.add(parseDeclaration());
            ignorerNouvellesLignes();
        }

        return declarations;
    }

    /**
     * Parse une déclaration de variable.
     * declaration -> identifiant : type
     */
    private AST.DeclarationNode parseDeclaration() {
        String nom = consommer(TokenType.IDENTIFIANT, "Nom de variable attendu").getValeur();
        consommer(TokenType.DEUX_POINTS, "':' attendu après le nom de variable");

        String type;
        if (verifier(TokenType.ENTIER)) {
            type = "ENTIER";
            avancer();
        } else if (verifier(TokenType.TEXTE)) {
            type = "TEXTE";
            avancer();
        } else {
            throw new RuntimeException("Type attendu (ENTIER ou TEXTE) à la ligne "
                    + tokenActuel().getLigne());
        }

        return new AST.DeclarationNode(nom, type);
    }

    /**
     * Parse un bloc d'instructions.
     * Retourne un BlockNode contenant toutes les instructions jusqu'à un mot-clé de fin.
     */
    private AST.BlockNode parseInstructions() {
        AST.BlockNode bloc = new AST.BlockNode();

        ignorerNouvellesLignes();

        // Continuer tant qu'on n'est pas sur un mot-clé de fin de bloc
        while (!verifier(TokenType.FIN) &&
               !verifier(TokenType.SINON) &&
               !verifier(TokenType.FINSI) &&
               !verifier(TokenType.FINTANTQUE) &&
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
     * instruction -> affectation | si | tantque | ecrire | lire
     */
    private AST.Node parseInstruction() {
        ignorerNouvellesLignes();

        // SI -> Structure conditionnelle
        if (verifier(TokenType.SI)) {
            return parseSi();
        }

        // TANTQUE -> Boucle while
        if (verifier(TokenType.TANTQUE)) {
            return parseTantQue();
        }

        // ECRIRE -> Affichage
        if (verifier(TokenType.ECRIRE)) {
            return parseEcrire();
        }

        // LIRE -> Lecture d'entrée
        if (verifier(TokenType.LIRE)) {
            return parseLire();
        }

        // IDENTIFIANT <- ... -> Affectation
        if (verifier(TokenType.IDENTIFIANT)) {
            return parseAffectation();
        }

        // Token inattendu
        Token t = tokenActuel();
        if (t.getType() != TokenType.EOF &&
            t.getType() != TokenType.FIN &&
            t.getType() != TokenType.FINSI &&
            t.getType() != TokenType.FINTANTQUE) {
            throw new RuntimeException("Instruction inattendue: " + t.getValeur()
                    + " à la ligne " + t.getLigne());
        }

        return null;
    }

    /**
     * Parse une affectation.
     * affectation -> identifiant <- expression
     */
    private AST.AffectationNode parseAffectation() {
        String variable = consommer(TokenType.IDENTIFIANT, "Identifiant attendu").getValeur();
        consommer(TokenType.AFFECTATION, "Opérateur '<-' attendu");
        AST.Node valeur = parseExpression();

        return new AST.AffectationNode(variable, valeur);
    }

    /**
     * Parse une structure conditionnelle SI.
     * si -> SI condition ALORS instructions (SINON instructions)? FINSI
     */
    private AST.SiNode parseSi() {
        consommer(TokenType.SI, "Mot-clé 'SI' attendu");

        // Parser la condition
        AST.Node condition = parseCondition();

        consommer(TokenType.ALORS, "Mot-clé 'ALORS' attendu après la condition");

        // Parser le bloc ALORS
        AST.BlockNode blocAlors = parseInstructions();

        // Vérifier si il y a un bloc SINON (optionnel)
        AST.BlockNode blocSinon = null;
        if (verifier(TokenType.SINON)) {
            avancer(); // Consommer SINON
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

        // Parser la condition
        AST.Node condition = parseCondition();

        consommer(TokenType.FAIRE, "Mot-clé 'FAIRE' attendu après la condition");

        // Parser le corps de la boucle
        AST.BlockNode corps = parseInstructions();

        consommer(TokenType.FINTANTQUE, "Mot-clé 'FINTANTQUE' attendu");

        return new AST.TantQueNode(condition, corps);
    }

    /**
     * Parse une instruction ECRIRE.
     * ecrire -> ECRIRE ( expression (, expression)* )
     */
    private AST.EcrireNode parseEcrire() {
        consommer(TokenType.ECRIRE, "Mot-clé 'ECRIRE' attendu");
        consommer(TokenType.PARENTHESE_G, "'(' attendu après ECRIRE");

        List<AST.Node> expressions = new ArrayList<>();

        // Au moins une expression
        expressions.add(parseExpression());

        // Expressions supplémentaires séparées par des virgules
        while (verifier(TokenType.VIRGULE)) {
            avancer(); // Consommer la virgule
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
     * Parse une condition (expression avec opérateurs de comparaison).
     * condition -> expression (op_comparaison expression)?
     */
    private AST.Node parseCondition() {
        AST.Node gauche = parseExpression();

        // Vérifier s'il y a un opérateur de comparaison
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
     * Parse un terme (multiplication et division).
     * terme -> facteur ((* | /) facteur)*
     */
    private AST.Node parseTerme() {
        AST.Node gauche = parseFacteur();

        while (verifier(TokenType.MULTIPLIE) || verifier(TokenType.DIVISE)) {
            String operateur = tokenActuel().getValeur();
            avancer();
            AST.Node droite = parseFacteur();
            gauche = new AST.ExpressionBinaire(gauche, operateur, droite);
        }

        return gauche;
    }

    /**
     * Parse un facteur (élément de base d'une expression).
     * facteur -> nombre | chaine | identifiant | ( expression )
     */
    private AST.Node parseFacteur() {
        // Nombre littéral
        if (verifier(TokenType.NOMBRE)) {
            int valeur = Integer.parseInt(tokenActuel().getValeur());
            avancer();
            return new AST.NombreNode(valeur);
        }

        // Chaîne de caractères
        if (verifier(TokenType.CHAINE)) {
            String valeur = tokenActuel().getValeur();
            avancer();
            return new AST.ChaineNode(valeur);
        }

        // Identifiant (variable)
        if (verifier(TokenType.IDENTIFIANT)) {
            String nom = tokenActuel().getValeur();
            avancer();
            return new AST.IdentifiantNode(nom);
        }

        // Expression parenthésée
        if (verifier(TokenType.PARENTHESE_G)) {
            avancer(); // Consommer (
            AST.Node expression = parseExpression();
            consommer(TokenType.PARENTHESE_D, "')' attendu");
            return expression;
        }

        Token t = tokenActuel();
        throw new RuntimeException("Expression attendue à la ligne " + t.getLigne()
                + ", colonne " + t.getColonne() + ". Token trouvé: " + t);
    }
}
