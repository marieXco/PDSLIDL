package ihm;

import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.Statement;

import connectionPool.DataSource;
import connectionPool.JDBCConnectionPool;

public class BDDCreateTable {
	JDBCConnectionPool jdbc;
	Statement stmt = null;
	{

		try {
			jdbc = new JDBCConnectionPool();
			Connection connection = DataSource.getConnectionFromPool(jdbc);
			assertNotNull(connection);
			System.out.println("Opened database successfully");

			stmt = connection.createStatement();
			String sql = "CREATE TABLE PATIENTS "
					+ "(ID_PATIENTS CHAR(50) PRIMARY KEY  NOT NULL,"
					+ " LASTNAME       CHAR(50)    NOT NULL, "
					+ " FIRSTNAME       CHAR(50)    NOT NULL)";
			stmt.executeUpdate(sql);
			stmt.close();
			connection.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Table created successfully");
	}
}
