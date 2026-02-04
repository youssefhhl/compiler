package org.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Définition de l'Arbre Syntaxique Abstrait (AST).
 * Contient toutes les classes de nœuds pour représenter le programme.
 */
public class AST {

    /**
     * Classe abstraite de base pour tous les nœuds de l'AST.
     */
    public static abstract class Node {
        // Méthode abstraite pour l'acceptation du visiteur (pattern Visitor)
        public abstract void accepter(NodeVisitor visiteur);
    }

    /**
     * Interface pour le pattern Visitor (utilisé par le générateur).
     */
    public interface NodeVisitor {
        void visiter(ProgrammeNode node);
        void visiter(BlockNode node);
        void visiter(DeclarationNode node);
        void visiter(AffectationNode node);
        void visiter(SiNode node);
        void visiter(TantQueNode node);
        void visiter(PourNode node);
        void visiter(EcrireNode node);
        void visiter(LireNode node);
        void visiter(ExpressionBinaire node);
        void visiter(ExpressionUnaire node);
        void visiter(NombreNode node);
        void visiter(NombreReelNode node);
        void visiter(ChaineNode node);
        void visiter(BooleanNode node);
        void visiter(IdentifiantNode node);
        void visiter(FunctionNode node);
        void visiter(FunctionCallNode node);
        void visiter(ReturnNode node);
        void visiter(ArrayDeclarationNode node);
        void visiter(ArrayAccessNode node);
        void visiter(SwitchNode node);
    }

    // ==================== NŒUDS DE STRUCTURE ====================

    /**
     * Nœud représentant le programme complet.
     */
    public static class ProgrammeNode extends Node {
        private final String nom;                // Nom de l'algorithme
        private final List<Node> declarations;   // Déclarations de variables et tableaux (DeclarationNode, ArrayDeclarationNode)
        private final List<FunctionNode> fonctions;  // Définitions de fonctions
        private final BlockNode corps;           // Corps du programme (DEBUT...FIN)

        public ProgrammeNode(String nom, List<Node> declarations, BlockNode corps) {
            this.nom = nom;
            this.declarations = declarations;
            this.fonctions = new ArrayList<>();
            this.corps = corps;
        }

        public ProgrammeNode(String nom, List<Node> declarations, List<FunctionNode> fonctions, BlockNode corps) {
            this.nom = nom;
            this.declarations = declarations;
            this.fonctions = fonctions;
            this.corps = corps;
        }

        public String getNom() { return nom; }
        public List<Node> getDeclarations() { return declarations; }
        public List<FunctionNode> getFonctions() { return fonctions; }
        public BlockNode getCorps() { return corps; }

        @Override
        public void accepter(NodeVisitor visiteur) {
            visiteur.visiter(this);
        }
    }

    /**
     * Nœud représentant un bloc d'instructions (liste d'instructions).
     */
    public static class BlockNode extends Node {
        private final List<Node> instructions;

        public BlockNode() {
            this.instructions = new ArrayList<>();
        }

        public BlockNode(List<Node> instructions) {
            this.instructions = instructions;
        }

        public void ajouter(Node instruction) {
            instructions.add(instruction);
        }

        public List<Node> getInstructions() { return instructions; }

        @Override
        public void accepter(NodeVisitor visiteur) {
            visiteur.visiter(this);
        }
    }

    /**
     * Nœud représentant une déclaration de variable.
     * Ex: x : ENTIER
     */
    public static class DeclarationNode extends Node {
        private final String nom;   // Nom de la variable
        private final String type;  // Type (ENTIER, TEXTE)

        public DeclarationNode(String nom, String type) {
            this.nom = nom;
            this.type = type;
        }

        public String getNom() { return nom; }
        public String getType() { return type; }

        @Override
        public void accepter(NodeVisitor visiteur) {
            visiteur.visiter(this);
        }
    }

    // ==================== NŒUDS D'INSTRUCTIONS ====================

    /**
     * Nœud représentant une affectation.
     * Ex: x <- 5
     */
    public static class AffectationNode extends Node {
        private final String variable;  // Nom de la variable
        private final Node valeur;      // Expression à affecter

