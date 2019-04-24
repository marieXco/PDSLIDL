package fr.pds.floralis.commons.bean.entity;

import java.sql.Date;

import org.json.JSONObject;

import fr.pds.floralis.commons.bean.entity.type.TypeLevel;

/**
 * Alert 
 * The entity made to map the alert object and map it to JSON with the toJSON
 * 
 * @author alveslaura
 *
 */

public class Alert {

	private int id;
	private TypeLevel level;
	private int sensor;
	private Date start;
	private Date end;

	public Alert(int id, TypeLevel level, int sensor, Date start, Date end) {
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

	public TypeLevel getLevel() {
		return level;
	}

	public void setLevel(TypeLevel level) {
		this.level = level;
	}

	public JSONObject toJSON() {
		JSONObject alertToJson = new JSONObject();
		alertToJson.put("id", id);
		alertToJson.put("level", level);
		alertToJson.put("sensor", sensor);
		alertToJson.put("start", start);
		alertToJson.put("end", end);
		return alertToJson;
	}
}
