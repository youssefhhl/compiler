package org.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Analyseur Sémantique pour le compilateur Pseudo-code vers Python.
 *
 * Cet analyseur parcourt l'AST et effectue les vérifications suivantes :
 * 1. EXISTENCE : Vérifier qu'une variable est déclarée avant utilisation
 * 2. TYPES : Empêcher l'affectation d'un TEXTE dans un ENTIER (et inversement)
 * 3. OPÉRATIONS : Vérifier que les opérateurs mathématiques (+, -, *, /)
 *                 ne sont utilisés qu'avec des entiers
 * 4. DOUBLONS : Vérifier qu'une variable n'est pas déclarée deux fois
 *
 * L'analyse s'effectue AVANT la génération Python. Si des erreurs sont détectées,
 * le fichier Python n'est pas généré.
 */
public class SemanticAnalyzer {

    private final SymbolTable tableSymboles;    // Table des symboles
    private final List<String> erreurs;          // Liste des erreurs détectées
    private int ligneActuelle;                   // Ligne actuelle (pour les messages)

    /**
     * Constructeur - initialise l'analyseur avec une table de symboles vide.
     */
    public SemanticAnalyzer() {
        this.tableSymboles = new SymbolTable();
        this.erreurs = new ArrayList<>();
        this.ligneActuelle = 0;
    }

    /**
     * Lance l'analyse sémantique sur un programme.
     *
     * @param programme Le nœud racine de l'AST (ProgrammeNode)
     * @throws SemanticException si des erreurs sémantiques sont détectées
     */
    public void analyser(AST.ProgrammeNode programme) throws SemanticException {
        erreurs.clear();

        // Étape 1 : Enregistrer toutes les déclarations dans la table des symboles
        // (Cela détecte aussi les doublons)
        for (AST.DeclarationNode declaration : programme.getDeclarations()) {
            analyserDeclaration(declaration);
        }

        // Étape 2 : Analyser le corps du programme
        analyserBloc(programme.getCorps());

        // Étape 3 : Si des erreurs ont été collectées, les signaler
        if (!erreurs.isEmpty()) {
            StringBuilder message = new StringBuilder();
            message.append("Analyse sémantique échouée avec ").append(erreurs.size()).append(" erreur(s):\n");
            for (int i = 0; i < erreurs.size(); i++) {
                message.append("  ").append(i + 1).append(". ").append(erreurs.get(i)).append("\n");
            }
            throw new SemanticException(message.toString());
        }
    }

    /**
     * Analyse une déclaration de variable.
     * Enregistre la variable dans la table des symboles.
     */
    private void analyserDeclaration(AST.DeclarationNode declaration) {
        try {
            tableSymboles.declarer(
                declaration.getNom(),
                declaration.getType(),
                1 // Ligne approximative (section VARIABLES)
            );
        } catch (SemanticException e) {
            erreurs.add(e.getMessage());
        }
    }

    /**
     * Analyse un bloc d'instructions.
     */
    private void analyserBloc(AST.BlockNode bloc) {
        for (AST.Node instruction : bloc.getInstructions()) {
            analyserInstruction(instruction);
        }
    }

    /**
     * Analyse une instruction individuelle.
     * Dispatch vers la méthode appropriée selon le type de nœud.
     */
    private void analyserInstruction(AST.Node instruction) {
        if (instruction instanceof AST.AffectationNode affectation) {
            analyserAffectation(affectation);
        } else if (instruction instanceof AST.SiNode si) {
            analyserSi(si);
        } else if (instruction instanceof AST.TantQueNode tantQue) {
            analyserTantQue(tantQue);
        } else if (instruction instanceof AST.EcrireNode ecrire) {
            analyserEcrire(ecrire);
        } else if (instruction instanceof AST.LireNode lire) {
            analyserLire(lire);
        }
        // Les autres types de nœuds (expressions) sont analysés dans leur contexte
    }

    /**
     * Analyse une affectation (x <- expression).
     * Vérifie :
     * - La variable cible existe
     * - Le type de l'expression est compatible avec le type de la variable
     */
    private void analyserAffectation(AST.AffectationNode affectation) {
        String nomVariable = affectation.getVariable();

        // Vérifier que la variable cible est déclarée
        if (!tableSymboles.existe(nomVariable)) {
            erreurs.add("Variable '" + nomVariable + "' non déclarée. " +
                       "Déclarez-la dans la section VARIABLES avant de l'utiliser.");
            return;
        }

        // Récupérer le type de la variable cible
        SymbolTable.TypeDonnee typeCible;
        try {
            typeCible = tableSymboles.getType(nomVariable, ligneActuelle);
        } catch (SemanticException e) {
            erreurs.add(e.getMessage());
            return;
        }

        // Déterminer le type de l'expression
        SymbolTable.TypeDonnee typeExpression = determinerTypeExpression(affectation.getValeur());

        // Vérifier la compatibilité des types (si on a pu déterminer le type)
        if (typeExpression != null && typeCible != typeExpression) {
            erreurs.add("Incompatibilité de types: impossible d'affecter un " +
                       typeExpression + " à la variable '" + nomVariable + "' de type " + typeCible + ".");
        }
    }

