package org.example;

import java.util.HashSet;
import java.util.Set;

/**
 * Générateur de code Python à partir de l'AST.
 * Parcourt l'arbre syntaxique et génère du code Python simple et procédural.
 *
 * RÈGLES IMPORTANTES :
 * - Pas de try/except
 * - Pas de def main()
 * - Pas de classes
 * - Pas de if __name__ == "__main__"
 * - Code exécuté séquentiellement (script plat)
 * - Indentation de 4 espaces pour les blocs
 */
public class PythonGenerator implements AST.NodeVisitor {

    private final StringBuilder code;        // Code Python généré
    private int niveauIndentation;           // Niveau d'indentation actuel
    private static final String INDENT = "    "; // 4 espaces

    // Ensemble des variables déclarées comme ENTIER (pour savoir si on doit convertir l'input)
    private final Set<String> variablesEntieres;

    public PythonGenerator() {
        this.code = new StringBuilder();
        this.niveauIndentation = 0;
        this.variablesEntieres = new HashSet<>();
    }

    /**
     * Génère le code Python à partir de l'AST.
     * @param programme Le nœud racine du programme
     * @return Le code Python sous forme de chaîne
     */
    public String generer(AST.ProgrammeNode programme) {
        // Ajouter un commentaire avec le nom de l'algorithme
        code.append("# Algorithme: ").append(programme.getNom()).append("\n");
        code.append("# Code généré automatiquement à partir du pseudo-code\n\n");

        // Enregistrer les types des variables pour la conversion des entrées
        for (AST.DeclarationNode decl : programme.getDeclarations()) {
            if (decl.getType().equals("ENTIER")) {
                variablesEntieres.add(decl.getNom());
            }
        }

        // Note: On ne génère pas les déclarations de variables car Python est dynamiquement typé

        // Générer le corps du programme
        programme.getCorps().accepter(this);

        return code.toString();
    }

    /**
     * Ajoute l'indentation actuelle au code.
     */
    private void ajouterIndentation() {
        for (int i = 0; i < niveauIndentation; i++) {
            code.append(INDENT);
        }
    }

    /**
     * Génère le code d'une expression et le retourne sous forme de chaîne.
     */
    private String genererExpression(AST.Node expression) {
        if (expression instanceof AST.NombreNode nombre) {
            return String.valueOf(nombre.getValeur());
        }
        else if (expression instanceof AST.ChaineNode chaine) {
            // Échapper les guillemets dans la chaîne
            String valeurEchappee = chaine.getValeur().replace("\"", "\\\"");
            return "\"" + valeurEchappee + "\"";
        }
        else if (expression instanceof AST.IdentifiantNode identifiant) {
            return identifiant.getNom();
        }
        else if (expression instanceof AST.ExpressionBinaire binaire) {
            String gauche = genererExpression(binaire.getGauche());
            String droite = genererExpression(binaire.getDroite());
            String operateur = binaire.getOperateur();

            // Conversion de l'opérateur si nécessaire
            // (les opérateurs Python sont les mêmes que ceux du pseudo-code)

            return "(" + gauche + " " + operateur + " " + droite + ")";
        }

        throw new RuntimeException("Type d'expression non géré: " + expression.getClass().getName());
    }

    // ==================== IMPLÉMENTATION DU VISITEUR ====================

    @Override
    public void visiter(AST.ProgrammeNode node) {
        // Cette méthode n'est pas utilisée directement, on utilise generer()
    }

    @Override
    public void visiter(AST.BlockNode node) {
        // Générer chaque instruction du bloc
        for (AST.Node instruction : node.getInstructions()) {
            instruction.accepter(this);
        }
    }

    @Override
    public void visiter(AST.DeclarationNode node) {
        // Les déclarations ne produisent rien en Python (typage dynamique)
        // On les enregistre juste pour savoir le type lors de LIRE
    }

    @Override
    public void visiter(AST.AffectationNode node) {
        // variable = expression
        ajouterIndentation();
        code.append(node.getVariable());
        code.append(" = ");
        code.append(genererExpression(node.getValeur()));
        code.append("\n");
    }

    @Override
    public void visiter(AST.SiNode node) {
        // if condition:
        ajouterIndentation();
        code.append("if ");
        code.append(genererExpression(node.getCondition()));
        code.append(":\n");

        // Bloc ALORS (avec indentation augmentée)
        niveauIndentation++;
        if (node.getBlocAlors().getInstructions().isEmpty()) {
            // En Python, un bloc vide nécessite 'pass'
            ajouterIndentation();
            code.append("pass\n");
        } else {
            node.getBlocAlors().accepter(this);
        }
        niveauIndentation--;

        // Bloc SINON (optionnel)
        if (node.getBlocSinon() != null) {
            ajouterIndentation();
            code.append("else:\n");

            niveauIndentation++;
            if (node.getBlocSinon().getInstructions().isEmpty()) {
                ajouterIndentation();
                code.append("pass\n");
            } else {
                node.getBlocSinon().accepter(this);
            }
            niveauIndentation--;
        }
    }

    @Override
    public void visiter(AST.TantQueNode node) {
        // while condition:
        ajouterIndentation();
        code.append("while ");
        code.append(genererExpression(node.getCondition()));
        code.append(":\n");

        // Corps de la boucle (avec indentation augmentée)
        niveauIndentation++;
        if (node.getCorps().getInstructions().isEmpty()) {
            // En Python, un bloc vide nécessite 'pass'
            ajouterIndentation();
            code.append("pass\n");
        } else {
            node.getCorps().accepter(this);
        }
        niveauIndentation--;
    }

    @Override
    public void visiter(AST.EcrireNode node) {
        // print(expression1, expression2, ...)
        ajouterIndentation();
        code.append("print(");

        boolean premier = true;
        for (AST.Node expr : node.getExpressions()) {
            if (!premier) {
                code.append(", ");
            }
            code.append(genererExpression(expr));
            premier = false;
        }

        code.append(")\n");
    }

    @Override
    public void visiter(AST.LireNode node) {
        // Si la variable est déclarée comme ENTIER, on convertit avec int()
        // Sinon, on utilise simplement input()
        ajouterIndentation();
        code.append(node.getVariable());
        code.append(" = ");

        if (variablesEntieres.contains(node.getVariable())) {
            // variable = int(input())
            code.append("int(input())");
        } else {
            // variable = input()
            code.append("input()");
        }

        code.append("\n");
    }

    @Override
    public void visiter(AST.ExpressionBinaire node) {
        // Cette méthode n'est pas utilisée directement, on utilise genererExpression()
    }

    @Override
    public void visiter(AST.NombreNode node) {
        // Cette méthode n'est pas utilisée directement, on utilise genererExpression()
    }

    @Override
    public void visiter(AST.ChaineNode node) {
        // Cette méthode n'est pas utilisée directement, on utilise genererExpression()
    }

    @Override
    public void visiter(AST.IdentifiantNode node) {
        // Cette méthode n'est pas utilisée directement, on utilise genererExpression()
    }
}
