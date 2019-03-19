package fr.pds.floralis.commons.bean.entity;

import java.sql.Date;

public class Sensors {

	private String brand;
	private String macAddress;
	private Date dateInstallation;
	private Boolean state;
	private Boolean alert;
	private Boolean breakdown;
	private String caracteristics;
	private int id;
	
	
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public Date getDateInstallation() {
		return dateInstallation;
	}
	public void setDateInstallation(Date dateInstallation) {
		this.dateInstallation = dateInstallation;
	}
	public Boolean getState() {
		return state;
	}
	public void setState(Boolean state) {
		this.state = state;
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
	public String getCaracteristics() {
		return caracteristics;
	}
	public void setCaracteristics(String caracteristics) {
		this.caracteristics = caracteristics;
	}
	public int getId() {
		return id;
	}

}
