package org.example;

import java.util.HashMap;
import java.util.Map;

/**
 * Table des symboles pour l'analyse sémantique.
 * Stocke les variables déclarées avec leur type (ENTIER ou TEXTE).
 *
 * Cette classe permet de :
 * - Enregistrer une variable lors de sa déclaration
 * - Vérifier si une variable existe
 * - Récupérer le type d'une variable
 * - Détecter les déclarations en double
 */
public class SymbolTable {

    /**
     * Énumération des types de données supportés.
     */
    public enum TypeDonnee {
        ENTIER,  // Nombre entier
        REEL,    // Nombre réel (floating-point)
        TEXTE    // Chaîne de caractères
    }

    /**
     * Information sur un symbole (variable).
     */
    public static class SymboleInfo {
        private final String nom;           // Nom de la variable
        private final TypeDonnee type;      // Type de la variable
        private final int ligneDeclaration; // Ligne où la variable a été déclarée

        public SymboleInfo(String nom, TypeDonnee type, int ligneDeclaration) {
            this.nom = nom;
            this.type = type;
            this.ligneDeclaration = ligneDeclaration;
        }

        public String getNom() { return nom; }
        public TypeDonnee getType() { return type; }
        public int getLigneDeclaration() { return ligneDeclaration; }

        @Override
        public String toString() {
            return nom + " : " + type + " (ligne " + ligneDeclaration + ")";
        }
    }

    // Table de hachage pour stocker les symboles (nom -> info)
    private final Map<String, SymboleInfo> symboles;

    /**
     * Constructeur - initialise une table vide.
     */
    public SymbolTable() {
        this.symboles = new HashMap<>();
    }

    /**
     * Déclare une nouvelle variable dans la table.
     *
     * @param nom Nom de la variable
     * @param type Type de la variable (chaîne "ENTIER" ou "TEXTE")
     * @param ligne Numéro de ligne de la déclaration
     * @throws SemanticException si la variable est déjà déclarée
     */
    public void declarer(String nom, String type, int ligne) throws SemanticException {
        // Vérifier si la variable existe déjà (doublon)
        if (symboles.containsKey(nom)) {
            SymboleInfo existant = symboles.get(nom);
            throw new SemanticException(
                "Variable '" + nom + "' déjà déclarée à la ligne " + existant.getLigneDeclaration(),
                ligne
            );
        }

        // Convertir le type chaîne en TypeDonnee
        TypeDonnee typeDonnee = convertirType(type, ligne);

        // Ajouter la variable à la table
        symboles.put(nom, new SymboleInfo(nom, typeDonnee, ligne));
    }

    /**
     * Vérifie si une variable existe dans la table.
     *
     * @param nom Nom de la variable à vérifier
     * @return true si la variable existe, false sinon
     */
    public boolean existe(String nom) {
        return symboles.containsKey(nom);
    }

    /**
     * Récupère les informations d'une variable.
     *
     * @param nom Nom de la variable
     * @return L'information du symbole, ou null si non trouvé
     */
    public SymboleInfo getSymbole(String nom) {
        return symboles.get(nom);
    }

    /**
     * Récupère le type d'une variable.
     *
     * @param nom Nom de la variable
     * @return Le type de la variable
     * @throws SemanticException si la variable n'existe pas
     */
    public TypeDonnee getType(String nom, int ligne) throws SemanticException {
        if (!existe(nom)) {
            throw new SemanticException(
                "Variable '" + nom + "' non déclarée. Déclarez-la dans la section VARIABLES.",
                ligne
            );
        }
        return symboles.get(nom).getType();
    }

    /**
     * Convertit une chaîne de type ("ENTIER", "REEL", "TEXTE") en TypeDonnee.
     *
     * @param type La chaîne représentant le type
     * @param ligne Numéro de ligne pour le message d'erreur
     * @return Le TypeDonnee correspondant
     * @throws SemanticException si le type est inconnu
     */
    private TypeDonnee convertirType(String type, int ligne) throws SemanticException {
        return switch (type.toUpperCase()) {
            case "ENTIER" -> TypeDonnee.ENTIER;
            case "REEL" -> TypeDonnee.REEL;
            case "TEXTE" -> TypeDonnee.TEXTE;
            default -> throw new SemanticException(
                "Type inconnu '" + type + "'. Types valides: ENTIER, REEL, TEXTE.",
                ligne
            );
        };
    }

    /**
     * Retourne le nombre de variables déclarées.
     */
    public int taille() {
        return symboles.size();
    }

    /**
     * Affiche toutes les variables déclarées (pour le débogage).
     */
    public void afficher() {
        System.out.println("=== Table des symboles ===");
        if (symboles.isEmpty()) {
            System.out.println("   (vide)");
        } else {
            for (SymboleInfo info : symboles.values()) {
                System.out.println("   " + info);
            }
        }
        System.out.println("==========================");
    }

    /**
     * Récupère tous les symboles (pour l'itération).
     */
    public Map<String, SymboleInfo> getTousLesSymboles() {
        return new HashMap<>(symboles);
    }
}
