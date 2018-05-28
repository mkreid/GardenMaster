package control;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Calendar;
import java.util.Date;


public class DatabaseController {
	
	private static final String DB_URL = "jdbc:mysql://localhost:3306/gardenmaster?autoReconnect=true&useSSL=false";
	private static final String DB_USER = "mkreid";
	private static final String DB_PWD = "ratbastard";
	
	private static Connection conn;
	private static String stmt;
	private static PreparedStatement ps;
	private static ResultSet rs;
	
	
	public static boolean authenticateUser(String username, String password) {
		
		try {
			
			setupConnection();
			stmt = "select count(*) from SEC_USERS where username = ? and password = ?";
			ps = conn.prepareStatement(stmt);
			ps.setString(1, username);
			ps.setString(2, obfuscatepw(password));
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				// found a match
				if (rs.getInt(1) == 1) {
					//update last_login value before notifying client of success
					Date currentDate = Calendar.getInstance().getTime();
					stmt = "update SEC_USERS set last_login = ? where username = ?";
					ps = conn.prepareStatement(stmt);
					ps.setDate(1, new java.sql.Date(currentDate.getTime()));
					ps.setString(2, username);
					
					ps.executeUpdate();
					
					closeConnection();
					return true; 
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		closeConnection();
		return false;
	}
	
	
	public static String registerUser(String username, String password, String first_name, String last_name, String email) {	
		String status = "FASLE";
		int rowsInserted = 0;
		try {		
			// prepare connection
			setupConnection();
			stmt = "insert into SEC_USERS (username, password, first_name, last_name, email_addr, account_type, account_created) values (?, ?, ?, ?, ?, ?, ?)";
			
			// prepare SQL
			ps = conn.prepareStatement(stmt);
			ps.setString(1, username);
			ps.setString(2, obfuscatepw(password));
			ps.setString(3, first_name);
			ps.setString(4, last_name);
			ps.setString(5, email);
			ps.setInt(6, 1);
			ps.setDate(7, new java.sql.Date((Calendar.getInstance().getTime()).getTime()));
						
			// execute prepared statement
			rowsInserted = ps.executeUpdate();		
			
		} catch (SQLIntegrityConstraintViolationException e2) {
			status = "EXISTS";
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (DigestException e) {
			e.printStackTrace();
		}
				
		
		if (rowsInserted == 1) {
			// 1 row inserted - success!
			status = "TRUE";
		} 
		
		closeConnection();
		return status;
	}
	
	public static String byteArrayToHexString(byte[] b) {
		  String result = "";
		  for (int i=0; i < b.length; i++) {
		    result +=
		          Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		  }
		  return result;
		}
	
	
	private static String obfuscatepw(String pw) throws DigestException {

		//obfuscate password:
		MessageDigest md = null;
		String obfpw = "";
		String payload = (pw+"ILUV2PARTY!");
		 try {
			 md = MessageDigest.getInstance("SHA-1");
		     md.update(payload.getBytes());
		     MessageDigest tc1 = (MessageDigest) md.clone();
		     byte[] toChapter1Digest = tc1.digest();
		     obfpw = byteArrayToHexString(toChapter1Digest);
		     
		 } catch (CloneNotSupportedException | NoSuchAlgorithmException cnse) {
		     throw new DigestException("couldn't make digest of partial content");
		 }
		 
		return obfpw;
	}

	
	private static void setupConnection() {
		conn = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PWD);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void closeConnection() {
		try {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
}
