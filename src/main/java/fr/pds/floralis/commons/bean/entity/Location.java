package fr.pds.floralis.commons.bean.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Location {
	
	private int id;
	private List<Integer> sensorId = new ArrayList<Integer>();
	private int room;
	private int floor;
	private int building;

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

	public int getRoom() {
		return room;
	}

	public void setRoom(int room) {
		this.room = room;
	}

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public int getBuilding() {
		return building;
	}

	public void setBuilding(int building) {
		this.building = building;
	}
	
	// TODO : TEST : ne fonctionne que si il y a un constructeur vide ? 
	public Location() {
	}

	public Location(int id, List<Integer> sensor, int room, int floor,
			int building) {
		super();
		this.id = id;
		this.sensorId = sensor;
		this.room = room;
		this.floor = floor;
		this.building = building;
	}

	@Override
	public String toString() {
		return "{ \"id\" : " + id + ", \"sensorId\" : " + sensorId.stream().map(Object::toString).collect(Collectors.joining(", ")) + ", \"room\" :" + room + ", \"floor\" : " + floor
				+ ", \"building\" : "+ building + "}";
	}
}
