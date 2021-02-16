package captors;


public class Captor implements Comparable<Captor>{
	
	String nom;
	String batiment;
	String lieu;
	int etage;
	float min, max;
	TypeMesure type;
	
	public Captor(String nom, String batiment, String lieu, int etage, TypeMesure type, float min, float max) {
		this.nom = nom;
		this.batiment = batiment;
		this.lieu = lieu;
		this.etage = etage;
		this.type = type;
		this.min = min;
		this.max = max;
	}
	
	public void setMin(float min) {
		this.min = min;
	}
	
	public void setMax(float max) {
		this.max = max;
	}
	
	public void setRange(float min, float max) {
		this.min = min;
		this.max = max;
	}
	
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public void setBatiment(String batiment) {
		this.batiment = batiment;
	}
	
	public void setLieu(String lieu) {
		this.lieu = lieu;
	}
	
	public void setEtage(int etage) {
		this.etage = etage;
	}

	public TypeMesure getType() {
		return type;
	}
	public float getMin() {
		return this.min;
	}
	public float getMax() {
		return this.max;
	}

	public void setType(TypeMesure type) {
		this.type = type;
	}

	public String getNom() {
		return nom;
	}

	public String getBatiment() {
		return batiment;
	}

	public String getLieu() {
		return lieu;
	}

	public int getEtage() {
		return etage;
	}
	
	
	public String toString() {
		return nom;
	}
	
	@Override
	public int compareTo(Captor o) {
		if(this.batiment.compareTo(o.getBatiment()) == 0) {
			if(this.etage == o.getEtage())
				return this.nom.compareTo(o.getNom());
			return (this.etage - o.getEtage());
		}
		return (this.batiment.compareTo(o.getBatiment()));
	}


	
	
	
	

}
