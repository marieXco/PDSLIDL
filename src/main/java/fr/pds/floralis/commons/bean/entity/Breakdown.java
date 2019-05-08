package fr.pds.floralis.commons.bean.entity;

import java.sql.Date;
import java.sql.Time;

import org.json.JSONObject;

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
	private Time start;
	private Date date;

	public Breakdown(int id, TypeBreakdown type, int sensor, Time start,
			Date date) {
		super();
		this.id = id;
		this.type = type;
		this.sensor = sensor;
		this.start = start;
		this.date = date;
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

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	public JSONObject toJSON() {
		JSONObject breakdownToJson = new JSONObject();
		breakdownToJson.put("id", id);
		breakdownToJson.put("sensor", sensor);
		breakdownToJson.put("start", start);
		breakdownToJson.put("date", date);
		return breakdownToJson;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Breakdown [id=" + id + ", sensor=" + sensor + ", start=" + start + ", date=" + date + "]";
	}

	
}
