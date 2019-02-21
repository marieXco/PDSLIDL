package connectionPool;

import static org.junit.Assert.assertNotNull;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.Test;
import connectionpool.DataSource;
import connectionpool.JDBCConnectionPool;
import ihm.OpenDatabase;

public class DataSourceTest {

	@Test
	public void TestConnection() throws ClassNotFoundException {
		JDBCConnectionPool jdbc;

		try {
			jdbc = OpenDatabase.database();
			Connection connection = DataSource.getConnectionFromPool(jdbc);
			System.out.println("Connection Used 1: " + connection);
			assertNotNull(connection);
			System.out.println();
			
			Connection connection2 = DataSource.getConnectionFromPool(jdbc);
			System.out.println("Connection Used 2: " + connection2);
			assertNotNull(connection2);
			System.out.println();
			
			DataSource.backConnection(jdbc, connection);
			
			Connection connection3 = DataSource.getConnectionFromPool(jdbc);
			System.out.println("Connection Used 3: " + connection3);
			assertNotNull(connection3);
			System.out.println();
			
			Connection connection4 = DataSource.getConnectionFromPool(jdbc);
			System.out.println("Connection Used 4:" + connection4);
			System.out.println();
			

			DataSource.close(jdbc);

		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

}
