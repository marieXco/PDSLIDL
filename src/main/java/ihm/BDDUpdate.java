package ihm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;


public class BDDUpdate {
	ArrayList arr = new ArrayList();
	String firstname;

	Connection c = null;
	Statement stmt = null;
	
	public BDDUpdate(String newFirstname, String oldFirstname)  {
		
		try {
			Class.forName("org.postgresql.Driver");
			c = DriverManager
					.getConnection("jdbc:postgresql://localhost:5432/test",
							"postgres", "root");
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");

			stmt = c.createStatement();
			
			ResultSet rs = stmt.executeQuery( "SELECT FIRSTNAME FROM PATIENTS;");
			ResultSetMetaData rsmd = rs.getMetaData();
			while(rs.next()) {          
				for(int i=1;i<=rsmd.getColumnCount();i++)
					arr.add(rs.getString(i));
			}
			for(int i=0; i<arr.size(); i++)
			{
				System.out.println (i+"=>"+arr.get(i));
			}
			rs.close();
			stmt.close();
			
			String sql = "UPDATE PATIENTS set FIRSTNAME = ? where FIRSTNAME= ?;";
			
			PreparedStatement statement = c.prepareStatement(sql);
	        statement.setString(1, newFirstname);
	        statement.setString(2, oldFirstname);

	        statement.execute();
			c.commit();

			
			c.close();
			
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName()+": "+ e.getMessage() );
			System.exit(0);
		}
		System.out.println("Operation done successfully");
	}
	public String toString() {
		return "BDDUpdate [v=" + arr + "]";
	}
}
