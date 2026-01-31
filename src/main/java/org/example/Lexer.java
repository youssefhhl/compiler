package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Analyseur lexical (Lexer) pour le pseudo-code français.
 * Transforme le code source en une liste de tokens.
 */
public class Lexer {
    private final String source;      // Le code source à analyser
    private final List<Token> tokens; // Liste des tokens générés
    private int position;             // Position actuelle dans le source
    private int ligne;                // Ligne actuelle
    private int colonne;              // Colonne actuelle

    // Table des mots-clés reconnus (en majuscules)
    private static final Map<String, TokenType> MOTS_CLES = new HashMap<>();

    static {
        // Structure du programme
        MOTS_CLES.put("ALGORITHME", TokenType.ALGORITHME);
        MOTS_CLES.put("VARIABLES", TokenType.VARIABLES);
        MOTS_CLES.put("DEBUT", TokenType.DEBUT);
        MOTS_CLES.put("FIN", TokenType.FIN);

        // Types
        MOTS_CLES.put("ENTIER", TokenType.ENTIER);
        MOTS_CLES.put("TEXTE", TokenType.TEXTE);

        // E/S
        MOTS_CLES.put("ECRIRE", TokenType.ECRIRE);
        MOTS_CLES.put("AFFICHER", TokenType.ECRIRE); // Alias
        MOTS_CLES.put("LIRE", TokenType.LIRE);

        // Conditions
        MOTS_CLES.put("SI", TokenType.SI);
        MOTS_CLES.put("ALORS", TokenType.ALORS);
        MOTS_CLES.put("SINON", TokenType.SINON);
        MOTS_CLES.put("FINSI", TokenType.FINSI);

        // Boucles
        MOTS_CLES.put("TANTQUE", TokenType.TANTQUE);
        MOTS_CLES.put("FAIRE", TokenType.FAIRE);
        MOTS_CLES.put("FINTANTQUE", TokenType.FINTANTQUE);
    }

    /**
     * Constructeur du Lexer.
     * @param source Le code source pseudo-code à analyser
     */
    public Lexer(String source) {
        this.source = source;
        this.tokens = new ArrayList<>();
        this.position = 0;
        this.ligne = 1;
        this.colonne = 1;
    }

    /**
     * Analyse le code source et retourne la liste des tokens.
     * @return Liste de tokens
     */
    public List<Token> analyser() {
        while (!estFin()) {
            char c = caractereActuel();

            // Ignorer les espaces et tabulations
            if (c == ' ' || c == '\t') {
                avancer();
            }
            // Nouvelle ligne
            else if (c == '\n') {
                ajouterToken(TokenType.NOUVELLE_LIGNE, "\\n");
                avancer();
                ligne++;
                colonne = 1;
            }
            // Retour chariot (Windows)
            else if (c == '\r') {
                avancer();
            }
            // Commentaires (ligne commençant par //)
            else if (c == '/' && regarderSuivant() == '/') {
                ignorerCommentaire();
            }
            // Opérateur d'affectation <-
            else if (c == '<' && regarderSuivant() == '-') {
                int col = colonne;
                avancer();
                avancer();
                tokens.add(new Token(TokenType.AFFECTATION, "<-", ligne, col));
            }
            // Opérateurs de comparaison
            else if (c == '<' && regarderSuivant() == '=') {
                int col = colonne;
                avancer();
                avancer();
                tokens.add(new Token(TokenType.INF_EGAL, "<=", ligne, col));
            }
            else if (c == '>' && regarderSuivant() == '=') {
                int col = colonne;
                avancer();
                avancer();
                tokens.add(new Token(TokenType.SUP_EGAL, ">=", ligne, col));
            }
            else if (c == '=' && regarderSuivant() == '=') {
                int col = colonne;
                avancer();
                avancer();
                tokens.add(new Token(TokenType.EGAL, "==", ligne, col));
            }
            else if (c == '!' && regarderSuivant() == '=') {
                int col = colonne;
                avancer();
                avancer();
                tokens.add(new Token(TokenType.DIFFERENT, "!=", ligne, col));
            }
            // Opérateurs simples
            else if (c == '<') {
                ajouterToken(TokenType.INFERIEUR, "<");
                avancer();
            }
            else if (c == '>') {
                ajouterToken(TokenType.SUPERIEUR, ">");
                avancer();
            }
            else if (c == '+') {
                ajouterToken(TokenType.PLUS, "+");
                avancer();
            }
            else if (c == '-') {
                ajouterToken(TokenType.MOINS, "-");
                avancer();
            }
            else if (c == '*') {
                ajouterToken(TokenType.MULTIPLIE, "*");
                avancer();
            }
            else if (c == '/') {
                ajouterToken(TokenType.DIVISE, "/");
                avancer();
            }
            // Ponctuation
            else if (c == '(') {
                ajouterToken(TokenType.PARENTHESE_G, "(");
                avancer();
            }
            else if (c == ')') {
                ajouterToken(TokenType.PARENTHESE_D, ")");
                avancer();
            }
            else if (c == ':') {
                ajouterToken(TokenType.DEUX_POINTS, ":");
                avancer();
            }
            else if (c == ',') {
                ajouterToken(TokenType.VIRGULE, ",");
                avancer();
            }
            // Chaînes de caractères
            else if (c == '"') {
                lireChaine();
            }
            // Nombres
            else if (Character.isDigit(c)) {
                lireNombre();
            }
            // Identifiants et mots-clés
            else if (Character.isLetter(c) || c == '_') {
                lireIdentifiant();
            }
            // Caractère non reconnu
            else {
                throw new RuntimeException("Caractère non reconnu '" + c + "' à la ligne "
                        + ligne + ", colonne " + colonne);
            }
        }

        // Ajouter le token de fin de fichier
        tokens.add(new Token(TokenType.EOF, "", ligne, colonne));
        return tokens;
    }

