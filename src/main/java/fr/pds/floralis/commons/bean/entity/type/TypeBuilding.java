package fr.pds.floralis.commons.bean.entity.type;

public enum TypeBuilding {

	A("A"), B("B"), C("C");
	
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	TypeBuilding(String name) {
		this.name = name;
	}
}
