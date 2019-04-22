package fr.pds.floralis.commons.bean.entity;

import org.json.JSONObject;

/**
 * Floor 
 * The entity made to map the Floor object and map it to JSON with the toJSON
 * 
 * @author alveslaura
 *
 */

public class Floor {
	
	private int id;
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Floor() {

	}
	
	public Floor(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public JSONObject toJSON() {
		JSONObject floorToJson = new JSONObject();
		floorToJson.put("id", id);
		floorToJson.put("name", name);
		
		return floorToJson;
	}
	
	public String jsonToString(Floor floorToJson) {
		return floorToJson.toJSON().toString();
	}

	@Override
	public String toString() {
		return "Floor [id=" + id + ", name=" + name + "]";
	}
		
}
