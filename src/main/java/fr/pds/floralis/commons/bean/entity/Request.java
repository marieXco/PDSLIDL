package fr.pds.floralis.commons.bean.entity;

import org.json.JSONObject;

public class Request {
	private String type;
	private String entity;
	private JSONObject parameters;
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getEntity() {
		return entity;
	}
	
	public void setEntity(String entity) {
		this.entity = entity;
	}
	
	public JSONObject getFields() {
		return parameters;
	}
	
	public void setFields(JSONObject fields) {
		this.parameters = fields;
	}

	
	public String toString() {
		return "\n { \n\t \"type\" : \""
				+ type
				+ "\", \n\t \"requested-view-entity\" : \""
				+ entity
				+ "\", \n\t \"requested-parameters\" : "
				+ parameters
				+ "\n }";
	}
}
