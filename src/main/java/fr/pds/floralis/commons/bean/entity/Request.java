package fr.pds.floralis.commons.bean.entity;

import java.util.Arrays;

public class Request {
	private String type;
	private String entity;
	private String[] fields;
	private String[] values;
	
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
	public String[] getFields() {
		return fields;
	}
	public void setFields(String[] fields) {
		this.fields = fields;
	}
	public String[] getValues() {
		return values;
	}
	public void setValues(String[] values) {
		this.values = values;
	}
	
	public String toString() {
		return "\n { \n\t \"type\" : \""
				+ type
				+ "\", \n\t \"requested-view-entity\" : \""
				+ entity
				+ "\", \n\t \"requested-fields\" : "
				+ Arrays.toString(fields)
				+ ", \n\t \"requested-values\" : "
				+ Arrays.toString(values)
				+ "\n }";
	}
}
