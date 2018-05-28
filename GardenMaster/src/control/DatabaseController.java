package control;

import java.security.DigestException;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class DatabaseController {
	
	public static boolean authenticateUser(String username, String password) {
		
		try {
			
			//Class.forName("com.mysql.jdbc.Driver");
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gardenmaster?autoReconnect=true&useSSL=false", "root", "malcolm0");
			//obfuscate password:
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			
			String obfpw = "";
			
			String payload = (password+"ILUV2PARTY!");
			 try {
			     md.update(payload.getBytes());
			     MessageDigest tc1 = (MessageDigest) md.clone();
			     byte[] toChapter1Digest = tc1.digest();
			     obfpw = byteArrayToHexString(toChapter1Digest);
			     
			 } catch (CloneNotSupportedException cnse) {
			     throw new DigestException("couldn't make digest of partial content");
			 }
			
			
			String stmt = "select count(*) from SEC_USERS where username = \'" + username + "\' and password = \'" + obfpw + "\'";

			System.out.println("DEBUG: SQL_STMT=" + stmt);
			
			PreparedStatement ps = conn.prepareStatement(stmt);
			ResultSet rs = ps.executeQuery();
			
			
			if (rs.next()) {
				// found a match
				if (rs.getInt(1) == 1) {
					return true; 
				}
			}
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

}
