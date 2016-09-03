package test;

import java.sql.* ;  // for standard JDBC programs
import java.math.* ; // for BigDecimal and BigInteger support
import static spark.Spark.get;
import static spark.Spark.post;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

//import app.NewPlaces;



public class FirstExample {
   // JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.postgresql.Driver";
	static final String DB_URL = "jdbc:postgresql://localhost/testdb";
    interface Validable {
        boolean isValid();
    }
    private static final int HTTP_BAD_REQUEST = 400;
    public static class Model {        
        public String createUser(String name, String email){
        	String sql;
            sql = "insert into users values(default, '" + name + "', '" + email  + "') RETURNING id;";
            return sql;
        }
        public String createPlace(int user_id, String name){
        	String sql;
            sql = "insert into places values(default, " + user_id + ", '" + name  + "')";
            return sql;
        }
        public String createPass(int user_id, String hash){
        	String sql;
            sql = "insert into passwords values(default, " + user_id + ", '" + hash  + "')";
            return sql;
        }
        public String getAllUsers(String email){
        	String sql;
            sql = "select * from users where email='" + email + "'";
            return sql;
        }
        public String getPass(int user_id){
        	String sql;
            sql = "select * from passwords where user_id='" + user_id + "'";
            return sql;
        }
    }
   public static void main(String[] args) {
	   post("/users", (request, response) -> {
		 Connection conn = null;
		 Statement stmt = null;
		 try{
		   Model model = new Model();
		  Class.forName("org.postgresql.Driver");
		  conn = DriverManager.getConnection(DB_URL);
		  stmt = conn.createStatement();
		  ResultSet rs = stmt.executeQuery(model.createUser("mock", "data"));
		  while(rs.next()){
		     //Retrieve by column name
		     int id  = rs.getInt("id");
		     stmt = conn.createStatement();
			 ResultSet psrs = stmt.executeQuery(model.createPass(id, "password"));
		     String name = rs.getString("name");
		     //Display values
		     //System.out.print("ID: " + id);
		     //System.out.print(", Name: " + name);
		  }
		  rs.close();
		  stmt.close();
		  conn.close();
   }
   
  catch(SQLException se){
		  //Handle errors for JDBC
		  se.printStackTrace();
   }catch(Exception e){
		  //Handle errors for Class.forName
		  e.printStackTrace();
   }finally{
		  //finally block used to close resources
		  try{
		     if(stmt!=null)
		        stmt.close();
		  }catch(SQLException se2){
		  }// nothing we can do
		  try{
		     if(conn!=null)
		        conn.close();
		  }catch(SQLException se){
		     se.printStackTrace();
		  }//end finally try
   }
		return request;
	   });
	   post("/verify", (request, response) -> {
		   JsonParser parser = new JsonParser();
		   JsonElement jsonTree = parser.parse(request.body());
		   if(jsonTree.isJsonObject()) {
			    JsonObject jsonObject = jsonTree.getAsJsonObject();
			    System.out.println(jsonObject);
			    JsonElement email = jsonObject.get("email");
			    System.out.println(email);
			}
		 Connection conn = null;
		 Statement stmt = null;
		 try{
		   Model model = new Model();
		  Class.forName("org.postgresql.Driver");
		  conn = DriverManager.getConnection(DB_URL);
		  stmt = conn.createStatement();
		  ResultSet rs = stmt.executeQuery(model.getAllUsers("data"));
		  if(rs.next()){
		     //retrieve user by email
		     int id  = rs.getInt("id");
		     stmt = conn.createStatement();
			 ResultSet psrs = stmt.executeQuery(model.getPass(id));
			  if(psrs.next()){
				  //retrieving password from user
				String hash = psrs.getString("hash");
				System.out.print("Password "+hash);
				//IF PASSWORD === INPUT
			  }
		  }
		  else{
			 System.out.print("not found");
			 //send "not found" to client
		  }
		  rs.close();
		  stmt.close();
		  conn.close();
   }
   
  catch(SQLException se){
		  //Handle errors for JDBC
		  se.printStackTrace();
   }catch(Exception e){
		  //Handle errors for Class.forName
		  e.printStackTrace();
   }finally{
		  //finally block used to close resources
		  try{
		     if(stmt!=null)
		        stmt.close();
		  }catch(SQLException se2){
		  }// nothing we can do
		  try{
		     if(conn!=null)
		        conn.close();
		  }catch(SQLException se){
		     se.printStackTrace();
		  }//end finally try
   }
		return request;
	   });
   }
}