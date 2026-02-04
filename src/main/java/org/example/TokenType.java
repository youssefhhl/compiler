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
    FONCTION,       // Définition de fonction
    PROCEDURE,      // Définition de procédure (sans retour)
    RETOURNE,       // Return statement
    FINFONCTION,    // Fin de fonction
    FINPROCEDURE,   // Fin de procédure

    // Types de données
    ENTIER,         // Type entier
    REEL,           // Type réel (floating-point)
    TEXTE,          // Type chaîne de caractères
    BOOLEEN,        // Type booléen

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
    POUR,           // Boucle for
    DE,             // De (from) in for loop
    A,              // À (to) in for loop
    FINPOUR,        // Fin de la boucle for

    // Switch/Cas
    CAS,            // Switch statement
    DEFAUT,         // Default case
    FINCAS,         // End of switch

    // Opérateurs
    AFFECTATION,    // <- (affectation)
    PLUS,           // +
    MOINS,          // -
    MULTIPLIE,      // *
    DIVISE,         // /
    MODULO,         // % (remainder)
    SUPERIEUR,      // >
    INFERIEUR,      // <
    EGAL,           // ==
    DIFFERENT,      // !=
    SUP_EGAL,       // >=
    INF_EGAL,       // <=

    // Opérateurs logiques
    ET,             // AND (logical AND)
    OU,             // OR (logical OR)
    NON,            // NOT (logical NOT)

    // Ponctuation
    PARENTHESE_G,   // (
    PARENTHESE_D,   // )
    CROCHET_G,      // [
    CROCHET_D,      // ]
    DEUX_POINTS,    // :
    VIRGULE,        // ,

    // Littéraux et identifiants
    IDENTIFIANT,    // Nom de variable
    NOMBRE,         // Nombre entier
    NOMBRE_REEL,    // Nombre réel (floating-point)
    CHAINE,         // Chaîne de caractères entre guillemets
    VRAI,           // Booléen vrai
    FAUX,           // Booléen faux

    // Spéciaux
    NOUVELLE_LIGNE, // Fin de ligne
    EOF             // Fin du fichier
}
