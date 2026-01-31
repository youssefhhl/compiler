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
        void visiter(EcrireNode node);
        void visiter(LireNode node);
        void visiter(ExpressionBinaire node);
        void visiter(NombreNode node);
        void visiter(ChaineNode node);
        void visiter(IdentifiantNode node);
    }

    // ==================== NŒUDS DE STRUCTURE ====================

    /**
     * Nœud représentant le programme complet.
     */
    public static class ProgrammeNode extends Node {
        private final String nom;                    // Nom de l'algorithme
        private final List<DeclarationNode> declarations; // Déclarations de variables
        private final BlockNode corps;               // Corps du programme (DEBUT...FIN)

        public ProgrammeNode(String nom, List<DeclarationNode> declarations, BlockNode corps) {
            this.nom = nom;
            this.declarations = declarations;
            this.corps = corps;
        }

        public String getNom() { return nom; }
        public List<DeclarationNode> getDeclarations() { return declarations; }
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
     * Ex: x + 5, a > b
     */
    public static class ExpressionBinaire extends Node {
        private final Node gauche;       // Opérande gauche
        private final String operateur;  // Opérateur (+, -, *, /, >, <, ==, etc.)
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
     * Nœud représentant un nombre littéral.
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
}