    /**
     * Analyse une structure conditionnelle SI/ALORS/SINON.
     */
    private void analyserSi(AST.SiNode si) {
        // Analyser la condition
        analyserCondition(si.getCondition());

        // Analyser le bloc ALORS
        analyserBloc(si.getBlocAlors());

        // Analyser le bloc SINON s'il existe
        if (si.getBlocSinon() != null) {
            analyserBloc(si.getBlocSinon());
        }
    }

    /**
     * Analyse une boucle TANTQUE.
     */
    private void analyserTantQue(AST.TantQueNode tantQue) {
        // Analyser la condition
        analyserCondition(tantQue.getCondition());

        // Analyser le corps de la boucle
        analyserBloc(tantQue.getCorps());
    }

    /**
     * Analyse une condition (expression de comparaison).
     */
    private void analyserCondition(AST.Node condition) {
        // Vérifier que toutes les variables utilisées existent
        verifierVariablesExistent(condition);

        // Si c'est une expression binaire de comparaison, vérifier les types
        if (condition instanceof AST.ExpressionBinaire expr) {
            String op = expr.getOperateur();

            // Pour les comparaisons numériques (>, <, >=, <=), les deux côtés doivent être des entiers
            if (op.equals(">") || op.equals("<") || op.equals(">=") || op.equals("<=")) {
                SymbolTable.TypeDonnee typeGauche = determinerTypeExpression(expr.getGauche());
                SymbolTable.TypeDonnee typeDroite = determinerTypeExpression(expr.getDroite());

                // Vérifier que les deux côtés sont des entiers pour les comparaisons numériques
                if (typeGauche != null && typeGauche != SymbolTable.TypeDonnee.ENTIER) {
                    erreurs.add("La comparaison '" + op + "' nécessite des ENTIERS. " +
                               "L'opérande gauche est de type " + typeGauche + ".");
                }
                if (typeDroite != null && typeDroite != SymbolTable.TypeDonnee.ENTIER) {
                    erreurs.add("La comparaison '" + op + "' nécessite des ENTIERS. " +
                               "L'opérande droite est de type " + typeDroite + ".");
                }
            }
        }
    }

    /**
     * Analyse une instruction ECRIRE.
     * Vérifie que toutes les variables utilisées existent.
     */
    private void analyserEcrire(AST.EcrireNode ecrire) {
        for (AST.Node expression : ecrire.getExpressions()) {
            verifierVariablesExistent(expression);
        }
    }

    /**
     * Analyse une instruction LIRE.
     * Vérifie que la variable cible est déclarée.
     */
    private void analyserLire(AST.LireNode lire) {
        String nomVariable = lire.getVariable();

        if (!tableSymboles.existe(nomVariable)) {
            erreurs.add("Variable '" + nomVariable + "' non déclarée. " +
                       "Déclarez-la dans la section VARIABLES avant d'utiliser LIRE.");
        }
    }

    /**
     * Détermine le type d'une expression.
     *
     * @param expression L'expression à analyser
     * @return Le type de l'expression, ou null si indéterminé
     */
    private SymbolTable.TypeDonnee determinerTypeExpression(AST.Node expression) {
        if (expression instanceof AST.NombreNode) {
            // Un nombre littéral est un ENTIER
            return SymbolTable.TypeDonnee.ENTIER;

        } else if (expression instanceof AST.ChaineNode) {
            // Une chaîne littérale est un TEXTE
            return SymbolTable.TypeDonnee.TEXTE;

        } else if (expression instanceof AST.IdentifiantNode identifiant) {
            // Pour un identifiant, on récupère son type dans la table des symboles
            String nom = identifiant.getNom();
            if (tableSymboles.existe(nom)) {
                try {
                    return tableSymboles.getType(nom, ligneActuelle);
                } catch (SemanticException e) {
                    return null;
                }
            } else {
                // La variable n'existe pas - l'erreur sera signalée ailleurs
                return null;
            }

        } else if (expression instanceof AST.ExpressionBinaire expr) {
            // Pour une expression binaire, analyser selon l'opérateur
            return analyserExpressionBinaire(expr);
        }

        return null;
    }

