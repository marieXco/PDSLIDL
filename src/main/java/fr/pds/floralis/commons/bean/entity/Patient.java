package fr.pds.floralis.commons.bean.entity;

import org.json.JSONObject;

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

	public JSONObject toJSON() {
		JSONObject patientToJson = new JSONObject();
		patientToJson.put("id", id);
		patientToJson.put("lastname", lastname);
		patientToJson.put("firstname", firstname);
		patientToJson.put("code", code);
		return patientToJson;
	}

}
