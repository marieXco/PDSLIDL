package fr.pds.floralis.commons.bean.entity;

/**
 * Building 
 * The entity made to map the Building object and map it to JSON with the toJSON
 * 
 * @author alveslaura
 *
 */

public class Building {

	private int id;
	private String typeBuilding;
	
	public Building(int id, String typeBuilding) {
		super();
		this.id = id;
		this.typeBuilding = typeBuilding;
	}
	
	public Building() {

	}

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
	
	//TODO : rename typeBuilding to name
	public String toString() {
		return "{ \"id\" : " + id + ", \"typeBuilding\" : \"" + typeBuilding + "\"}";
	}
}
