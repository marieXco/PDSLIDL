package fr.pds.floralis.commons.bean.entity;

public class Code {

	private int id = 0;
	private Person person;
	private String code = "";
	private Code tabCode[];

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Code[] getTabCode() {
		return tabCode;
	}

	public void setTabCode(Code tabCode[]) {
		this.tabCode = tabCode;
	}

	public Code(int id, Person person, String code, Code[] tabCode) {
		super();
		this.id = id;
		this.person = person;
		this.code = code;
		this.tabCode = tabCode;
	}

}
