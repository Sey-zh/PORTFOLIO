package comportement.deplacement.impl;

import comportements.deplacement.ComportementDeplacement;

public class ComportementCaleche implements ComportementDeplacement{
	public ComportementCaleche() {}

	@Override
	public void deplacement() {
		System.out.println("D�placement en cal�che");
	}
}

