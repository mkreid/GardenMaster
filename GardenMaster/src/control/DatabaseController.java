package control;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class DatabaseController {
	
	public static boolean authenticateUser(String username, String password) {
		
		try {
			
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gardenmaster?autoReconnect=true&useSSL=false", "root", "malcolm0");
				
			String stmt = "select count(*) from SEC_USERS where username = \'" + username + "\' and password = \'" + obfuscatepw(password) + "\'";

			System.out.println("DEBUG: SQL_STMT=" + stmt);
			
			PreparedStatement ps = conn.prepareStatement(stmt);
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				// found a match
				if (rs.getInt(1) == 1) {
					rs.close();
					ps.close();
					conn.close();
					return true; 
				}
			}
			rs.close();
			ps.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
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

}
