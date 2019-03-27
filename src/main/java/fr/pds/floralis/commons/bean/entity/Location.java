package fr.pds.floralis.commons.bean.entity;

import fr.pds.floralis.commons.bean.entity.type.TypeFloor;

public class Location {

	//TODO : créer une location avec les capteurs 
	private int id;
	private Sensor sensor[];
	private Room room;
	private TypeFloor floor;
	private Building building;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Sensor[] getSensor() {
		return sensor;
	}

	public void setSensor(Sensor[] sensor) {
		this.sensor = sensor;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public TypeFloor getFloor() {
		return floor;
	}

	public void setFloor(TypeFloor floor) {
		this.floor = floor;
	}

	public Building getBuilding() {
		return building;
	}

	public void setBuilding(Building building) {
		this.building = building;
	}

	public Location(int id, Sensor[] sensor, Room room, TypeFloor floor,
			Building building) {
		super();
		this.id = id;
		this.sensor = sensor;
		this.room = room;
		this.floor = floor;
		this.building = building;
	}

}
