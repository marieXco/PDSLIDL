package connectionPool;

import static org.junit.Assert.*;
import connectionPool.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

public class DataSourceTest {

	@Test
	void test()  {
		JDBCConnectionPool jdbc;
		
		
		try {
			jdbc = new JDBCConnectionPool();
			Connection connection = DataSource.getConnectionFromPool(jdbc);
			assertNotNull(connection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
