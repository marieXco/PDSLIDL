package fr.pds.floralis.commons.bean.entity;

import java.sql.Date;
import java.sql.Time;

import org.json.JSONObject;

/**
 * Breakdown 
 * The entity made to map the breakdown object and map it to JSON with the toJSON
 * 
 * @author alveslaura
 *
 */

public class Breakdown {

	private int id;
	private int sensor;
	private Time start;
	private Time end;
	private Date date;

	public Breakdown() {
		
	}

	public Breakdown(int id, int sensor, Time start, Time end, Date date) {
		super();
		this.id = id;
		this.sensor = sensor;
		this.start = start;
		this.end = end;
		this.date = date;
	}


	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the sensor
	 */
	public int getSensor() {
		return sensor;
	}

	/**
	 * @param sensor the sensor to set
	 */
	public void setSensor(int sensor) {
		this.sensor = sensor;
	}

	/**
	 * @return the start
	 */
	public Time getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(Time start) {
		this.start = start;
	}

	/**
	 * @return the end
	 */
	public Time getEnd() {
		return end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd(Time end) {
		this.end = end;
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
		breakdownToJson.put("end", end);
		breakdownToJson.put("date", date);
		return breakdownToJson;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Breakdown [id=" + id + ", sensor=" + sensor + ", start=" + start + ", end=" + end + ", date=" + date
				+ "]";
	}	
}
