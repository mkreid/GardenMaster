package control;

import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Postmaster  {
	private final static Logger LOGGER = Logger.getLogger(LoginController.class.getName());

	private static final String FROM = "<snip>";
	private static final String TO   = "<snip>";
	private static final String FROM_NAME = "No Reply";
	private static final String SMTP_USERNAME = "<snip>";
	private static final String SMTP_PASSWORD = "<snip>";
	private static final String HOST = "smtp.gmail.com";
	private static final int    SMTP_PORT = 587;
	
	static final String SUBJECT = "Gmail test (SMTP interface accessed using Java)";
	
	static final String BODY = String.join(
    	    System.getProperty("line.separator"),
    	    "<h1>Gmail SMTP Email Test</h1>",
    	    "<p>This email was sent with gmail using the ", 
    	    "<a href='https://github.com/javaee/javamail'>Javamail Package</a>",
    	    " for <a href='https://www.java.com'>Java</a>."
    	);
	
	
    public static void main(String[] args) throws Exception {
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
        msg.setFrom(new InternetAddress(FROM,FROM_NAME));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(TO));
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
        msg.setFrom(new InternetAddress(FROM,FROM_NAME));
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
