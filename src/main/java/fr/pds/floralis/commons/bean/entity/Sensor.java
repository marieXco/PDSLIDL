package fr.pds.floralis.commons.bean.entity;

import java.sql.Date;

import fr.pds.floralis.commons.bean.entity.type.TypeSensor;

public class Sensor {

	private int id;
	private TypeSensor type;
	private Boolean state;
	private Alert alerts[];
	private String brand;
	private String macAdress;
	private Date installation;
	private String caracteristics;
	private Breakdown breakdowns[];

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public Alert[] getAlerts() {
		return alerts;
	}

	public void setAlerts(Alert[] alerts) {
		this.alerts = alerts;
	}

	public Breakdown[] getBreakdowns() {
		return breakdowns;
	}

	public void setBreakdowns(Breakdown[] breakdowns) {
		this.breakdowns = breakdowns;
	}

	public Sensor(int id, TypeSensor type, Boolean state, Alert[] alerts,
			String brand, String macAdress, Date installation,
			String caracteristics, Breakdown[] breakdowns) {
		super();
		this.id = id;
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
	
	
	

}
