package org.example;

/**
 * Exception levée lors d'une erreur sémantique.
 * Contient un message descriptif et le numéro de ligne où l'erreur s'est produite.
 */
public class SemanticException extends Exception {
    private final int ligne;

    /**
     * Constructeur avec message et numéro de ligne.
     *
     * @param message Description de l'erreur
     * @param ligne Numéro de ligne où l'erreur s'est produite
     */
    public SemanticException(String message, int ligne) {
        super(message);
        this.ligne = ligne;
    }

    /**
     * Constructeur avec message seulement (ligne inconnue).
     *
     * @param message Description de l'erreur
     */
    public SemanticException(String message) {
        super(message);
        this.ligne = -1;
    }

    /**
     * Retourne le numéro de ligne de l'erreur.
     */
    public int getLigne() {
        return ligne;
    }

    /**
     * Retourne un message formaté avec le numéro de ligne.
     */
    @Override
    public String toString() {
        if (ligne > 0) {
            return "Erreur sémantique (ligne " + ligne + "): " + getMessage();
        }
        return "Erreur sémantique: " + getMessage();
    }
}