        public AffectationNode(String variable, Node valeur) {
            this.variable = variable;
            this.valeur = valeur;
        }

        public String getVariable() { return variable; }
        public Node getValeur() { return valeur; }

        @Override
        public void accepter(NodeVisitor visiteur) {
            visiteur.visiter(this);
        }
    }

    /**
     * Nœud représentant une structure conditionnelle SI/ALORS/SINON.
     */
    public static class SiNode extends Node {
        private final Node condition;       // Condition du SI
        private final BlockNode blocAlors;  // Bloc ALORS
        private final BlockNode blocSinon;  // Bloc SINON (peut être null)

        public SiNode(Node condition, BlockNode blocAlors, BlockNode blocSinon) {
            this.condition = condition;
            this.blocAlors = blocAlors;
            this.blocSinon = blocSinon;
        }

        public Node getCondition() { return condition; }
        public BlockNode getBlocAlors() { return blocAlors; }
        public BlockNode getBlocSinon() { return blocSinon; }

        @Override
        public void accepter(NodeVisitor visiteur) {
            visiteur.visiter(this);
        }
    }

    /**
     * Nœud représentant une boucle TANTQUE.
     */
    public static class TantQueNode extends Node {
        private final Node condition;    // Condition de la boucle
        private final BlockNode corps;   // Corps de la boucle

        public TantQueNode(Node condition, BlockNode corps) {
            this.condition = condition;
            this.corps = corps;
        }

        public Node getCondition() { return condition; }
        public BlockNode getCorps() { return corps; }

        @Override
        public void accepter(NodeVisitor visiteur) {
            visiteur.visiter(this);
        }
    }

    /**
     * Nœud représentant une boucle POUR.
     * Ex: POUR i DE 1 A 10 FAIRE ... FINPOUR
     */
    public static class PourNode extends Node {
        private final String variable;   // Variable de boucle (i)
        private final Node debut;        // Expression de début (1)
        private final Node fin;          // Expression de fin (10)
        private final BlockNode corps;   // Corps de la boucle

        public PourNode(String variable, Node debut, Node fin, BlockNode corps) {
            this.variable = variable;
            this.debut = debut;
            this.fin = fin;
            this.corps = corps;
        }

        public String getVariable() { return variable; }
        public Node getDebut() { return debut; }
        public Node getFin() { return fin; }
        public BlockNode getCorps() { return corps; }

        @Override
        public void accepter(NodeVisitor visiteur) {
            visiteur.visiter(this);
        }
    }

    /**
     * Nœud représentant une instruction ECRIRE (affichage).
     */
    public static class EcrireNode extends Node {
        private final List<Node> expressions;  // Liste d'expressions à afficher

        public EcrireNode(List<Node> expressions) {
            this.expressions = expressions;
        }

        public List<Node> getExpressions() { return expressions; }

        @Override
        public void accepter(NodeVisitor visiteur) {
            visiteur.visiter(this);
        }
    }

    /**
     * Nœud représentant une instruction LIRE (entrée utilisateur).
     */
    public static class LireNode extends Node {
        private final String variable;  // Variable à remplir

        public LireNode(String variable) {
            this.variable = variable;
        }

        public String getVariable() { return variable; }

        @Override
        public void accepter(NodeVisitor visiteur) {
            visiteur.visiter(this);
        }
    }

    // ==================== NŒUDS D'EXPRESSIONS ====================

    /**
     * Nœud représentant une expression binaire (opération entre deux opérandes).
     * Ex: x + 5, a > b, a ET b, a OU b
     */
    public static class ExpressionBinaire extends Node {
        private final Node gauche;       // Opérande gauche
        private final String operateur;  // Opérateur (+, -, *, /, >, <, ==, ET, OU, etc.)
        private final Node droite;       // Opérande droite

        public ExpressionBinaire(Node gauche, String operateur, Node droite) {
            this.gauche = gauche;
            this.operateur = operateur;
            this.droite = droite;
        }

        public Node getGauche() { return gauche; }
        public String getOperateur() { return operateur; }
        public Node getDroite() { return droite; }

        @Override
        public void accepter(NodeVisitor visiteur) {
            visiteur.visiter(this);
        }
    }

