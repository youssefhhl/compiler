package org.example;

/**
 * Représente un token (unité lexicale) du pseudo-code.
 * Un token contient son type, sa valeur et sa position dans le code source.
 */
public class Token {
    private final TokenType type;    // Le type du token
    private final String valeur;     // La valeur textuelle du token
    private final int ligne;         // Numéro de ligne (pour les messages d'erreur)
    private final int colonne;       // Numéro de colonne (pour les messages d'erreur)

    /**
     * Constructeur d'un token.
     * @param type Le type du token
     * @param valeur La valeur textuelle
     * @param ligne Le numéro de ligne
     * @param colonne Le numéro de colonne
     */
    public Token(TokenType type, String valeur, int ligne, int colonne) {
        this.type = type;
        this.valeur = valeur;
        this.ligne = ligne;
        this.colonne = colonne;
    }

    public TokenType getType() {
        return type;
    }

    public String getValeur() {
        return valeur;
    }

    public int getLigne() {
        return ligne;
    }

    public int getColonne() {
        return colonne;
    }

    @Override
    public String toString() {
        return String.format("Token[%s, '%s', ligne:%d, col:%d]",
                type, valeur, ligne, colonne);
    }
}