    /**
     * Analyse une expression binaire et retourne son type résultant.
     * Vérifie aussi que les opérateurs sont utilisés correctement.
     */
    private SymbolTable.TypeDonnee analyserExpressionBinaire(AST.ExpressionBinaire expr) {
        String operateur = expr.getOperateur();
        SymbolTable.TypeDonnee typeGauche = determinerTypeExpression(expr.getGauche());
        SymbolTable.TypeDonnee typeDroite = determinerTypeExpression(expr.getDroite());

        // Vérifier que les variables utilisées existent
        verifierVariablesExistent(expr.getGauche());
        verifierVariablesExistent(expr.getDroite());

        // Opérateurs arithmétiques : +, -, *, /
        if (operateur.equals("+") || operateur.equals("-") ||
            operateur.equals("*") || operateur.equals("/")) {

            // Les opérateurs arithmétiques ne fonctionnent qu'avec des ENTIERS
            if (typeGauche != null && typeGauche != SymbolTable.TypeDonnee.ENTIER) {
                erreurs.add("L'opérateur '" + operateur + "' ne peut être utilisé qu'avec des ENTIERS. " +
                           "L'opérande gauche est de type " + typeGauche + ".");
            }
            if (typeDroite != null && typeDroite != SymbolTable.TypeDonnee.ENTIER) {
                erreurs.add("L'opérateur '" + operateur + "' ne peut être utilisé qu'avec des ENTIERS. " +
                           "L'opérande droite est de type " + typeDroite + ".");
            }

            // Le résultat d'une opération arithmétique est un ENTIER
            return SymbolTable.TypeDonnee.ENTIER;
        }

        // Opérateurs de comparaison : >, <, >=, <=
        if (operateur.equals(">") || operateur.equals("<") ||
            operateur.equals(">=") || operateur.equals("<=")) {

            // Les comparaisons numériques nécessitent des ENTIERS des deux côtés
            if (typeGauche != null && typeGauche != SymbolTable.TypeDonnee.ENTIER) {
                erreurs.add("L'opérateur '" + operateur + "' nécessite des ENTIERS. " +
                           "L'opérande gauche est de type " + typeGauche + ".");
            }
            if (typeDroite != null && typeDroite != SymbolTable.TypeDonnee.ENTIER) {
                erreurs.add("L'opérateur '" + operateur + "' nécessite des ENTIERS. " +
                           "L'opérande droite est de type " + typeDroite + ".");
            }

            // Le résultat d'une comparaison est... on reste en ENTIER pour simplifier
            // (Pas de type BOOLEEN dans notre langage)
            return SymbolTable.TypeDonnee.ENTIER;
        }

        // Opérateurs d'égalité : ==, !=
        if (operateur.equals("==") || operateur.equals("!=")) {
            // Les deux opérandes doivent être du même type
            if (typeGauche != null && typeDroite != null && typeGauche != typeDroite) {
                erreurs.add("L'opérateur '" + operateur + "' compare des types différents: " +
                           typeGauche + " et " + typeDroite + ".");
            }

            return SymbolTable.TypeDonnee.ENTIER; // Résultat booléen traité comme entier
        }

        return typeGauche; // Par défaut, retourner le type de gauche
    }

    /**
     * Vérifie récursivement que toutes les variables utilisées dans une expression existent.
     */
    private void verifierVariablesExistent(AST.Node expression) {
        if (expression instanceof AST.IdentifiantNode identifiant) {
            String nom = identifiant.getNom();
            if (!tableSymboles.existe(nom)) {
                erreurs.add("Variable '" + nom + "' non déclarée. " +
                           "Déclarez-la dans la section VARIABLES.");
            }

        } else if (expression instanceof AST.ExpressionBinaire expr) {
            // Vérifier récursivement les deux côtés
            verifierVariablesExistent(expr.getGauche());
            verifierVariablesExistent(expr.getDroite());
        }
        // Pour NombreNode et ChaineNode, rien à vérifier
    }

    /**
     * Retourne la table des symboles (utile pour le générateur Python).
     */
    public SymbolTable getTableSymboles() {
        return tableSymboles;
    }

    /**
     * Retourne la liste des erreurs détectées.
     */
    public List<String> getErreurs() {
        return new ArrayList<>(erreurs);
    }

    /**
     * Retourne true si l'analyse a réussi (pas d'erreurs).
     */
    public boolean estValide() {
        return erreurs.isEmpty();
    }
}
