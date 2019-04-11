package fr.pds.floralis.commons.bean.entity;

/**
 * Personnel 
 * The entity made to map the Personnel object and map it to JSON with the toJSON
 * 
 * @author alveslaura
 *
 */

public class Personnel extends Staff{

	public Personnel(int id, String lastName, String firstName, int code) {
		super(id, lastName, firstName, code);
	}

}