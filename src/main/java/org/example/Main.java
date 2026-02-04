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
 * 4. Effectuer l'analyse sémantique (SemanticAnalyzer) - NOUVEAU
 * 5. Générer le code Python (PythonGenerator)
 * 6. Écrire le résultat dans un fichier .py
 */
public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("=============================================================");
            System.out.println("Compilateur Pseudo-Code (FR) vers Python");
            System.out.println("=============================================================");
            System.out.println("Usage: java -jar compilateur.jar <fichier.pso>");
            System.out.println("");
            System.out.println("Exemple: java -jar compilateur.jar exemple.pso");
            System.out.println("         -> Genere: exemple.py");
            System.out.println("=============================================================");
            System.exit(1);
        }

        String fichierSource = args[0];

        if (!fichierSource.endsWith(".pso")) {
            System.err.println("Erreur: Le fichier source doit avoir l'extension .pso");
            System.exit(1);
        }

        try {
            System.out.println("Lecture du fichier: " + fichierSource);
            Path cheminSource = Paths.get(fichierSource);
            String codeSource = Files.readString(cheminSource);

            System.out.println("Analyse lexicale en cours...");
            Lexer lexer = new Lexer(codeSource);
            List<Token> tokens = lexer.analyser();
            System.out.println("   [OK] " + tokens.size() + " tokens identifies");

            if (args.length > 1 && args[1].equals("--debug")) {
                System.out.println("\n--- TOKENS ---");
                for (Token token : tokens) {
                    System.out.println("   " + token);
                }
                System.out.println("--------------\n");
            }

            System.out.println("Analyse syntaxique en cours...");
            Parser parser = new Parser(tokens);
            AST.ProgrammeNode ast = parser.analyser();
            System.out.println("   [OK] AST construit avec succes");
            System.out.println("   [OK] Algorithme: " + ast.getNom());
            System.out.println("   [OK] " + ast.getDeclarations().size() + " variable(s) declaree(s)");
            System.out.println("   [OK] " + ast.getCorps().getInstructions().size() + " instruction(s) principale(s)");

            System.out.println("Analyse semantique en cours...");
            SemanticAnalyzer analyseurSemantique = new SemanticAnalyzer();
            try {
                analyseurSemantique.analyser(ast);
                System.out.println("   [OK] Aucune erreur semantique detectee");
                System.out.println("   [OK] Toutes les variables sont declarees");
                System.out.println("   [OK] Tous les types sont coherents");
            } catch (SemanticException e) {
                System.err.println("[ERREUR] Erreur semantique detectee:");
                System.err.println(e.getMessage());
                System.err.println("\nLe fichier Python n'a pas ete genere.");
                System.exit(1);
            }

            System.out.println("Generation du code Python...");
            PythonGenerator generateur = new PythonGenerator();
            String codePython = generateur.generer(ast);

            String fichierSortie = fichierSource.replace(".pso", ".py");
            Path cheminSortie = Paths.get(fichierSortie);
            Files.writeString(cheminSortie, codePython);

            System.out.println("[SUCCES] Compilation reussie!");
            System.out.println("Fichier genere: " + fichierSortie);
            System.out.println();
            System.out.println("Code Python genere:");
            System.out.println(codePython);

        } catch (IOException e) {
            System.err.println("[ERREUR] Erreur de lecture/ecriture: " + e.getMessage());
            System.exit(1);
        } catch (RuntimeException e) {
            System.err.println("[ERREUR] Erreur de compilation: " + e.getMessage());
            System.exit(1);
        }
    }
}
