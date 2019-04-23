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
		return "Request [type=" + type + ", entity=" + entity + ", parameters=" + parameters + "]";
	}

	public JSONObject toJSON() {
		JSONObject requestToJson = new JSONObject();
		requestToJson.put("type", type);
		requestToJson.put("requested-view-entity", entity);
		requestToJson.put("requested-parameters", parameters);
		return requestToJson;
	}
}
