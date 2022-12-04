package blackjack.om;

public class Carte {

	// -----------------------------------------------------------------------
	// Constantes de la classe Carte
	/**
	 * Représentation textuelle de la couleur d'une carte
	 */
	public static final String[] COULEURCARTE = {"Pique", "Coeur", "Trefle", "Carreau"};
	
	/**
	 * Représentation textuelle du nom d'une carte
	 */
	public static final String[] NOMCARTE     = { "As", "Deux", "Trois", "Quatre", "Cinq", "Six", "Sept", "Huit", "Neuf", "Dix", "Valet", "Dame", "Roi" };

	/**
	 * Valeur d'une carte dans une main de BlackJack.
	 * !!! un AS peut valoir 1 ou 11 mais ca valeur est de 0 dans ce tableau
	 */
	public static final int[] VALCARTE        = { 0, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10 }; 

	
	// -----------------------------------------------------------------------
	// Attributs des objets Cartes
	
	private int couleur; // 0..4  => 0=Pique, 1=Coeur, 2=Trefle, 3=Carreau
	private int hauteur; // 0..12 => 0=As, ... 9=10, 10=Valet, 11=Dame, 12=Roi

	/**
	 * Constructeur
	 * 
	 * Paramètres :
	 * @param : couleur de la carte (de 0 à 3)
	 * @param : hauteur de la carte  (de 0 à 12)
	 * 
	 * Attributs utilisable sur un objet Carte :
	 *  couleur : la couleur de la carte encodée dans un nombre [0...3]
	 *  hauteur : hauteur de la carte [as..roi] encodée dans une nombre [0..12]
	 */
    public Carte(int couleur, int hauteur) {
		if (couleur<0||couleur>3||hauteur<0||hauteur>12) {
			throw new BlackjackException("Creation de carte impossible : ["+couleur+"/"+hauteur+"]");
		}
		this.couleur = couleur;
		this.hauteur = hauteur;
	}
	/**
	 * Nom de la carte (d'après le tableau "NomCarte")
	 * @returns le nom de la carte (As, Deux, ..., Roi)
	 */
	public String getNom() {
		return NOMCARTE[this.hauteur];
	}
	/**
	 * Valeur de la carte de 2 à 10 ou "0" si c'est un As
	 * @returns valeur de la carte [0,2,3..10]
	 */
	public int getValeur() {
		return VALCARTE[this.hauteur];
	}
	/**
	 * Nom de la couleur de la carte (d'après le tableau "Couleur")
	 * @returns "Pique", "Coeur", "Carreau" ou "Trefle"
	 */
	public String getNomCouleur() {
		return COULEURCARTE[this.couleur];
	}
	/**
	 * Nom complet de la carte (ex: "Dame de Pique")
	 * @returns le nom complet en français
	 */
	public String getNomComptet() {
		return NOMCARTE[this.hauteur]+" de "+COULEURCARTE[this.couleur];
	}
	/**
	 * Couleur de la carte au format numérique
	 * 0=Pique, 1=Coeur, 2=Trefle, 3=Carreau
	 * @return 0, 1, 2 ou 3
	 */
	public int getCouleur() {
		return couleur;
	}
	/**
	 * Hauteur de la carte au format numérique
	 * hauteur de la carte [as..roi] encodée dans une nombre [0..12]
	 * @return la hauteur (de 0 à 12)
	 */
	public int getHauteur() {
		return hauteur;
	}

}
