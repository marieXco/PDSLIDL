package fr.pds.floralis.commons.bean.entity;

import org.json.JSONObject;

/**
 * Room 
 * The entity made to map the Room object and map it to JSON with the toJSON
 * 
 * @author alveslaura
 *
 */

public class Room {

	private int id;
	private String typeRoom;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTypeRoom() {
		return typeRoom;
	}

	public void setTypeRoom(String typeRoom) {
		this.typeRoom = typeRoom;
	}

	public Room(int id, String typeRoom) {
		super();
		this.id = id;
		this.typeRoom = typeRoom;
	}
	
	public Room() {
		
	}
	
	@Override
	public String toString() {
		return "Room [id=" + id + ", typeRoom=" + typeRoom + "]";
	}

	public JSONObject toJSON() {
		JSONObject roomToJson = new JSONObject();
		roomToJson.put("id", id);
		roomToJson.put("typeRoom", typeRoom);
		return roomToJson;
	}

}
