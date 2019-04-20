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
	private int roomId;
	private int floorId;
	private int buildingId;

	
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the sensorId
	 */
	public List<Integer> getSensorId() {
		return sensorId;
	}

	/**
	 * @param sensorId the sensorId to set
	 */
	public void setSensorId(List<Integer> sensorId) {
		this.sensorId = sensorId;
	}

	/**
	 * @return the roomId
	 */
	public int getRoomId() {
		return roomId;
	}

	/**
	 * @param roomId the roomId to set
	 */
	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	/**
	 * @return the floorId
	 */
	public int getFloorId() {
		return floorId;
	}

	/**
	 * @param floorId the floorId to set
	 */
	public void setFloorId(int floorId) {
		this.floorId = floorId;
	}

	/**
	 * @return the buildingId
	 */
	public int getBuildingId() {
		return buildingId;
	}

	/**
	 * @param buildingId the buildingId to set
	 */
	public void setBuildingId(int buildingId) {
		this.buildingId = buildingId;
	}

	
	public Location() {
	}


	public Location(int id, List <Integer> sensor, int roomId, int floorId,
			int buildingId) {
		super();
		this.id = id;
		this.sensorId = sensor;
		this.roomId = roomId;
		this.floorId = floorId;
		this.buildingId = buildingId;
	}

	public String toString() {
		return "Location [id=" + id + ", sensorId=" + sensorId + ", room=" + roomId + ", floor=" + floorId + ", building="
				+ buildingId + "]";
	}

	public JSONObject toJSON() {
		JSONObject locationToJson = new JSONObject();
		locationToJson.put("id", id);
		locationToJson.put("sensorId", sensorId);
		locationToJson.put("roomId", roomId);
		locationToJson.put("floorId", floorId);
		locationToJson.put("buildingId", buildingId);
		return locationToJson;
	}
}
