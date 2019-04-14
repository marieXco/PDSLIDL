package fr.pds.floralis.commons.bean.entity;

/**
 * Patient 
 * The entity made to map the Patient object and map it to JSON with the toJSON
 * 
 * @author alveslaura
 *
 */

public class Patient extends Person{
	
	public Patient(int id, String lastname, String firstname, int code) {
		super(id, lastname, firstname, code);
	}

}
