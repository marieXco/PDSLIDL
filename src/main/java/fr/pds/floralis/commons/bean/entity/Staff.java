package fr.pds.floralis.commons.bean.entity;

import java.util.List;

import fr.pds.floralis.commons.bean.entity.type.TypeFonction;

public abstract class Staff extends Person {

	public Staff(int id, String lastName, String firstName, Code code) {
		super(id, lastName, firstName, code);
		// TODO Auto-generated constructor stub
	}

	private TypeFonction fonction;
	private String userName;
	private String password;

	public void addPatient() {
	};

	public void deletePatient() {
	};

	public List<Sensor> seeSensors() {
		return null;
	}

	public void updateCode() {
	};

	public TypeFonction getFonction() {
		return fonction;
	}

	public void setFonction(TypeFonction fonction) {
		this.fonction = fonction;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
