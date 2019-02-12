package ihm;

import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.Statement;

import connectionPool.DataSource;
import connectionPool.JDBCConnectionPool;

public class BDDEraseAll {

	public BDDEraseAll() {
		JDBCConnectionPool jdbc;
		Statement stmt = null;

		try {
			jdbc = new JDBCConnectionPool();
			Connection connection = DataSource.getConnectionFromPool(jdbc);
			assertNotNull(connection);
			connection.setAutoCommit(false);
			System.out.println("Opened database successfully");

			stmt = connection.createStatement();
			String sql = "DELETE FROM PATIENTS;";
			stmt.executeUpdate(sql);
			connection.commit();

			stmt.close();
			connection.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Operation done successfully");
	}
}
