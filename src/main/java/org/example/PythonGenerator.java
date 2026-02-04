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

    // Ensemble des variables déclarées comme ENTIER (pour savoir si on doit convertir l'input en int)
    private final Set<String> variablesEntieres;
    // Ensemble des variables déclarées comme REEL (pour savoir si on doit convertir l'input en float)
    private final Set<String> variablesReelles;

    public PythonGenerator() {
        this.code = new StringBuilder();
        this.niveauIndentation = 0;
        this.variablesEntieres = new HashSet<>();
        this.variablesReelles = new HashSet<>();
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

        // Enregistrer les types des variables et générer les déclarations
        for (AST.Node decl : programme.getDeclarations()) {
            if (decl instanceof AST.DeclarationNode d) {
                if (d.getType().equals("ENTIER")) {
                    variablesEntieres.add(d.getNom());
                } else if (d.getType().equals("REEL")) {
                    variablesReelles.add(d.getNom());
                }
            } else if (decl instanceof AST.ArrayDeclarationNode arr) {
                // Générer la déclaration du tableau
                arr.accepter(this);
            }
        }

        // Générer les définitions de fonctions
        for (AST.FunctionNode fonction : programme.getFonctions()) {
            fonction.accepter(this);
        }

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
        else if (expression instanceof AST.NombreReelNode nombreReel) {
            return String.valueOf(nombreReel.getValeur());
        }
        else if (expression instanceof AST.ChaineNode chaine) {
            // Échapper les guillemets dans la chaîne
            String valeurEchappee = chaine.getValeur().replace("\"", "\\\"");
            return "\"" + valeurEchappee + "\"";
        }
        else if (expression instanceof AST.BooleanNode bool) {
            return bool.getValeur() ? "True" : "False";
        }
        else if (expression instanceof AST.IdentifiantNode identifiant) {
            return identifiant.getNom();
        }
        else if (expression instanceof AST.ArrayAccessNode arrayAccess) {
            // nom[indice]
            return arrayAccess.getNom() + "[" + genererExpression(arrayAccess.getIndice()) + "]";
        }
        else if (expression instanceof AST.FunctionCallNode appelFonction) {
            // nom_fonction(arg1, arg2, ...)
            StringBuilder sb = new StringBuilder();
            sb.append(appelFonction.getNom()).append("(");

            boolean premier = true;
            for (AST.Node arg : appelFonction.getArguments()) {
                if (!premier) {
                    sb.append(", ");
                }
                sb.append(genererExpression(arg));
                premier = false;
            }

            sb.append(")");
            return sb.toString();
        }
        else if (expression instanceof AST.ExpressionBinaire binaire) {
            String gauche = genererExpression(binaire.getGauche());
            String droite = genererExpression(binaire.getDroite());
            String operateur = binaire.getOperateur();

            // Conversion des opérateurs logiques du pseudo-code vers Python
            String operateurPython = operateur;
            if (operateur.equals("ET")) {
                operateurPython = "and";
            } else if (operateur.equals("OU")) {
                operateurPython = "or";
            }

            return "(" + gauche + " " + operateurPython + " " + droite + ")";
        }
        else if (expression instanceof AST.ExpressionUnaire unaire) {
            String operande = genererExpression(unaire.getOperande());
            String operateur = unaire.getOperateur();

            // Conversion des opérateurs unaires du pseudo-code vers Python
            String operateurPython = operateur;
            if (operateur.equals("NON")) {
                operateurPython = "not";
            }

            return "(" + operateurPython + " " + operande + ")";
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
        // Cas spécial: appel de fonction comme statement (variable = "_call")
        if (node.getVariable().equals("_call") && node.getValeur() instanceof AST.FunctionCallNode) {
            // Générer juste l'appel de fonction sans affectation
            ajouterIndentation();
            code.append(genererExpression(node.getValeur()));
            code.append("\n");
        } else if (node.getValeur() instanceof AST.ExpressionBinaire expr && expr.getOperateur().equals("array_set")) {
            // Affectation à un élément de tableau: nom[indice] = valeur
            ajouterIndentation();
            code.append(genererExpression(expr.getGauche()));
            code.append(" = ");
            code.append(genererExpression(expr.getDroite()));
            code.append("\n");
        } else {
            // Affectation normale: variable = expression
            ajouterIndentation();
            code.append(node.getVariable());
            code.append(" = ");
            code.append(genererExpression(node.getValeur()));
            code.append("\n");
        }
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
    public void visiter(AST.PourNode node) {
        // for variable in range(debut, fin + 1):
        ajouterIndentation();
        code.append("for ");
        code.append(node.getVariable());
        code.append(" in range(");
        code.append(genererExpression(node.getDebut()));
        code.append(", ");
        code.append(genererExpression(node.getFin()));
        code.append(" + 1):\n");

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
        // Convertir l'input selon le type de la variable
        // - ENTIER: int(input())
        // - REEL: float(input())
        // - TEXTE: input()
        ajouterIndentation();
        code.append(node.getVariable());
        code.append(" = ");

        if (variablesEntieres.contains(node.getVariable())) {
            // variable = int(input())
            code.append("int(input())");
        } else if (variablesReelles.contains(node.getVariable())) {
            // variable = float(input())
            code.append("float(input())");
        } else {
            // variable = input() (pour TEXTE ou autres)
            code.append("input()");
        }

        code.append("\n");
    }

    @Override
    public void visiter(AST.ExpressionBinaire node) {
        // Cette méthode n'est pas utilisée directement, on utilise genererExpression()
    }

    @Override
    public void visiter(AST.ExpressionUnaire node) {
        // Cette méthode n'est pas utilisée directement, on utilise genererExpression()
    }

    @Override
    public void visiter(AST.NombreNode node) {
        // Cette méthode n'est pas utilisée directement, on utilise genererExpression()
    }

    @Override
    public void visiter(AST.NombreReelNode node) {
        // Cette méthode n'est pas utilisée directement, on utilise genererExpression()
    }

    @Override
    public void visiter(AST.ChaineNode node) {
        // Cette méthode n'est pas utilisée directement, on utilise genererExpression()
    }

    @Override
    public void visiter(AST.BooleanNode node) {
        // Cette méthode n'est pas utilisée directement, on utilise genererExpression()
    }

    @Override
    public void visiter(AST.IdentifiantNode node) {
        // Cette méthode n'est pas utilisée directement, on utilise genererExpression()
    }

    @Override
    public void visiter(AST.FunctionNode node) {
        // Générer une fonction Python
        // def nom_fonction(param1, param2, ...):
        code.append("def ").append(node.getNom()).append("(");

        boolean premier = true;
        for (AST.ParameterNode param : node.getParametres()) {
            if (!premier) {
                code.append(", ");
            }
            code.append(param.getNom());
            premier = false;
        }

        code.append("):\n");

        // Corps de la fonction
        niveauIndentation++;
        if (node.getCorps().getInstructions().isEmpty()) {
            ajouterIndentation();
            code.append("pass\n");
        } else {
            node.getCorps().accepter(this);
        }
        niveauIndentation--;

        code.append("\n");
    }

    @Override
    public void visiter(AST.FunctionCallNode node) {
        // Cette méthode n'est pas utilisée directement, on utilise genererExpression()
    }

    @Override
    public void visiter(AST.ReturnNode node) {
        // Générer une instruction return
        ajouterIndentation();
        code.append("return ");
        if (node.getValeur() != null) {
            code.append(genererExpression(node.getValeur()));
        }
        code.append("\n");
    }

    @Override
    public void visiter(AST.ArrayDeclarationNode node) {
        // Génération de tableau: nom = [0] * taille
        ajouterIndentation();
        code.append(node.getNom());
        code.append(" = [0] * ").append(node.getTaille()).append("\n");
    }

    @Override
    public void visiter(AST.ArrayAccessNode node) {
        // Cette méthode n'est pas utilisée directement, on utilise genererExpression()
    }

    @Override
    public void visiter(AST.SwitchNode node) {
        // Generate if/elif/else chain for switch statement
        String expression = genererExpression(node.getExpression());
        boolean firstCase = true;

        for (AST.CaseNode caseNode : node.getCases()) {
            if (firstCase) {
                ajouterIndentation();
                code.append("if ").append(expression).append(" == ").append(caseNode.getValeur()).append(":\n");
                firstCase = false;
            } else {
                ajouterIndentation();
                code.append("elif ").append(expression).append(" == ").append(caseNode.getValeur()).append(":\n");
            }

            niveauIndentation++;
            if (caseNode.getInstructions().getInstructions().isEmpty()) {
                ajouterIndentation();
                code.append("pass\n");
            } else {
                caseNode.getInstructions().accepter(this);
            }
            niveauIndentation--;
        }

        // Handle default case
        if (node.getCaseDefaut() != null) {
            ajouterIndentation();
            code.append("else:\n");
            niveauIndentation++;
            if (node.getCaseDefaut().getInstructions().isEmpty()) {
                ajouterIndentation();
                code.append("pass\n");
            } else {
                node.getCaseDefaut().accepter(this);
            }
            niveauIndentation--;
        }
    }
}