    /**
     * Vérifie si on a atteint la fin du source.
     */
    private boolean estFin() {
        return position >= source.length();
    }

    /**
     * Retourne le caractère actuel.
     */
    private char caractereActuel() {
        if (estFin()) return '\0';
        return source.charAt(position);
    }

    /**
     * Regarde le caractère suivant sans avancer.
     */
    private char regarderSuivant() {
        if (position + 1 >= source.length()) return '\0';
        return source.charAt(position + 1);
    }

    /**
     * Avance d'un caractère.
     */
    private void avancer() {
        position++;
        colonne++;
    }

    /**
     * Ajoute un token à la liste.
     */
    private void ajouterToken(TokenType type, String valeur) {
        tokens.add(new Token(type, valeur, ligne, colonne));
    }

    /**
     * Ignore un commentaire de ligne (//).
     */
    private void ignorerCommentaire() {
        while (!estFin() && caractereActuel() != '\n') {
            avancer();
        }
    }

    /**
     * Lit une chaîne de caractères entre guillemets.
     */
    private void lireChaine() {
        int colonneDebut = colonne;
        avancer(); // Sauter le guillemet ouvrant

        StringBuilder sb = new StringBuilder();
        while (!estFin() && caractereActuel() != '"') {
            if (caractereActuel() == '\n') {
                throw new RuntimeException("Chaîne non terminée à la ligne " + ligne);
            }
            sb.append(caractereActuel());
            avancer();
        }

        if (estFin()) {
            throw new RuntimeException("Chaîne non terminée à la ligne " + ligne);
        }

        avancer(); // Sauter le guillemet fermant
        tokens.add(new Token(TokenType.CHAINE, sb.toString(), ligne, colonneDebut));
    }

    /**
     * Lit un nombre entier.
     */
    private void lireNombre() {
        int colonneDebut = colonne;
        StringBuilder sb = new StringBuilder();

        while (!estFin() && Character.isDigit(caractereActuel())) {
            sb.append(caractereActuel());
            avancer();
        }

        tokens.add(new Token(TokenType.NOMBRE, sb.toString(), ligne, colonneDebut));
    }

    /**
     * Lit un identifiant ou un mot-clé.
     */
    private void lireIdentifiant() {
        int colonneDebut = colonne;
        StringBuilder sb = new StringBuilder();

        while (!estFin() && (Character.isLetterOrDigit(caractereActuel())
                || caractereActuel() == '_')) {
            sb.append(caractereActuel());
            avancer();
        }

        String mot = sb.toString();
        String motMajuscule = mot.toUpperCase();

        // Vérifier si c'est un mot-clé
        TokenType type = MOTS_CLES.getOrDefault(motMajuscule, TokenType.IDENTIFIANT);

        // Pour les identifiants, conserver la casse originale
        String valeur = (type == TokenType.IDENTIFIANT) ? mot : motMajuscule;

        tokens.add(new Token(type, valeur, ligne, colonneDebut));
    }
}
