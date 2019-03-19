package ihm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import org.json.*;
import org.postgresql.util.PGobject;

public class JSONRequest {

	public JSONRequest() throws SQLException {
	
		Connection c = null;
		Statement stmt = null;
		//Insert into test
//		JSONObject obj = new JSONObject();
//
//	      obj.put("name", "foo");
//	      obj.put("num", new Integer(100));
//	      obj.put("balance", new Double(1000.21));
//	      obj.put("is_vip", new Boolean(true));
//	      
//	      PGobject jsonObject = new PGobject();
//	      jsonObject.setType("json");
//	      jsonObject.setValue(obj.toString());
	      
		
	      try {
	         Class.forName("org.postgresql.Driver");
	         c = DriverManager
	            .getConnection("jdbc:postgresql://localhost:5432/test_json",
	            "postgres", "root");
	         c.setAutoCommit(false);
	         stmt = c.createStatement();
	         System.out.println("Opened database successfully");
	         
	         ResultSet rs = stmt.executeQuery( "SELECT * FROM test;" );
				
				JSONArray json = new JSONArray();
				ResultSetMetaData rsmd = rs.getMetaData();
				while(rs.next()) {
				  int numColumns = rsmd.getColumnCount();
				  JSONObject object = new JSONObject();
				  for (int i=1; i<=numColumns; i++) {
				    String column_name = rsmd.getColumnName(i);
				    object.put(column_name, rs.getObject(column_name));
				  }
				  json.put(object);
				  System.out.println(object);
				}			
				rs.close();
				stmt.close();
	         
//	         stmt = c.createStatement();
//	         String sql = "INSERT INTO test (data) "
//	            + "VALUES (?);";
//	        
//	         
//	         PreparedStatement statement = c.prepareStatement(sql);
//	         statement.setObject(1, jsonObject);
//	         statement.execute();
//	         c.commit();
//	         c.close();
	      } catch (Exception e) {
	         System.err.println( e.getClass().getName()+": "+ e.getMessage() );
	         System.exit(0);
	      }
	      System.out.println("Records created successfully");

	}
	
	
}
