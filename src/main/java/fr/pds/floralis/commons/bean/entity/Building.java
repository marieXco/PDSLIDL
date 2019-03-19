package fr.pds.floralis.commons.bean.entity;

import fr.pds.floralis.commons.bean.entity.type.TypeBuilding;

public class Building {

	private int id = 0;
	private TypeBuilding name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TypeBuilding getName() {
		return name;
	}

	public void setName(TypeBuilding name) {
		this.name = name;
	}

	public Building(int id, TypeBuilding name) {
		super();
		this.id = id;
		this.name = name;
	}

}
