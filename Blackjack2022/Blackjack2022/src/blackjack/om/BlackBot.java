package blackjack.om;

/**
 * Robot de jeu de Blackjack
 * 
 * Déroulement d'une partie :
 *  - en état "MISE"
 *    -- chaque "joueur" peut déposer sa mise
 *    -- un joueur sans mise ne participera pas à la prochaine partie
 *    -- il faut au moins une mise pour pouvoir lancer la partie
 *    -- la méthode "distribuer()" termine la phase "MISE" et passe en état "JEU"
 *  - en état "JEU"
 *    -- chaque joueur peut "tirer(j)" ou "terminer(j)" tant qu'il n'a pas perdu ou déjà terminé
 *    -- tirer(j) ajoute une carte dans la main d'un joueur (et peut éventuellement le faire perdre)
 *    -- terminer(j) indique qu'il ne prendra plus de carte
 *    -- quand tous les joueurs ont perdu ou terminé :
 *         --- le croupier tire ses cartes et calcule les gains de chacun
 *         --- les gains sont placés dans miseJoueurs[j] (à la place des mises)
 *         --- le jeu passe alors en état "GAIN"
 * - en état "GAIN"
 *    -- on peut encore consulter les mains et les gains
 *    -- la seule action disponible est "relancerPartie()"
 *    -- relancerPartie() va vider les mains et les gains et basculer dans l'état "MISE"
 * 
 * 
 * Attributs utilisables sur un objet BlackBot :
 * 
 * sabot       : le sabot des cartes (mais dans une partie normale on n'a pas à y toucher)
 * etat        : l'état de la partie MISE, JEU ou GAIN
 * mainBanque  : la main du croupier
 * mainJoueurs : tableau des mains des joueurs (même ceux qui ne participent pas à un tour)
 * miseJoueurs : la mise initiale (en etat MISE et JEU)
 * gainJoueurs : le gain (en etat GAIN)
 * finJoueurs  : indique si un joueur peut encore tirer ou non (false si il a terminé, perdu ou n'a pas joué)
 */
public class BlackBot {

	private Sabot           sabot;
	private EtatBlackBot    etat;
	private MainBlackjack   mainBanque;
	private MainBlackjack[] mainJoueurs;
	private int[]           miseJoueurs;
	private int[]           gainJoueurs;
	private boolean[]       finJoueurs;

