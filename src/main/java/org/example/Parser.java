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
     * programme -> ALGORITHME identifiant VARIABLES declarations DEBUT instructions FIN
     */
    private AST.ProgrammeNode parseProgramme() {
        ignorerNouvellesLignes();

        consommer(TokenType.ALGORITHME, "Mot-clé 'ALGORITHME' attendu au début du programme");
        String nomAlgorithme = consommer(TokenType.IDENTIFIANT, "Nom de l'algorithme attendu").getValeur();

        ignorerNouvellesLignes();

        List<AST.DeclarationNode> declarations = new ArrayList<>();
        if (verifier(TokenType.VARIABLES)) {
            avancer();
            declarations = parseDeclarations();
        }

        ignorerNouvellesLignes();

        consommer(TokenType.DEBUT, "Mot-clé 'DEBUT' attendu");
        AST.BlockNode corps = parseInstructions();
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
        } else if (verifier(TokenType.REEL)) {
            type = "REEL";
            avancer();
        } else if (verifier(TokenType.TEXTE)) {
            type = "TEXTE";
            avancer();
        } else {
            throw new RuntimeException("Type attendu (ENTIER, REEL ou TEXTE) à la ligne "
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

        while (!verifier(TokenType.FIN) &&
               !verifier(TokenType.SINON) &&
               !verifier(TokenType.FINSI) &&
               !verifier(TokenType.FINTANTQUE) &&
               !verifier(TokenType.FINPOUR) &&
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
     * instruction -> affectation | si | tantque | pour | ecrire | lire
     */
    private AST.Node parseInstruction() {
        ignorerNouvellesLignes();

        if (verifier(TokenType.SI)) {
            return parseSi();
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
            return parseAffectation();
        }

        Token t = tokenActuel();
        if (t.getType() != TokenType.EOF &&
            t.getType() != TokenType.FIN &&
            t.getType() != TokenType.FINSI &&
            t.getType() != TokenType.FINTANTQUE &&
            t.getType() != TokenType.FINPOUR) {
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

        if (verifier(TokenType.IDENTIFIANT)) {
            String nom = tokenActuel().getValeur();
            avancer();
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
