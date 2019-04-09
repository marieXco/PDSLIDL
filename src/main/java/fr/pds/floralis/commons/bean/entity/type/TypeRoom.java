package fr.pds.floralis.commons.bean.entity.type;

public enum TypeRoom {
	CORRIDOR("CORRIDOR"), CAFETERIA("CAFETERIA"), LIVING_ROOM("LIVING_ROOM"), FRONT_DESK("FRONT_DESK"), GARDEN("GARDEN");
	
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	TypeRoom(String name) {
		this.name = name;
	}
}


