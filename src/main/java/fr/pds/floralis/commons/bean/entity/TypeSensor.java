package fr.pds.floralis.commons.bean.entity;

import org.json.JSONObject;

/**
 * TypeSensor 
 * The entity made to map the TypeSensor object and map it to JSON with the toJSON
 * 
 * @author alveslaura
 *
 */

public class TypeSensor {
	
	private int id;
	private String type;
	private int nightSensitivity;
	private int daySensitivity;
	
	public TypeSensor(){
		
	}
	
	public TypeSensor(int id, String typeSensor, int night, int day) {
		this.id = id; 
		this.type= typeSensor; 
		this.daySensitivity = day;
		this.nightSensitivity = night;
	}
	
	public TypeSensor() {

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
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the nightSensitivity
	 */
	public int getNightSensitivity() {
		return nightSensitivity;
	}
	/**
	 * @param nightSensitivity the nightSensitivity to set
	 */
	public void setNightSensitivity(int nightSensitivity) {
		this.nightSensitivity = nightSensitivity;
	}
	/**
	 * @return the daySensitivity
	 */
	public int getDaySensitivity() {
		return daySensitivity;
	}
	/**
	 * @param daySensitivity the daySensitivity to set
	 */
	public void setDaySensitivity(int daySensitivity) {
		this.daySensitivity = daySensitivity;
	}
	
	public JSONObject toJSON() {
		JSONObject typeSensorToJson = new JSONObject();
		typeSensorToJson.put("id", id);
		typeSensorToJson.put("type", type);
		typeSensorToJson.put("nightSensitivity", nightSensitivity);
		typeSensorToJson.put("daySensitivity", daySensitivity);
		
		return typeSensorToJson;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TypeSensor [id=" + id + ", type=" + type + ", nightSensitivity=" + nightSensitivity
				+ ", daySensitivity=" + daySensitivity + "]";
	}
	

}
