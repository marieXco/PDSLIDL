package fr.pds.floralis.commons.bean.entity;

import java.sql.Date;

import fr.pds.floralis.commons.bean.entity.type.TypeLevel;

public class Alert {

	private int id;
	private TypeLevel level;
	private Sensor sensor;
	private Date start;
	private Date end;

	public Alert(int id, TypeLevel level, Sensor sensor, Date start, Date end) {
		super();
		this.id = id;
		this.level = level;
		this.setEnd(end);
		this.setStart(start);
		this.setSensor(sensor);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
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

	public TypeLevel getLevel() {
		return level;
	}

	public void setLevel(TypeLevel level) {
		this.level = level;
	}

}
