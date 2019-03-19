package ihm;

import java.sql.Connection;

import java.sql.SQLException;
import connectionpool.DataSource;
import connectionpool.JDBCConnectionPool;

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
