package bdd;

import java.sql.Timestamp;

public class Valeur {
	Float valeur;
	java.sql.Timestamp temps;
	public Valeur(Float valeur,Timestamp temps) {
		this.valeur = valeur;
		this.temps = temps;
	}
	public Timestamp getTemps() {
		return temps;
	}
	public Float getValeur() {
		return valeur;
	}

}
