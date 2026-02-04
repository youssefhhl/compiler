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
    private final String source;
    private final List<Token> tokens;
    private int position;
    private int ligne;
    private int colonne;

    private static final Map<String, TokenType> MOTS_CLES = new HashMap<>();

    static {
        MOTS_CLES.put("ALGORITHME", TokenType.ALGORITHME);
        MOTS_CLES.put("VARIABLES", TokenType.VARIABLES);
        MOTS_CLES.put("DEBUT", TokenType.DEBUT);
        MOTS_CLES.put("FIN", TokenType.FIN);
        MOTS_CLES.put("FONCTION", TokenType.FONCTION);
        MOTS_CLES.put("PROCEDURE", TokenType.PROCEDURE);
        MOTS_CLES.put("RETOURNE", TokenType.RETOURNE);
        MOTS_CLES.put("FINFONCTION", TokenType.FINFONCTION);
        MOTS_CLES.put("FINPROCEDURE", TokenType.FINPROCEDURE);

        MOTS_CLES.put("ENTIER", TokenType.ENTIER);
        MOTS_CLES.put("REEL", TokenType.REEL);
        MOTS_CLES.put("TEXTE", TokenType.TEXTE);
        MOTS_CLES.put("BOOLEEN", TokenType.BOOLEEN);

        MOTS_CLES.put("VRAI", TokenType.VRAI);
        MOTS_CLES.put("FAUX", TokenType.FAUX);

        MOTS_CLES.put("ECRIRE", TokenType.ECRIRE);
        MOTS_CLES.put("AFFICHER", TokenType.ECRIRE);
        MOTS_CLES.put("LIRE", TokenType.LIRE);

        MOTS_CLES.put("SI", TokenType.SI);
        MOTS_CLES.put("ALORS", TokenType.ALORS);
        MOTS_CLES.put("SINON", TokenType.SINON);
        MOTS_CLES.put("FINSI", TokenType.FINSI);

        MOTS_CLES.put("TANTQUE", TokenType.TANTQUE);
        MOTS_CLES.put("FAIRE", TokenType.FAIRE);
        MOTS_CLES.put("FINTANTQUE", TokenType.FINTANTQUE);
        MOTS_CLES.put("POUR", TokenType.POUR);
        MOTS_CLES.put("DE", TokenType.DE);
        MOTS_CLES.put("A", TokenType.A);
        MOTS_CLES.put("FINPOUR", TokenType.FINPOUR);

        MOTS_CLES.put("CAS", TokenType.CAS);
        MOTS_CLES.put("DEFAUT", TokenType.DEFAUT);
        MOTS_CLES.put("FINCAS", TokenType.FINCAS);

        MOTS_CLES.put("ET", TokenType.ET);
        MOTS_CLES.put("OU", TokenType.OU);
        MOTS_CLES.put("NON", TokenType.NON);
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

            if (c == ' ' || c == '\t') {
                avancer();
            }
            else if (c == '\n') {
                ajouterToken(TokenType.NOUVELLE_LIGNE, "\\n");
                avancer();
                ligne++;
                colonne = 1;
            }
            else if (c == '\r') {
                avancer();
            }
            else if (c == '/' && regarderSuivant() == '/') {
                ignorerCommentaire();
            }
            else if (c == '<' && regarderSuivant() == '-') {
                int col = colonne;
                avancer();
                avancer();
                tokens.add(new Token(TokenType.AFFECTATION, "<-", ligne, col));
            }
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
            else if (c == '%') {
                ajouterToken(TokenType.MODULO, "%");
                avancer();
            }
            else if (c == '(') {
                ajouterToken(TokenType.PARENTHESE_G, "(");
                avancer();
            }
            else if (c == ')') {
                ajouterToken(TokenType.PARENTHESE_D, ")");
                avancer();
            }
            else if (c == '[') {
                ajouterToken(TokenType.CROCHET_G, "[");
                avancer();
            }
            else if (c == ']') {
                ajouterToken(TokenType.CROCHET_D, "]");
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
            else if (c == '"') {
                lireChaine();
            }
            else if (Character.isDigit(c)) {
                lireNombre();
            }
            else if (Character.isLetter(c) || c == '_') {
                lireIdentifiant();
            }
            else {
                throw new RuntimeException("Caractère non reconnu '" + c + "' à la ligne "
                        + ligne + ", colonne " + colonne);
            }
        }

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
        avancer();

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

        avancer();
        tokens.add(new Token(TokenType.CHAINE, sb.toString(), ligne, colonneDebut));
    }

    /**
     * Lit un nombre entier ou réel (floating-point).
     * Détecte la présence d'un point décimal pour distinguer NOMBRE et NOMBRE_REEL.
     */
    private void lireNombre() {
        int colonneDebut = colonne;
        StringBuilder sb = new StringBuilder();

        while (!estFin() && Character.isDigit(caractereActuel())) {
            sb.append(caractereActuel());
            avancer();
        }

        if (!estFin() && caractereActuel() == '.' && regarderSuivant() != '\0' && Character.isDigit(regarderSuivant())) {
            sb.append(caractereActuel());
            avancer();

            while (!estFin() && Character.isDigit(caractereActuel())) {
                sb.append(caractereActuel());
                avancer();
            }

            tokens.add(new Token(TokenType.NOMBRE_REEL, sb.toString(), ligne, colonneDebut));
        } else {
            tokens.add(new Token(TokenType.NOMBRE, sb.toString(), ligne, colonneDebut));
        }
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

        TokenType type = MOTS_CLES.getOrDefault(motMajuscule, TokenType.IDENTIFIANT);

        String valeur = (type == TokenType.IDENTIFIANT) ? mot : motMajuscule;

        tokens.add(new Token(type, valeur, ligne, colonneDebut));
    }
}
