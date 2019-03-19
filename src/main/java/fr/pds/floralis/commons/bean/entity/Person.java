package fr.pds.floralis.commons.bean.entity;

public abstract class Person {
	private int id;
	private String lastName;
	private String firstName;
	private Code code;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLastnName() {
		return lastName;
	}

	public void setLastnName(String lastnName) {
		this.lastName = lastnName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
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
		this.lastName = lastName;
		this.firstName = firstName;
		this.code = code;
	}

}
