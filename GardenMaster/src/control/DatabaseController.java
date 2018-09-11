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
import java.sql.SQLNonTransientConnectionException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;


public class DatabaseController {
	
	private static final String DB_URL = "jdbc:mysql://localhost:3306/gardenmaster?autoReconnect=true&useSSL=false";
	private static final String DB_USER = "mkreid";
	private static final String DB_PWD = "ratbastard";
	private static final String PW_SALT = "ILUV2PARTY!";
	
	private static Connection conn;
	private static String stmt;
	private static PreparedStatement ps;
	private static ResultSet rs;
	
	public static final int AUTH_FAILURE     = 0;
	public static final int AUTH_SUCCESS     = 1;
	public static final int AUTH_NO_DB 		 = 2;
	public static final int AUTH_UNKNOWN_ERR = 3;
	
	
	public static int authenticateUser(String username, String password) {
		
		try {
			
			setupConnection();
			stmt = "select count(*) from SEC_USERS where username = ? and password = ?";
			if (conn != null) {
				ps = conn.prepareStatement(stmt);
			} else {
				return DatabaseController.AUTH_NO_DB;
			}
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
					return DatabaseController.AUTH_SUCCESS; 
				}
			}
			
		} catch (SQLNonTransientConnectionException e) {
			// No db to connect to ...
			e.printStackTrace();
			return DatabaseController.AUTH_NO_DB;
			
		} catch (Exception e) {
			// something else went wrong authenticating!
			e.printStackTrace();
			return DatabaseController.AUTH_UNKNOWN_ERR;
		}
		closeConnection();
		return DatabaseController.AUTH_FAILURE;
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
	
	
	/**
	 * This function is used during password reset requests.
	 * It looks up a given email in the database and returns 
	 * true if and only if there is an account registered to
	 * that email address.
	 * 
	 * @param email - address to check the database for
	 * @return true if account found, false otherwise.
	 */
	public static boolean emailLookup(String email) {
		
		try {
			// prepare connection
			setupConnection();
			
			// prepare stmt:
			stmt = "select username from SEC_USERS where email_addr = ?";
			ps = conn.prepareStatement(stmt);
			ps.setString(1, email);
			
			// execute prepared statement
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				// we found an account!
				
				return true;
			}
			
			// close connection
			closeConnection();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// no account found
		return false;
	}
	
	
	private static String obfuscatepw(String pw) throws DigestException {

		//obfuscate password:
		MessageDigest md = null;
		String obfpw = "";
		String payload = (pw + PW_SALT);
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
			conn = null;
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


	public static boolean registerPwReset(String email, UUID token, Date expiry) {
		try {
			// prepare connection
			setupConnection();
			java.text.SimpleDateFormat sdf = 
				     new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			// prepare stmt:
			stmt = "insert into SEC_PW_RESET (token, email_addr, expires) values (?, ?, ?)";
			ps = conn.prepareStatement(stmt);
			ps.setString(1, String.valueOf(token));
			ps.setString(2, email);
			ps.setString(3, sdf.format(expiry));
			
						
			// execute prepared statement
			int result = ps.executeUpdate();
			
			if (result == 1) {
				return true;
			}
			
			// close connection
			closeConnection();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
		
	}
}
