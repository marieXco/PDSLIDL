package fr.pds.floralis.commons.bean.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Location {
	
	private int id;
	private List <Integer> sensorId = new ArrayList<Integer>();
	private Room room;
	private Floor floor;
	private Building building;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
	
	public List<Integer> getSensorId() {
		return sensorId;
	}

	public void setSensorId(List<Integer> sensorId) {
		this.sensorId = sensorId;
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

	

	@Override
	public String toString() {
		return "{ \"id\" : " + id + ", \"sensorId\" : " + sensorId + ", \"room\" :" + room + ", \"floor\" : " + floor
				+ ", \"building\" : "+ building + "}";
	}
}
