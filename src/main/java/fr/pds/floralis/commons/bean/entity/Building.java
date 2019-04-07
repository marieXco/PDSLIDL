package fr.pds.floralis.commons.bean.entity;

import fr.pds.floralis.commons.bean.entity.type.TypeBuilding;

public class Building {

	private int id;
	private String typeBuilding;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTypeBuilding() {
		return typeBuilding;
	}

	public void setTypeBuilding(String typeBuilding) {
		this.typeBuilding = typeBuilding;
	}

	public Building(int id, String typeBuilding) {
		super();
		this.id = id;
		this.typeBuilding = typeBuilding;
	}

	public Building() {

	}
	
	//TODO : renomer typeBuilding en name

	@Override
	public String toString() {
		return "{ \"id\" : " + id + ", \"typeBuilding\" : \"" + typeBuilding + "\"}";
	}



}
