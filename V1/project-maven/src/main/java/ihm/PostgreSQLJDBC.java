package ihm;

import java.sql.SQLException;

import connectionpool.JDBCConnectionPool;

public class PostgreSQLJDBC {
	
	public static void main( String args[] ) throws ClassNotFoundException, SQLException {
		JDBCConnectionPool jdbc = OpenDatabase.database();
		
		WindowV1 frame = new WindowV1(jdbc);
		WindowV1 frame1 = new WindowV1(jdbc);
	}
}