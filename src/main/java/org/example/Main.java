package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Classe principale du compilateur Pseudo-code vers Python.
 *
 * Usage: java -jar compilateur.jar fichier.pso
 *
 * Le compilateur va :
 * 1. Lire le fichier source .pso (pseudo-code)
 * 2. Effectuer l'analyse lexicale (Lexer)
 * 3. Effectuer l'analyse syntaxique (Parser) et construire l'AST
 * 4. Générer le code Python (PythonGenerator)
 * 5. Écrire le résultat dans un fichier .py
 */
public class Main {

    public static void main(String[] args) {
        // Vérifier les arguments
        if (args.length < 1) {
            System.out.println("╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║     Compilateur Pseudo-Code (FR) vers Python              ║");
            System.out.println("╠═══════════════════════════════════════════════════════════╣");
            System.out.println("║ Usage: java -jar compilateur.jar <fichier.pso>            ║");
            System.out.println("║                                                           ║");
            System.out.println("║ Exemple: java -jar compilateur.jar exemple.pso            ║");
            System.out.println("║          -> Génère: exemple.py                            ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
            System.exit(1);
        }

        String fichierSource = args[0];

        // Vérifier que le fichier a l'extension .pso
        if (!fichierSource.endsWith(".pso")) {
            System.err.println("Erreur: Le fichier source doit avoir l'extension .pso");
            System.exit(1);
        }

        try {
            // Étape 1: Lire le fichier source
            System.out.println("📂 Lecture du fichier: " + fichierSource);
            Path cheminSource = Paths.get(fichierSource);
            String codeSource = Files.readString(cheminSource);

            // Étape 2: Analyse lexicale
            System.out.println("🔍 Analyse lexicale en cours...");
            Lexer lexer = new Lexer(codeSource);
            List<Token> tokens = lexer.analyser();
            System.out.println("   ✓ " + tokens.size() + " tokens identifiés");

            // Afficher les tokens en mode debug (optionnel)
            if (args.length > 1 && args[1].equals("--debug")) {
                System.out.println("\n--- TOKENS ---");
                for (Token token : tokens) {
                    System.out.println("   " + token);
                }
                System.out.println("--------------\n");
            }

            // Étape 3: Analyse syntaxique
            System.out.println("🌳 Analyse syntaxique en cours...");
            Parser parser = new Parser(tokens);
            AST.ProgrammeNode ast = parser.analyser();
            System.out.println("   ✓ AST construit avec succès");
            System.out.println("   ✓ Algorithme: " + ast.getNom());
            System.out.println("   ✓ " + ast.getDeclarations().size() + " variable(s) déclarée(s)");
            System.out.println("   ✓ " + ast.getCorps().getInstructions().size() + " instruction(s) principale(s)");

            // Étape 4: Génération du code Python
            System.out.println("🐍 Génération du code Python...");
            PythonGenerator generateur = new PythonGenerator();
            String codePython = generateur.generer(ast);

            // Étape 5: Écrire le fichier Python
            String fichierSortie = fichierSource.replace(".pso", ".py");
            Path cheminSortie = Paths.get(fichierSortie);
            Files.writeString(cheminSortie, codePython);

            System.out.println("✅ Compilation réussie!");
            System.out.println("📄 Fichier généré: " + fichierSortie);
            System.out.println();
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("Code Python généré:");
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println(codePython);
            System.out.println("═══════════════════════════════════════════════════════════");

        } catch (IOException e) {
            System.err.println("❌ Erreur de lecture/écriture: " + e.getMessage());
            System.exit(1);
        } catch (RuntimeException e) {
            System.err.println("❌ Erreur de compilation: " + e.getMessage());
            System.exit(1);
        }
    }
}
