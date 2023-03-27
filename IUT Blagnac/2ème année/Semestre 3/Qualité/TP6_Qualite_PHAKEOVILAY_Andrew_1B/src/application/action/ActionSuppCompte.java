package application.action;

import java.util.Locale;
import java.util.Scanner;

import banque.AgenceBancaire;
import banque.Compte;
import banque.exception.CompteException;

public class ActionSuppCompte implements Action {

	private String message;
	private String code;
	
	public ActionSuppCompte(String _msg, String _code) {
		this.message = _msg;
		this.code = _code;
	}
	
	@Override
	public String actionMessage() {
		// TODO Auto-generated method stub
		return this.message;
	}

	@Override
	public String actionCode() {
		// TODO Auto-generated method stub
		return this.code;
	}

	@Override
	public void execute(AgenceBancaire ag) throws Exception {
		// TODO Auto-generated method stub
		Scanner lect;
		lect = new Scanner ( System.in );
		lect.useLocale(Locale.US);
		System.out.print("Num compte -> ");
		String numero = lect.next();
		ag.removeCompte(numero);
	}
	
}
