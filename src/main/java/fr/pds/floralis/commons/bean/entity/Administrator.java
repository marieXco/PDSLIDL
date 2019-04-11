package fr.pds.floralis.commons.bean.entity;

/**
 * Administrator 
 * The entity made to map the administrator object and map it to JSON with the toJSON
 * 
 * @author alveslaura
 *
 */

public class Administrator extends Staff {
	public Administrator(int id, String lastName, String firstName, int code) {
		super(id, lastName, firstName, code);
	}
	
	public String toJSON() {
		return super.toString();
	}
	
}