	/**
	 * Construction d'une table de blackJack pour nbJoueurs participants.
	 */
	public BlackBot( int nbJoueurs ) {
		this.sabot = new Sabot(6);
		this.etat  = EtatBlackBot.MISE;
		this.mainBanque  = new MainBlackjack();
		this.mainJoueurs = new MainBlackjack[nbJoueurs];
		for (int i = 0; i < mainJoueurs.length; i++) {
			mainJoueurs[i] = new MainBlackjack();
		}
		this.miseJoueurs = new int[nbJoueurs];
		this.gainJoueurs = new int[nbJoueurs];
		this.finJoueurs  = new boolean[nbJoueurs];
	}
	/**
	 * Relancer une partie
	 * Utilisable uniquement en état GAIN
	 * 
	 */
	public void relancerPartie() {
		if (this.etat==EtatBlackBot.GAIN) {
			this.etat = EtatBlackBot.MISE;
			this.mainBanque.viderMain();
			for (int i = 0; i < mainJoueurs.length; i++) {	
				this.mainJoueurs[i].viderMain();
				this.miseJoueurs[i] = 0;
				this.gainJoueurs[i] = 0;
				this.finJoueurs[i]  = false;
			}
		} else {
			throw new BlackjackException("[BlackBot] : Impossible d'encaisser et de relancer une partie en dehors de l'état GAIN");
		}
	}
	/**
	 * Enregistrer la mise d'un joueur
	 * Utilisable uniquement en état MISE
	 * 
	 * @param   joueur   numéro du joueur  [0..nbJoueur-1]
	 * @param   montant  mise totale pour ce joueur
	 */
	public void miser(int joueur, int montant) {
		if ( this.etat==EtatBlackBot.MISE ) {
			this.miseJoueurs[joueur] = montant;
		} else {
			throw new BlackjackException("[BlackBot] : Mise impossible en dehors de l'état MISE");
		}
	}
	/**
	 * Tirer une nouvelle carte pour un joueur
	 * Utilisable uniquement en état MISE si le joueur n'a pas déjà "perdu" ou "terminé"
	 * 
	 * @param   joueur   numéro du joueur [0..nbJoueur-1]
	 */
	public void tirer(int joueur) {
		if (this.etat==EtatBlackBot.JEU) {
			if (this.finJoueurs[joueur]==false) {
				this.mainJoueurs[joueur].prendreCarte( this.sabot.tirage() );
				if (this.mainJoueurs[joueur].isPerdante()) {
					// si un joueur perd en tirant on évalue la fin de partie
					this.finJoueurs[joueur] = true;
					this.checkFinPartie();
				}
			} else {
				throw new BlackjackException("[BlackBot] : Tirage impossible quand le joueur a terminé de jouer");
			}
		} else {
			throw new BlackjackException("[BlackBot] : Tirage impossible en dehors de l'état JEU");
		}
	}
	/**
	 * Terminer le tour d'un joueur
	 * Utilisable uniquement en état MISE si le joueur n'a pas déjà "perdu" ou "terminé"
	 * 
	 * @param   joueur   numéro du joueur [0..nbJoueur-1]
	 */
	public void terminer(int joueur) {
		if ( this.etat==EtatBlackBot.JEU ) {
			if (this.finJoueurs[joueur]==false) {
				// quand un joueur termine de jouer on évalue la fin de partie
				this.finJoueurs[joueur] = true;
				this.checkFinPartie();
			} else {
				throw new BlackjackException("[BlackBot] : Impossible de terminer sa main quand elle est déjà terminée");
			}
		} else {
			throw new BlackjackException("[BlackBot] : Impossible de terminer sa main en dehors de l'état JEU");
		}
	}
	/**
	 * Lancer une nouvelle partie et distribuer les premières cartes
	 * Utilisable uniquement en etat MISE si au moins un joueur a misé
	 * 
	 */
	public void distribuer() {
		// Il faut être en état MISE
		if (this.etat!=EtatBlackBot.MISE) {
			throw new BlackjackException("[BlackBot] : Distribution impossible en dehors de l'état MISE");
		}
		// On distribue :
		//  - Une carte à chaque joueur ayant misé (Exception si aucune mise)
		//  - Puis une carte au croupier
		//  - Puis une seconde carte à chaque joueur
		this.mainBanque.viderMain();
		int nbMises = 0;
		for (int i = 0; i < miseJoueurs.length; i++) {
			if (miseJoueurs[i]>0) {
				nbMises++;
				this.finJoueurs[i] = false;
				this.mainJoueurs[i].viderMain();
				this.mainJoueurs[i].prendreCarte( this.sabot.tirage() );
			} else {
				this.finJoueurs[i] = true;
			}
		}
		if (nbMises==0) {
			throw new BlackjackException("[BlackBot] : Distribution impossible si aucun joueur n'a misé");
		}
		this.mainBanque.prendreCarte( this.sabot.tirage() );
		for (int i = 0; i < miseJoueurs.length; i++) {
			if (miseJoueurs[i]>0) {
				this.mainJoueurs[i].prendreCarte( this.sabot.tirage() );
			}
		}
		// Le bot passe alors en état  JEU
		this.etat = EtatBlackBot.JEU;
	}
	/**
	 * Méthode interne (lancée automatiquement après chaque tirage ou demande de fin de tour)
	 * Elle vérifie si il reste des joueurs "actifs" (ni perdu ni terminé)
	 * Si il n'y a plus de joueurs actifs :
	 *   -- le croupier tire ses cartes
	 *   -- il calcul les gains (en prend les mises perdues)
	 *   -- le jeu bascule en etat "GAIN"
	 */
	private void checkFinPartie() {
		// la fin de partie est lancée ssi :
		// tous les joueurs ayant une mise ont perdu ou sont en mode "fin"
		int nbJoueursActifs = 0;
		for (int i = 0; i < miseJoueurs.length; i++) {
			if (miseJoueurs[i]>0) {
				if ( ! this.finJoueurs[i] ) {
					if (this.mainJoueurs[i].isPerdante()) {
						this.finJoueurs[i] = true;
					} else {
						nbJoueursActifs++;
					}
				}
			}
		}
		if (nbJoueursActifs==0) {
			// fin de partie : le croupier fait son tirage et calcule les gains.
			this.etat = EtatBlackBot.GAIN;
			// on tire à 16 passe à 17
			while (this.mainBanque.getScore()<=16) {
				this.mainBanque.prendreCarte(this.sabot.tirage());
			}            
			// on calcule les gains... 
			for (int i = 0; i < miseJoueurs.length; i++) {
				if (miseJoueurs[i]>0) {
					if (this.mainJoueurs[i].isPerdante()) {
						// main perdante : on perd la mise
						this.gainJoueurs[i] = 0;
					} else if (this.mainJoueurs[i].isBlackJack()) {
						if ( ! this.mainBanque.isBlackJack() ) {
							// gain par BlackJack : payement 3 pour 2
							this.gainJoueurs[i] = (int)(this.miseJoueurs[i] * 2.5);
						} else {
							// égalité de blackJack :-(  : pas de gain / pas de perte
							this.gainJoueurs[i] = this.miseJoueurs[i];
						}
					} else if (this.mainBanque.isPerdante()) {
						// main croupier perdante : on double la mise
						this.gainJoueurs[i] = this.miseJoueurs[i] * 2;
					} else if ( this.mainJoueurs[i].getScore() > this.mainBanque.getScore() ) {
						// main supérieure : on double la mise
						this.gainJoueurs[i] = this.miseJoueurs[i] * 2;
					} else if ( this.mainJoueurs[i].getScore() < this.mainBanque.getScore() ) {
						// main inférieure : on perd la mise
						this.gainJoueurs[i] = 0;
					} else {
						// main égale : pas de gain / pas de perte
						this.gainJoueurs[i] = this.miseJoueurs[i];
					}
					this.miseJoueurs[i] = 0;
				}
			}
		}
	}

	// ACCESSEURS données blackbot
	public Sabot getSabot() {
		return sabot;
	}
	public EtatBlackBot getEtat() {
		return etat;
	}
	public MainBlackjack getMainBanque() {
		return mainBanque;
	}
	public MainBlackjack getMainJoueurs(int joueur) {
		return mainJoueurs[joueur];
	}
	public int getMiseJoueurs(int joueur) {
		return miseJoueurs[joueur];
	}
	public int getGainJoueurs(int joueur) {
		return gainJoueurs[joueur];
	}
	public boolean getFinJoueurs(int joueur) {
		return finJoueurs[joueur];
	}
}
