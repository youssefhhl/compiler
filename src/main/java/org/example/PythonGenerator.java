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

    private final StringBuilder code;
    private int niveauIndentation;
    private static final String INDENT = "    ";

    private final Set<String> variablesEntieres;
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

        for (AST.Node decl : programme.getDeclarations()) {
            if (decl instanceof AST.DeclarationNode d) {
                if (d.getType().equals("ENTIER")) {
                    variablesEntieres.add(d.getNom());
                } else if (d.getType().equals("REEL")) {
                    variablesReelles.add(d.getNom());
                }
            } else if (decl instanceof AST.ArrayDeclarationNode arr) {
                arr.accepter(this);
            }
        }

        for (AST.FunctionNode fonction : programme.getFonctions()) {
            fonction.accepter(this);
        }

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
            return arrayAccess.getNom() + "[" + genererExpression(arrayAccess.getIndice()) + "]";
        }
        else if (expression instanceof AST.FunctionCallNode appelFonction) {
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

            String operateurPython = operateur;
            if (operateur.equals("NON")) {
                operateurPython = "not";
            }

            return "(" + operateurPython + " " + operande + ")";
        }

        throw new RuntimeException("Type d'expression non géré: " + expression.getClass().getName());
    }

    @Override
    public void visiter(AST.ProgrammeNode node) {
    }

    @Override
    public void visiter(AST.BlockNode node) {
        for (AST.Node instruction : node.getInstructions()) {
            instruction.accepter(this);
        }
    }

    @Override
    public void visiter(AST.DeclarationNode node) {
    }

    @Override
    public void visiter(AST.AffectationNode node) {
        if (node.getVariable().equals("_call") && node.getValeur() instanceof AST.FunctionCallNode) {
            ajouterIndentation();
            code.append(genererExpression(node.getValeur()));
            code.append("\n");
        } else if (node.getValeur() instanceof AST.ExpressionBinaire expr && expr.getOperateur().equals("array_set")) {
            ajouterIndentation();
            code.append(genererExpression(expr.getGauche()));
            code.append(" = ");
            code.append(genererExpression(expr.getDroite()));
            code.append("\n");
        } else {
            ajouterIndentation();
            code.append(node.getVariable());
            code.append(" = ");
            code.append(genererExpression(node.getValeur()));
            code.append("\n");
        }
    }

    @Override
    public void visiter(AST.SiNode node) {
        ajouterIndentation();
        code.append("if ");
        code.append(genererExpression(node.getCondition()));
        code.append(":\n");

        niveauIndentation++;
        if (node.getBlocAlors().getInstructions().isEmpty()) {
            ajouterIndentation();
            code.append("pass\n");
        } else {
            node.getBlocAlors().accepter(this);
        }
        niveauIndentation--;

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
        ajouterIndentation();
        code.append("while ");
        code.append(genererExpression(node.getCondition()));
        code.append(":\n");

        niveauIndentation++;
        if (node.getCorps().getInstructions().isEmpty()) {
            ajouterIndentation();
            code.append("pass\n");
        } else {
            node.getCorps().accepter(this);
        }
        niveauIndentation--;
    }

    @Override
    public void visiter(AST.PourNode node) {
        ajouterIndentation();
        code.append("for ");
        code.append(node.getVariable());
        code.append(" in range(");
        code.append(genererExpression(node.getDebut()));
        code.append(", ");
        code.append(genererExpression(node.getFin()));
        code.append(" + 1):\n");

        niveauIndentation++;
        if (node.getCorps().getInstructions().isEmpty()) {
            ajouterIndentation();
            code.append("pass\n");
        } else {
            node.getCorps().accepter(this);
        }
        niveauIndentation--;
    }

    @Override
    public void visiter(AST.EcrireNode node) {
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
        ajouterIndentation();
        code.append(node.getVariable());
        code.append(" = ");

        if (variablesEntieres.contains(node.getVariable())) {
            code.append("int(input())");
        } else if (variablesReelles.contains(node.getVariable())) {
            code.append("float(input())");
        } else {
            code.append("input()");
        }

        code.append("\n");
    }

    @Override
    public void visiter(AST.ExpressionBinaire node) {
    }

    @Override
    public void visiter(AST.ExpressionUnaire node) {
    }

    @Override
    public void visiter(AST.NombreNode node) {
    }

    @Override
    public void visiter(AST.NombreReelNode node) {
    }

    @Override
    public void visiter(AST.ChaineNode node) {
    }

    @Override
    public void visiter(AST.BooleanNode node) {
    }

    @Override
    public void visiter(AST.IdentifiantNode node) {
    }

    @Override
    public void visiter(AST.FunctionNode node) {
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

        niveauIndentation++;

        // Generate initialization for local variables
        if (!node.getVariablesLocales().isEmpty()) {
            for (AST.Node varDecl : node.getVariablesLocales()) {
                if (varDecl instanceof AST.DeclarationNode d) {
                    ajouterIndentation();
                    code.append(d.getNom()).append(" = ");

                    // Initialize with appropriate default value based on type
                    String type = d.getType();
                    if (type.equals("ENTIER")) {
                        code.append("0");
                    } else if (type.equals("REEL")) {
                        code.append("0.0");
                    } else if (type.equals("TEXTE")) {
                        code.append("\"\"");
                    } else if (type.equals("BOOLEEN")) {
                        code.append("False");
                    } else {
                        code.append("None");
                    }
                    code.append("\n");
                }
            }
        }

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
    }

    @Override
    public void visiter(AST.ReturnNode node) {
        ajouterIndentation();
        code.append("return ");
        if (node.getValeur() != null) {
            code.append(genererExpression(node.getValeur()));
        }
        code.append("\n");
    }

    @Override
    public void visiter(AST.ArrayDeclarationNode node) {
        ajouterIndentation();
        code.append(node.getNom());
        code.append(" = [0] * ").append(node.getTaille()).append("\n");
    }

    @Override
    public void visiter(AST.ArrayAccessNode node) {
    }

    @Override
    public void visiter(AST.SwitchNode node) {
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
