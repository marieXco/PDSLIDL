package fr.pds.floralis.gui;

import java.sql.Connection;

import java.sql.SQLException;

import fr.pds.floralis.server.configurationpool.DataSource;
import fr.pds.floralis.server.configurationpool.JDBCConnectionPool;

public class OpenDatabase {
	JDBCConnectionPool jdbc;

	public static JDBCConnectionPool database() throws ClassNotFoundException, SQLException {
		JDBCConnectionPool jdbc = new JDBCConnectionPool();
		DataSource.createPool(jdbc);
		System.out.println("Opened database successfully");
		return jdbc;
	}

	public static Connection databaseConnection(JDBCConnectionPool jdbc) throws ClassNotFoundException, SQLException {
		Connection connection = DataSource.getConnectionFromPool(jdbc);	
		return connection;
	}

}
