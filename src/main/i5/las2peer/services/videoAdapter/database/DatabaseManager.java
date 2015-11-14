package i5.las2peer.services.videoAdapter.database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.JSONObject;
import org.json.JSONArray;


/**
 * 
 * Establishes connection to the MySql database.
 * Uses 'dbconnection.properties' file for configuration.
 *
 */
public class DatabaseManager
{
	private static String url;
	private static String host;
	private static String port;
	private static String dbName;
	private static String driver;
	private static String userName;
	private static String password;
	private static String databaseServer;
	
	public void init(String driver, String databaseServer, String port, String dbName, String userName, String password, String host) {

		System.out.println("DB CHECK: ");
		
		this.driver = driver;
		this.databaseServer = databaseServer;
		this.port = port;
		this.dbName = dbName;
		this.userName = userName;
		this.password = password;
		this.host = host;
		
		url = "jdbc:" + this.databaseServer + "://" + this.host +":"+ this.port + "/";
		
		System.out.println("DB URL: "+url);
	}

	public void saveSearch(String searchString, String result, String username, String domain){
		
		int rowCount = 0;
		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			System.out.println("Domain: "+domain);
			String insertQuery = "insert into searches (query, result, user, domain) values (?, ?, ?, ?)";
			
			PreparedStatement pstmt = conn.prepareStatement(insertQuery);
			pstmt.setString(1, searchString);
			pstmt.setString(2, result);
			pstmt.setString(3, username);
			pstmt.setString(4, domain);
			
			rowCount = pstmt.executeUpdate();
			
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public String setStrategy(String strategy, int language, int location, int duration, 
			int relevance, int weightOrder, String sequence){
		
		String message = null;
		int rowCount = 0;
		ResultSet res = null;
		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			
			
			
			//String id = String.valueOf(language)+String.valueOf(location)+String.valueOf(duration)+
					//String.valueOf(adaptation);
			
			//System.out.println("id: "+id);
			
			/*String selectQuery = "SELECT * FROM adaptive_strategies WHERE sequence = ?";
			PreparedStatement selectPstmt = conn.prepareStatement(selectQuery);
			selectPstmt.setString(1, sequence);
			res = selectPstmt.executeQuery();*/
			
			//if(!res.next()){
				
				String insertQuery = "insert into adaptive_strategies (sequence, strategy, language, location, duration, "
						+ "relevance, weightOrder) values (?, ?, ?, ?, ?, ?, ?)";
				PreparedStatement insertPstmt = conn.prepareStatement(insertQuery);
				insertPstmt.setString(1, sequence);
				insertPstmt.setString(2, strategy);
				insertPstmt.setInt(3, language);
				insertPstmt.setInt(4, location);
				insertPstmt.setInt(5, duration);
				insertPstmt.setInt(6, relevance);
				insertPstmt.setInt(7, weightOrder);
				
				rowCount = insertPstmt.executeUpdate();
				message = "saved";
				
			/*}
			else{
				
				message = res.getString("strategy");
				
			}*/
			
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
		
	}
	
	public String getStrategies(){
		
		ResultSet res = null;
		JSONArray strategyJSONArray = new JSONArray();
		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			
			String selectQuery = "SELECT * FROM adaptive_strategies";
			PreparedStatement selectPstmt = conn.prepareStatement(selectQuery);
			res = selectPstmt.executeQuery();
			
			while(res.next()){
				
				JSONObject strategyJSON = new JSONObject();
				
				strategyJSON.put("strategy", res.getString("strategy"));
				strategyJSON.put("language", res.getString("language"));
				strategyJSON.put("location", res.getString("location"));
				strategyJSON.put("duration", res.getString("duration"));
				strategyJSON.put("relevance", res.getString("relevance"));
				strategyJSON.put("weightOrder", res.getString("weightOrder"));
				strategyJSON.put("sequence", res.getString("sequence"));
				
				strategyJSONArray.put(strategyJSON);
			}
			
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strategyJSONArray.toString();
		
	}
	
	
	
}