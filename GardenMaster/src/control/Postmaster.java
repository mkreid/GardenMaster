package control;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Postmaster  {
	private final static Logger LOGGER = Logger.getLogger(LoginController.class.getName());

	private static final String FROM_ADDR;
	private static final String FROM_NAME;
	private static final String SMTP_USERNAME;
	private static final String SMTP_PASSWORD;
	private static final String HOST;
	private static final int    SMTP_PORT;
	
	static final String SUBJECT = "Gmail test (SMTP interface accessed using Java)";
	
	private static final Properties config;
	
	static final String BODY = String.join(
    	    System.getProperty("line.separator"),
    	    "<h1>Gmail SMTP Email Test</h1>",
    	    "<p>This email was sent with gmail using the ", 
    	    "<a href='https://github.com/javaee/javamail'>Javamail Package</a>",
    	    " for <a href='https://www.java.com'>Java</a>."
    	);
	
	static {
		// read in config file to get Mail settings:
		Properties fallback = new Properties();
		fallback.setProperty("MAIL_FROM_ADDRESS",  	"noreply@domain.com");
		fallback.setProperty("MAIL_FROM_NAME", 		"No Reply");
		fallback.setProperty("MAIL_SMTP_USER",  	"username");
		fallback.setProperty("MAIL_SMTP_PASS", 		"password");
		fallback.setProperty("MAIL_SMTP_HOST", 		"mail.domain.com"); 
		fallback.setProperty("MAIL_SMTP_PORT", 		"587");
		config = new Properties(fallback);
		try {
			config.load(DatabaseController.class.getResourceAsStream("../gardenmaster.conf")); // must be in GardenMaster/WEB-INF/classes/
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// capture properties:
		FROM_ADDR = config.getProperty("MAIL_FROM_ADDRESS");
		FROM_NAME = config.getProperty("MAIL_FROM_NAME");
		SMTP_USERNAME  = config.getProperty("MAIL_SMTP_USER");
		SMTP_PASSWORD = config.getProperty("SMTP_PASSWORD");
		HOST = config.getProperty("MAIL_SMTP_HOST");
		String portstr = config.getProperty("MAIL_SMTP_PORT");
		if (!portstr.isEmpty()) {
			SMTP_PORT = Integer.parseInt(portstr);
		} else {
			SMTP_PORT = 0;
		}
		
		
		// Output for debug info:
		System.out.println("config file FROM_ADDR is: " + FROM_ADDR);
		System.out.println("config file FROM_NAME is: " + FROM_NAME);
		System.out.println("config file SMTP_USERNAME is: " + SMTP_USERNAME);
		System.out.println("config file SMTP_PASSWORD is: " + SMTP_PASSWORD);
		System.out.println("config file MAIL_SMTP_HOST is: " + HOST);
		System.out.println("config file MAIL_SMTP_PORT is: " + SMTP_PORT);
	}
	
	
    public static void main(String[] args, String to) throws Exception {
    	// Create a Properties object to contain connection configuration information.
    	Properties props = System.getProperties();
//    	props.put("mail.transport.protocol", "smtp");
    	props.put("mail.smtp.auth", "true");
    	props.put("mail.smtp.starttls.enable", "true");
    	props.put("mail.smtp.host", HOST);
    	props.put("mail.smtp.port", SMTP_PORT);
    	
    	// Create a Session object to represent a mail session with the specified properties. 
    	Session session = Session.getDefaultInstance(props);
    	
    	// Create a message with the specified information. 
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(FROM_ADDR,FROM_NAME));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(SUBJECT);
        msg.setContent(BODY,"text/html");
        
        // Create a transport.
        Transport transport = session.getTransport();
                    
        // Send the message.
        try
        {
            System.out.println("Sending...");
            
            // Connect to Amazon SES using the SMTP username and password you specified above.
            transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
        	
            // Send the email.
            transport.sendMessage(msg, msg.getAllRecipients());
            System.out.println("Email sent!");
        }
        catch (Exception ex) {
            System.out.println("The email was not sent.");
            System.out.println("Error message: " + ex.getMessage());
        }
        finally
        {
            // Close and terminate the connection.
            transport.close();
        }
    	
    }
    
    
    public static void sendMail(String to_address, String subject, String body) throws Exception {
    	Properties props = System.getProperties();
    	props.put("mail.smtp.auth", "true");
    	props.put("mail.smtp.starttls.enable", "true");
    	props.put("mail.smtp.host", HOST);
    	props.put("mail.smtp.port", SMTP_PORT);
    	
    	// Create a Session object to represent a mail session with the specified properties. 
    	Session session = Session.getDefaultInstance(props);
    	
    	// Create a message with the specified information. 
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(FROM_ADDR,FROM_NAME));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to_address));
        msg.setSubject(subject);
        msg.setContent(body,"text/html");
        
        // Create a transport.
        Transport transport = session.getTransport();
        
     // Send the message.
        try
        {
            LOGGER.info("Sending...");
            
            // Connect to Amazon SES using the SMTP username and password you specified above.
            transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
        	
            // Send the email.
            transport.sendMessage(msg, msg.getAllRecipients());
            LOGGER.info("Email sent!");
        }
        catch (Exception ex) {
        	LOGGER.info("The email was not sent.");
        	LOGGER.info("Error message: " + ex.getMessage());
        }
        finally
        {
            // Close and terminate the connection.
            transport.close();
        }
    }
	

}
