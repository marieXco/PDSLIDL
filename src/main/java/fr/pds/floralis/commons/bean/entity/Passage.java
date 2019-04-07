package fr.pds.floralis.commons.bean.entity;

import java.sql.Date;

import fr.pds.floralis.commons.bean.entity.type.TypePassage;

public class Passage {

	public Passage(int id, Building building, TypePassage type, Date date) {
		super();
		this.id = id;
		this.building = building;
		this.type = type;
		this.date = date;
	}

	private int id;
	private Building building;
	private TypePassage type;
	private Date date;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Building getBuilding() {
		return building;
	}

	public void setBuilding(Building building) {
		this.building = building;
	}

	public TypePassage getType() {
		return type;
	}

	public void setType(TypePassage type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
