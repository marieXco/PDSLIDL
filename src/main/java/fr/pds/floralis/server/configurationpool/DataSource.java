package fr.pds.floralis.server.configurationpool;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * DataSource class
 * Where the client get access to the connections
 * 
 * JDBCConnexionPool Instance
 * Static methods to create the connections, to get one connection, 
 * to give back one connection or to close all connections 
 * 
 */

public class DataSource {
	public static JDBCConnectionPool jcp;
	
	
	
	/*
	 * Method that creates the maximum number of connections
	 */
	public static JDBCConnectionPool createPool() throws ClassNotFoundException, SQLException {
		JDBCConnectionPool jdbc = new JDBCConnectionPool();
		System.out.println();
		System.out.println("createPool :");
		jdbc.putConnection();
		return jdbc;
	}
	
	/*
	 * Method that gives one connection to the user
	 */
	public static Connection getConnectionFromPool(JDBCConnectionPool jcp) throws ClassNotFoundException, SQLException {
		System.out.println();
		Connection connection = jcp.getConnection();
		return connection;
	}

	/*
	 * Method that gives back a connection from the user
	 */
	public static void backConnection(JDBCConnectionPool jcp, Connection connection) {
		System.out.println();
		jcp.backConnection(connection);
	}

	/*
	 * Method to close all the connections
	 */
	public static void close(JDBCConnectionPool jcp) throws SQLException {
		System.out.println();
		jcp.closeAll(); 
	}

}
