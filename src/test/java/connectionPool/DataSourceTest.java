package connectionPool;

import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

public class DataSourceTest {

	@Test
	public void TestConnection() throws ClassNotFoundException {
		JDBCConnectionPool jdbc;

		try {
			jdbc = new JDBCConnectionPool();
			Connection connection = DataSource.getConnectionFromPool(jdbc);
			
			assertNotNull(connection);

			// DataSource.backConnection(connection);

		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

}
