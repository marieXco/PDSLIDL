package fr.pds.floralis.server.service;

import java.sql.SQLException;

/** 
 * TestMain
 * Mehtod to start the server
 * 
 * @author alveslaura
 *
 */

public class TestMain {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		TimeServer ts = new TimeServer();
		ts.open();	
	}
}