package fr.pds.floralis.commons.bean.entity;

import java.sql.Date;

import fr.pds.floralis.commons.bean.entity.type.TypePassage;

/**
 * Passage 
 * The entity made to map the Passage object and map it to JSON with the toJSON
 * 
 * @author alveslaura
 *
 */

public class Passage {

	public Passage(int id, int building, TypePassage type, Date date) {
		super();
		this.id = id;
		this.building = building;
		this.type = type;
		this.date = date;
	}

	private int id;
	private int building;
	private TypePassage type;
	private Date date;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBuilding() {
		return building;
	}

	public void setBuilding(int building) {
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
