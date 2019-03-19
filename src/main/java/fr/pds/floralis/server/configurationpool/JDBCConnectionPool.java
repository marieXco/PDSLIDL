package fr.pds.floralis.server.configurationpool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.pds.floralis.server.configuration.Configuration;

/**
 * JDBCConnectionPool class
 * Class where we create a number of connections available and a number of connections that are used by the users
 * Both stocked in an ArrayList of Connections
 * 
 * Contains the methods to add connections to our available connections,
 * to give one connection from the available ones to the user
 * or to return a connection that is not used anymore to our available connections
 */

public class JDBCConnectionPool {
	private String driver;
	private String url, password, login;
	private int nbrConnexions;
	Connection connection = null;

	/*
	 * availableConnections : ArrayList that contains opened connections that are not used
	 * synchroConnections : ArrayList that contains the synchronized connections that are used
	 */
	private List<Connection> availableConnections; 
	private List<Connection> synchroConnections;


	public JDBCConnectionPool() throws ClassNotFoundException, SQLException {
		Configuration configuration = new Configuration();
		this.driver = configuration.getCdriver();
		this.url = configuration.getCurl();
		this.password = configuration.getCpassword();
		this.login = configuration.getClogin();
		this.nbrConnexions = Integer
				.parseInt(configuration.getCnbrConnexions());

		availableConnections = new ArrayList<Connection>();
		synchroConnections = Collections.synchronizedList(new ArrayList<Connection>());
	}
	

	/*
	 * Method to create the maximum number of available connections
	 */
	public void putConnection() throws
	SQLException, ClassNotFoundException {
		Class.forName(driver);
		Connection connection = DriverManager.getConnection(url, login,
				password);
		
		for (int i = 0; i < nbrConnexions; i++) {
			availableConnections.add(connection);
			System.out.println("Connection " + availableConnections.size() + " created");
		}
	}
	
	/*
	 * Method to get a connection from our available connections 
	 * and to put it on our synchronized connections
	 */
	public Connection getConnection() throws SQLException, ClassNotFoundException {
		System.out.println();
		System.out.println("GetConnection");
		System.out.println("------------------------------------------");
		
		if (!(availableConnections.isEmpty())) {
			System.out.println("AvailableConnections is not empty, size : " + availableConnections.size());

			System.out.println("AvailableConnections list : " + availableConnections);
			connection = availableConnections.get(availableConnections.size() - 1);

			System.out.println();
			System.out.println("Connection that we put into synchroConnections : " + connection);	
			synchroConnections.add(connection);
			System.out.println("SynchroConnections list : " + synchroConnections);

			availableConnections.remove(availableConnections.size() - 1);
			System.out.println();
			System.out.println("AvailableConnections after remove : " + availableConnections);
		}
		else {
			System.out.println("No connections are available, please wait");
			throw new RuntimeException("Maximum pool size reached, no available connections!");

		}
		return connection;
	}


	/*
	 * Method to give back one connection from our synchronized connections
	 * to our available connections
	 */
	public void backConnection(Connection connection) {
		System.out.println();
		System.out.println("BackConnection");
		System.out.println("------------------------------------------");

		System.out.println("Connection that we put back : " + connection);
		availableConnections.add(connection);

		System.out.println();
		System.out.println("AvailableConnections after backConnection : " + availableConnections);
		synchroConnections.remove(connection);
		System.out.println("SynchroConnections after backConnection : " + synchroConnections);
		System.out.println();
	}


	/*
	 * Method to close every connections, from both of our lists
	 */
	public void closeAll() throws SQLException {		
		System.out.println();
		System.out.println("CloseAll");
		System.out.println("------------------------------------------");

		System.out.println("BackConnection for every synchroConnections");
		while (!(synchroConnections.isEmpty())) {
			backConnection(synchroConnections.get(synchroConnections.size() - 1));
		}

		System.out.println("Removing all available connections");
		while (!(availableConnections.isEmpty())) {
			availableConnections.remove(availableConnections.size() - 1);	
		}

		System.out.println("SynchroConnections after closeAll : " + synchroConnections);
		System.out.println("availableConnections after closeAll : " + availableConnections);
	}

}