    /**
     * Nœud représentant une expression unaire (opération sur un seul opérande).
     * Ex: NON condition
     */
    public static class ExpressionUnaire extends Node {
        private final String operateur;  // Opérateur (NON)
        private final Node operande;     // Opérande

        public ExpressionUnaire(String operateur, Node operande) {
            this.operateur = operateur;
            this.operande = operande;
        }

        public String getOperateur() { return operateur; }
        public Node getOperande() { return operande; }

        @Override
        public void accepter(NodeVisitor visiteur) {
            visiteur.visiter(this);
        }
    }

    /**
     * Nœud représentant un nombre littéral (entier).
     */
    public static class NombreNode extends Node {
        private final int valeur;

        public NombreNode(int valeur) {
            this.valeur = valeur;
        }

        public int getValeur() { return valeur; }

        @Override
        public void accepter(NodeVisitor visiteur) {
            visiteur.visiter(this);
        }
    }

    /**
     * Nœud représentant un nombre réel littéral (floating-point).
     */
    public static class NombreReelNode extends Node {
        private final double valeur;

        public NombreReelNode(double valeur) {
            this.valeur = valeur;
        }

        public double getValeur() { return valeur; }

        @Override
        public void accepter(NodeVisitor visiteur) {
            visiteur.visiter(this);
        }
    }

    /**
     * Nœud représentant une chaîne de caractères littérale.
     */
    public static class ChaineNode extends Node {
        private final String valeur;

        public ChaineNode(String valeur) {
            this.valeur = valeur;
        }

        public String getValeur() { return valeur; }

        @Override
        public void accepter(NodeVisitor visiteur) {
            visiteur.visiter(this);
        }
    }

    /**
     * Nœud représentant une valeur booléenne littérale (VRAI ou FAUX).
     */
    public static class BooleanNode extends Node {
        private final boolean valeur;

        public BooleanNode(boolean valeur) {
            this.valeur = valeur;
        }

        public boolean getValeur() { return valeur; }

        @Override
        public void accepter(NodeVisitor visiteur) {
            visiteur.visiter(this);
        }
    }

    /**
     * Nœud représentant un identifiant (nom de variable).
     */
    public static class IdentifiantNode extends Node {
        private final String nom;

        public IdentifiantNode(String nom) {
            this.nom = nom;
        }

        public String getNom() { return nom; }

        @Override
        public void accepter(NodeVisitor visiteur) {
            visiteur.visiter(this);
        }
    }

    // ==================== NŒUDS DE FONCTIONS ====================

    /**
     * Classe représentant un paramètre de fonction.
     */
    public static class ParameterNode {
        private final String nom;   // Nom du paramètre
        private final String type;  // Type (ENTIER, REEL, TEXTE)

        public ParameterNode(String nom, String type) {
            this.nom = nom;
            this.type = type;
        }

        public String getNom() { return nom; }
        public String getType() { return type; }
    }

    /**
     * Nœud représentant une définition de fonction.
     * Ex: FONCTION Add(a: ENTIER, b: ENTIER) RETOURNE ENTIER ... FINFONCTION
     */
    public static class FunctionNode extends Node {
        private final String nom;                    // Nom de la fonction
        private final List<ParameterNode> parametres; // Paramètres
        private final String typeRetour;            // Type de retour (null pour procédure)
        private final List<Node> variablesLocales;  // Variables locales (DeclarationNode, ArrayDeclarationNode)
        private final BlockNode corps;               // Corps de la fonction

        // Constructeur sans variables locales (backward compatibility)
        public FunctionNode(String nom, List<ParameterNode> parametres, String typeRetour, BlockNode corps) {
            this.nom = nom;
            this.parametres = parametres;
            this.typeRetour = typeRetour;
            this.variablesLocales = new ArrayList<>();
            this.corps = corps;
        }

        // Constructeur avec variables locales
        public FunctionNode(String nom, List<ParameterNode> parametres, String typeRetour, List<Node> variablesLocales, BlockNode corps) {
            this.nom = nom;
            this.parametres = parametres;
            this.typeRetour = typeRetour;
            this.variablesLocales = variablesLocales;
            this.corps = corps;
        }

