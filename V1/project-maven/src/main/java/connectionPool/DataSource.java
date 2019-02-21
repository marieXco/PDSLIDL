package connectionpool;

import java.sql.Connection;
import java.sql.SQLException;

/*
 * DataSource class
 * Where the client ask has access to the connections
 * 
 * JDBCConnexionPool Instance
 * Static methods to create the connections, to get one connection, 
 * to give back one connection or to close all connections 
 */
public class DataSource {

	public static JDBCConnectionPool jcp;
	
	/*
	 * Method that creates a number of connections
	 */
	public static void createPool(JDBCConnectionPool jcp) throws ClassNotFoundException, SQLException {
		jcp.putConnection();
	}
	
	/*
	 * Method that gives one connection
	 */
	public static Connection getConnectionFromPool(JDBCConnectionPool jcp) throws ClassNotFoundException, SQLException {
		Connection connection = jcp.getConnection();
		return connection;
	}

	/*
	 * Method that gives back a connection
	 */
	public static void backConnection(JDBCConnectionPool jcp, Connection connection) {
		jcp.backConnection(connection);
	}

	/*
	 * Method to close every connection
	 */
	public static void close(JDBCConnectionPool jcp) throws SQLException {
		jcp.closeAll(); 
	}

}
