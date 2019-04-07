package fr.pds.floralis.commons.bean.entity;

import java.util.Arrays;

public class Location {
	
	private int id;
	private int sensorId[];
	private Room room;
	private Floor floor;
	private Building building;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int[] getSensorId() {
		return sensorId;
	}

	public void setSensorId(int[] sensor) {
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

	public Location(int id, int[] sensor, Room room, Floor floor,
			Building building) {
		super();
		this.id = id;
		this.sensorId = sensor;
		this.room = room;
		this.floor = floor;
		this.building = building;
	}

	@Override
	public String toString() {
		return "{ \"id\" : " + id + ", \"sensorId\" : " + Arrays.toString(sensorId) + ", \"room\" :" + room + ", \"floor\" : " + floor
				+ ", \"building\" : "+ building + "}";
	}
}