        public String getNom() { return nom; }
        public List<ParameterNode> getParametres() { return parametres; }
        public String getTypeRetour() { return typeRetour; }
        public List<Node> getVariablesLocales() { return variablesLocales; }
        public BlockNode getCorps() { return corps; }

        @Override
        public void accepter(NodeVisitor visiteur) {
            visiteur.visiter(this);
        }
    }

    /**
     * Nœud représentant un appel de fonction.
     * Ex: result <- Add(5, 3)
     */
    public static class FunctionCallNode extends Node {
        private final String nom;              // Nom de la fonction
        private final List<Node> arguments;    // Arguments de l'appel

        public FunctionCallNode(String nom, List<Node> arguments) {
            this.nom = nom;
            this.arguments = arguments;
        }

        public String getNom() { return nom; }
        public List<Node> getArguments() { return arguments; }

        @Override
        public void accepter(NodeVisitor visiteur) {
            visiteur.visiter(this);
        }
    }

    /**
     * Nœud représentant une instruction RETOURNE.
     * Ex: RETOURNE (a + b)
     */
    public static class ReturnNode extends Node {
        private final Node valeur;  // Expression à retourner (null pour procédure)

        public ReturnNode(Node valeur) {
            this.valeur = valeur;
        }

        public Node getValeur() { return valeur; }

        @Override
        public void accepter(NodeVisitor visiteur) {
            visiteur.visiter(this);
        }
    }

    // ==================== NŒUDS DE TABLEAUX ====================

    /**
     * Nœud représentant une déclaration de tableau.
     * Ex: nombres[10]: ENTIER
     */
    public static class ArrayDeclarationNode extends Node {
        private final String nom;           // Nom du tableau
        private final int taille;           // Taille du tableau
        private final String type;          // Type des éléments (ENTIER, REEL, TEXTE)

        public ArrayDeclarationNode(String nom, int taille, String type) {
            this.nom = nom;
            this.taille = taille;
            this.type = type;
        }

        public String getNom() { return nom; }
        public int getTaille() { return taille; }
        public String getType() { return type; }

        @Override
        public void accepter(NodeVisitor visiteur) {
            visiteur.visiter(this);
        }
    }

    /**
     * Nœud représentant un accès à un élément de tableau.
     * Ex: nombres[i] ou tableau[5]
     */
    public static class ArrayAccessNode extends Node {
        private final String nom;   // Nom du tableau
        private final Node indice;  // Expression de l'indice

        public ArrayAccessNode(String nom, Node indice) {
            this.nom = nom;
            this.indice = indice;
        }

        public String getNom() { return nom; }
        public Node getIndice() { return indice; }

        @Override
        public void accepter(NodeVisitor visiteur) {
            visiteur.visiter(this);
        }
    }

    // ==================== NŒUDS DE SWITCH ====================

    /**
     * Classe représentant un cas dans un switch.
     */
    public static class CaseNode {
        private final int valeur;           // Valeur du cas
        private final BlockNode instructions; // Instructions du cas

        public CaseNode(int valeur, BlockNode instructions) {
            this.valeur = valeur;
            this.instructions = instructions;
        }

        public int getValeur() { return valeur; }
        public BlockNode getInstructions() { return instructions; }
    }

    /**
     * Nœud représentant une instruction CAS (switch).
     * Ex: CAS variable FAIRE
     *         1: instruction
     *         2: instruction
     *         DEFAUT: instruction
     *     FINCAS
     */
    public static class SwitchNode extends Node {
        private final Node expression;          // Expression à tester
        private final List<CaseNode> cases;     // Cas du switch
        private final BlockNode caseDefaut;     // Cas par défaut (peut être null)

        public SwitchNode(Node expression, List<CaseNode> cases, BlockNode caseDefaut) {
            this.expression = expression;
            this.cases = cases;
            this.caseDefaut = caseDefaut;
        }

        public Node getExpression() { return expression; }
        public List<CaseNode> getCases() { return cases; }
        public BlockNode getCaseDefaut() { return caseDefaut; }

        @Override
        public void accepter(NodeVisitor visiteur) {
            visiteur.visiter(this);
        }
    }
}
