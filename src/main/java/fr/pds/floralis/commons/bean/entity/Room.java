package fr.pds.floralis.commons.bean.entity;

import fr.pds.floralis.commons.bean.entity.type.TypeRoom;

public class Room {

	private int id;
	private TypeRoom name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TypeRoom getName() {
		return name;
	}

	public void setName(TypeRoom name) {
		this.name = name;
	}

	public Room(int id, TypeRoom name) {
		super();
		this.id = id;
		this.name = name;
	}

}
