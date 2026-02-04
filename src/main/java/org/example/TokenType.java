package org.example;

/**
 * Énumération de tous les types de tokens reconnus par le lexer.
 * Chaque token représente un élément lexical du pseudo-code français.
 */
public enum TokenType {
    ALGORITHME,
    VARIABLES,
    DEBUT,
    FIN,
    FONCTION,
    PROCEDURE,
    RETOURNE,
    FINFONCTION,
    FINPROCEDURE,

    // Types de données
    ENTIER,
    REEL,
    TEXTE,
    BOOLEEN,

    // Entrées/Sorties
    ECRIRE,
    LIRE,

    // Structures conditionnelles
    SI,
    ALORS,
    SINON,
    FINSI,

    // Boucles
    TANTQUE,
    FAIRE,
    FINTANTQUE,
    POUR,
    DE,
    A,
    FINPOUR,

    // Switch/Cas
    CAS,
    DEFAUT,
    FINCAS,

    // Opérateurs
    AFFECTATION,
    PLUS,
    MOINS,
    MULTIPLIE,
    DIVISE,
    MODULO,
    SUPERIEUR,
    INFERIEUR,
    EGAL,
    DIFFERENT,
    SUP_EGAL,
    INF_EGAL,

    // Opérateurs logiques
    ET,
    OU,
    NON,

    // Ponctuation
    PARENTHESE_G,
    PARENTHESE_D,
    CROCHET_G,
    CROCHET_D,
    DEUX_POINTS,
    VIRGULE,

    // Littéraux et identifiants
    IDENTIFIANT,
    NOMBRE,
    NOMBRE_REEL,
    CHAINE,
    VRAI,
    FAUX,

    // Spéciaux
    NOUVELLE_LIGNE,
    EOF
}
