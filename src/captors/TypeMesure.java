package captors;

public enum TypeMesure {
	WATER("M³","EAU",0,10),
	ELECTRICITY("kWh", "ELECTRICITE",10,500),
	PRESSURIZEDAIR("M³/h", "AIR COMPRIME",0,5),
	TEMPERATURE("°C", "TEMPERATURE",17,22);
	
	private float min;
	private float max;
	private String unity;
	private String name;
	
	TypeMesure(String unity, String name,float min, float max){
		this.unity = unity;
		this.name = name;
		this.min = min;
		this.max = max;
		
	}


	public String getUnity() {
		return unity;
	}
	
	public String toString() {
		return name;
	}
	
	public float getDefaultMin() {
		return this.min;
	}
	public float getDefaultMax() {
		return this.max;
	}
}
