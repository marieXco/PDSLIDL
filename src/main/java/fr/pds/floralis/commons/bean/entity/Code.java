package fr.pds.floralis.commons.bean.entity;

/**
 * Code 
 * The entity made to map the Code object and map it to JSON with the toJSON
 * 
 * @author alveslaura
 *
 */

import java.util.ArrayList;
import java.util.List;

public class Code {

	private int id = 0;
	private int person;
	private String code = "";

	private List<Integer> tabCode = new ArrayList<Integer>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPerson() {
		return person;
	}

	public void setPerson(int person) {
		this.person = person;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	

	public List<Integer> getTabCode() {
		return tabCode;
	}

	public void setTabCode(List<Integer> tabCode) {
		this.tabCode = tabCode;
	}

	public Code(int id, int person, String code, List<Integer> tabCode) {
		super();
		this.id = id;
		this.person = person;
		this.code = code;
		this.tabCode = tabCode;
	}

	

}
