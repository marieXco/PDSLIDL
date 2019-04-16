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
	private Boolean alert;
	private String brand;
	private String macAddress;
	private String ipAddress;
	private String port;
	private Boolean configure;
	private Date installation;
	private String min;
	private String max;
	private Boolean breakdown;

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

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAdress) {
		this.macAddress = macAdress;
	}

	public Date getInstallation() {
		return installation;
	}

	public void setInstallation(Date installation) {
		this.installation = installation;
	}

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
	}

	public Boolean getAlert() {
		return alert;
	}

	public void setAlert(Boolean alert) {
		this.alert = alert;
	}

	public Boolean getBreakdown() {
		return breakdown;
	}

	public void setBreakdown(Boolean breakdown) {
		this.breakdown = breakdown;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String iPAddress) {
		ipAddress = iPAddress;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public Boolean getConfigure() {
		return configure;
	}

	public void setConfigure(Boolean configure) {
		this.configure = configure;
	}

	public Sensor(int id, int idLocation, TypeSensor type, Boolean state,
			Boolean alert, String brand, String macAdress,
			Date installation, String min, String max, Boolean breakdown, String ipAddress, 
			String port, Boolean configure) {
		super();
		this.id = id;
		this.idLocation = idLocation;
		this.type = type;
		this.state = state;
		this.alert = alert;
		this.brand = brand;
		this.macAddress = macAdress;
		this.installation = installation;
		this.ipAddress = ipAddress;
		this.port = port;
		this.configure = configure;
		this.min = min;
		this.max = max;
		this.breakdown = breakdown;
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
				+ ", \n\t \"alert\" : "
				+ alert
				+ ", \n\t \"brand\" : \""
				+ brand
				+ "\", \n\t \"macAddress\" : \""
				+ macAddress
				+ "\", \n\t \"ipAddress\" : \""
				+ ipAddress
				+ "\", \n\t \"port\" : \""
				+ port
				+ "\", \n\t \"configure\" : \""
				+ configure
				+ "\", \n\t \"installation\" : \""
				+ installation
				+ "\", \n\t \"min\" : \""
				+ min
				+ "\", \n\t \"max\" : \""
				+ max
				+ "\", \n\t \"breakdown\" : "
				+ breakdown
				+ "\n }";
	}
}
