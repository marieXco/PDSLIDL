package fr.pds.floralis.commons.bean.entity;

/**
 * Sensor 
 * The entity made to map the Sensor object and map it to JSON with the toJSON
 * 
 * @author alveslaura
 *
 */

public class Sensors {

	private int id;
	private String typeSensor;
	private int idSensor;
	private int min;
	private int max;
	private String sensitivityNight;
	private String sensitivityDay;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTypeSensor() {
		return typeSensor;
	}
	public void setTypeSensor(String typeSensor) {
		this.typeSensor = typeSensor;
	}
	public int getIdSensor() {
		return idSensor;
	}
	public void setIdSensor(int idSensor) {
		this.idSensor = idSensor;
	}
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public String getSensitivityNight() {
		return sensitivityNight;
	}
	public void setSensitivityNight(String sensitivityNight) {
		this.sensitivityNight = sensitivityNight;
	}
	public String getSensitivityDay() {
		return sensitivityDay;
	}
	public void setSensitivityDay(String sensitivityDay) {
		this.sensitivityDay = sensitivityDay;
	}
	
	public String toString() {
		return "\n { \n\"id\" : "
				+ id
				+ ", \n\t \"idSensor\" : "
				+ idSensor
				+ ", \n\t \"typeSensor\" : \""
				+ typeSensor
				+ "\", \n\t \"sensitivityNight\" : \""
				+ sensitivityNight
				+ "\", \n\t \"sensitivityDay\" : \""
				+ sensitivityDay
				+ "\", \n\t \"min\" : "
				+ min
				+ ", \n\t \"max\" : "
				+ max
				+ "\n }";
	}

}
