package fr.pds.floralis.commons.bean.entity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import fr.pds.floralis.commons.bean.entity.type.TypeSensor;

/**
 * Sensor 
 * The entity made to map the Sensor object and map it to JSON with the toJSON
 * 
 * @author alveslaura
 *
 */

public class Sensor {

	private int id;
	private TypeSensor type;
	private int idLocation;
	private Boolean state;
	private List<Integer> alerts = new ArrayList<Integer>();
	private String brand;
	private String macAdress;
	private Date installation;
	private String caracteristics;
	private List<Integer> breakdowns = new ArrayList<Integer>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdLocation() {
		return idLocation;
	}

	public void setIdLocation(int idLocation) {
		this.idLocation = idLocation;
	}

	public TypeSensor getType() {
		return type;
	}

	public void setType(TypeSensor type) {
		this.type = type;
	}

	public Boolean getState() {
		return state;
	}

	public void setState(Boolean state) {
		this.state = state;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getMacAdress() {
		return macAdress;
	}

	public void setMacAdress(String macAdress) {
		this.macAdress = macAdress;
	}

	public Date getInstallation() {
		return installation;
	}

	public void setInstallation(Date installation) {
		this.installation = installation;
	}

	public String getCaracteristics() {
		return caracteristics;
	}

	public void setCaracteristics(String caracteristics) {
		this.caracteristics = caracteristics;
	}

	public List<Integer> getAlerts() {
		return alerts;
	}

	public void setAlerts(List<Integer> alerts) {
		this.alerts = alerts;
	}

	public List<Integer> getBreakdowns() {
		return breakdowns;
	}

	public void setBreakdowns(List<Integer> breakdowns) {
		this.breakdowns = breakdowns;
	}

	public Sensor(int id, int idLocation, TypeSensor type, Boolean state,
			List<Integer> alerts, String brand, String macAdress,
			Date installation, String caracteristics, List<Integer> breakdowns) {
		super();
		this.id = id;
		this.idLocation = idLocation;
		this.type = type;
		this.state = state;
		this.alerts = alerts;
		this.brand = brand;
		this.macAdress = macAdress;
		this.installation = installation;
		this.caracteristics = caracteristics;
		this.breakdowns = breakdowns;
	}

	public Sensor() {

	}

	@Override
	public String toString() {
		return "\n { \n\"id\" : "
				+ id
				+ ", \n\t \"idLocation\" : "
				+ idLocation
				+ ", \n\t \"type\" : "
				+ type
				+ ", \n\t \"state\" : "
				+ state
				+ ", \n\t \"alerts\" : "
				+ alerts
				+ ", \n\t \"brand\" : \""
				+ brand
				+ "\", \n\t \"macAdress\" : \""
				+ macAdress
				+ "\", \n\t \"installation\" : \""
				+ installation
				+ "\", \n\t \"caracteristics\" : \""
				+ caracteristics
				+ "\", \n\t \"breakdowns\" : "
				+ breakdowns
				+ "\n }";
	}
}
