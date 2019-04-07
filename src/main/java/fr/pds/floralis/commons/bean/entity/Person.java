package fr.pds.floralis.commons.bean.entity;

public abstract class Person {
	private int id;
	private String lastname;
	private String firstname;
	private Code code;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public Code getCode() {
		return code;
	}

	public void setCode(Code code) {
		this.code = code;
	}

	public Person(int id, String lastName, String firstName, Code code) {
		super();
		this.id = id;
		this.lastname = lastName;
		this.firstname = firstName;
		this.code = code;
	}

}
