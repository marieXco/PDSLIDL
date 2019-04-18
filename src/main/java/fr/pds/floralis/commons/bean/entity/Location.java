package fr.pds.floralis.commons.bean.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

/**
 * Location 
 * The entity made to map the Location object and map it to JSON with the toJSON
 * 
 * @author alveslaura
 *
 */

public class Location {
	
	private int id;
	private List<Integer> sensorId = new ArrayList<Integer>();
	private Room room;
	private Floor floor;
	private Building building;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Integer> getSensorId() {
		return sensorId;
	}

	public void setSensorId(List<Integer> sensor) {
		this.sensorId = sensor;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public Floor getFloor() {
		return floor;
	}

	public void setFloor(Floor floor) {
		this.floor = floor;
	}

	public Building getBuilding() {
		return building;
	}

	public void setBuilding(Building building) {
		this.building = building;
	}
	
	
	// TODO : TEST : ne fonctionne que si il y a un constructeur vide ? 
	public Location() {
	}


	public Location(int id, List <Integer> sensor, Room room, Floor floor,
			Building building) {
		super();
		this.id = id;
		this.sensorId = sensor;
		this.room = room;
		this.floor = floor;
		this.building = building;
	}

	public String toString() {
		return "Location [id=" + id + ", sensorId=" + sensorId + ", room=" + room + ", floor=" + floor + ", building="
				+ building + "]";
	}

	public JSONObject toJSON() {
		JSONObject locationToJson = new JSONObject();
		locationToJson.put("id", id);
		locationToJson.put("sensorId", sensorId);
		locationToJson.put("room", room);
		locationToJson.put("floor", floor);
		locationToJson.put("building", building);
		return locationToJson;
	}
}
