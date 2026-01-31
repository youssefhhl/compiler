package org.example;

/**
 * Énumération de tous les types de tokens reconnus par le lexer.
 * Chaque token représente un élément lexical du pseudo-code français.
 */
public enum TokenType {
    // Mots-clés de structure
    ALGORITHME,     // Début du programme
    VARIABLES,      // Section de déclaration des variables
    DEBUT,          // Début du bloc principal
    FIN,            // Fin du programme

    // Types de données
    ENTIER,         // Type entier
    TEXTE,          // Type chaîne de caractères

    // Entrées/Sorties
    ECRIRE,         // Afficher à l'écran (alias: AFFICHER)
    LIRE,           // Lire une entrée utilisateur

    // Structures conditionnelles
    SI,             // Condition if
    ALORS,          // Début du bloc then
    SINON,          // Bloc else
    FINSI,          // Fin de la condition

    // Boucles
    TANTQUE,        // Boucle while
    FAIRE,          // Début du bloc de boucle
    FINTANTQUE,     // Fin de la boucle

    // Opérateurs
    AFFECTATION,    // <- (affectation)
    PLUS,           // +
    MOINS,          // -
    MULTIPLIE,      // *
    DIVISE,         // /
    SUPERIEUR,      // >
    INFERIEUR,      // <
    EGAL,           // ==
    DIFFERENT,      // !=
    SUP_EGAL,       // >=
    INF_EGAL,       // <=

    // Ponctuation
    PARENTHESE_G,   // (
    PARENTHESE_D,   // )
    DEUX_POINTS,    // :
    VIRGULE,        // ,

    // Littéraux et identifiants
    IDENTIFIANT,    // Nom de variable
    NOMBRE,         // Nombre entier
    CHAINE,         // Chaîne de caractères entre guillemets

    // Spéciaux
    NOUVELLE_LIGNE, // Fin de ligne
    EOF             // Fin du fichier
}
