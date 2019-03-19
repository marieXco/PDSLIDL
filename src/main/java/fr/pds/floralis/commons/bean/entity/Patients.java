package fr.pds.floralis.commons.bean.entity;

public class Patients {
	private String firstname;
    private String lastname;
    private int code;
	private int id;
	
	
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}

}
