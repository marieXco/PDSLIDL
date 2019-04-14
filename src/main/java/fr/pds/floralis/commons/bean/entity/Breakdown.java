package fr.pds.floralis.commons.bean.entity;

import java.sql.Date;

import fr.pds.floralis.commons.bean.entity.type.TypeBreakdown;

/**
 * Breakdown 
 * The entity made to map the breakdown object and map it to JSON with the toJSON
 * 
 * @author alveslaura
 *
 */

public class Breakdown {

	private int id;
	private TypeBreakdown type;
	private int sensor;
	private Date start;
	private Date end;

	public Breakdown(int id, TypeBreakdown type, int sensor, Date start,
			Date end) {
		super();
		this.id = id;
		this.type = type;
		this.end = end;
		this.sensor = sensor;
		this.start = start;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TypeBreakdown getType() {
		return type;
	}

	public void setType(TypeBreakdown type) {
		this.type = type;
	}

	public int getSensor() {
		return sensor;
	}

	public void setSensor(int sensor) {
		this.sensor = sensor;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	@Override
	public String toString() {
		return "Breakdown [id=" + id + ", type=" + type + ", sensor=" + sensor + ", start=" + start + ", end=" + end
				+ "]";
	}
	

}
