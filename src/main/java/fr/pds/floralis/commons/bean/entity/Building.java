package fr.pds.floralis.commons.bean.entity;

import org.json.JSONObject;

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
	public JSONObject toJSON() {
		JSONObject buildingToJson = new JSONObject();
		buildingToJson.put("id", id);
		buildingToJson.put("typeBuilding", typeBuilding);
		
		return buildingToJson;
	}

	@Override
	public String toString() {
		return "Building [id=" + id + ", typeBuilding=" + typeBuilding + "]";
	}
}
