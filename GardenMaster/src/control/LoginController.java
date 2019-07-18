package control;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Garden;

public class LoginController extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int LOGIN_REQUEST  = 1;
	public static final int LOGOUT_REQUEST = 2;
	public static final int SIGNUP_REQUEST = 3;
	public static final int PW_RESET_REQUEST = 4;
	public static final int PW_RESET       = 5;
	
	private final static Logger LOGGER = Logger.getLogger(LoginController.class.getName());
	
	
	public LoginController() {
		LOGGER.setLevel(Level.INFO);
	}
	
	

	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
				
		// calls are made using an action request integer
		String actionDelimiter = req.getParameter("action_request");
		
		if (Integer.parseInt(actionDelimiter) == LOGIN_REQUEST) {
			// we're handling a login
			String username = req.getParameter("username");
			String password = req.getParameter("password");
			
			
//			boolean loginAttempt = DatabaseController.authenticateUser(username, password);
			int loginAttempt = DatabaseController.authenticateUser(username, password);
			
			if (loginAttempt == DatabaseController.AUTH_SUCCESS) {
				// successful!
				LOGGER.info("LoginController: found corrosponding username and password in the database");
				Garden g = new Garden();
				
				req.getSession().setAttribute("currentGarden", g);
				
				resp.sendRedirect("home.jsp");
			} else if (loginAttempt == DatabaseController.AUTH_FAILURE) {
				// login failure
				LOGGER.info("LoginController: WRN! Did not find the corrosponding username and password in the database.");
				// send a message saying something was wrong with credentials
				req.getSession().setAttribute("ErrorCode","Bad username or password!");
				
				resp.sendRedirect("login.jsp");
			} else if (loginAttempt == DatabaseController.AUTH_NO_DB) {
				// login failure - no connection to back end DB!
				LOGGER.info("LoginController: WRN! Unable to connect to MySQL database.");
				// send a message saying something was wrong with the database
				req.getSession().setAttribute("ErrorCode","Unable to connect to MySQL database!");
				
				resp.sendRedirect("login.jsp");
			}
			LOGGER.info("LoginController: End of login_request");
			
		} else if (Integer.parseInt(actionDelimiter) == LOGOUT_REQUEST) {
			// we're handling a logout
			req.getSession().setAttribute("currentGarden", null);
			req.getSession().invalidate();
			
			resp.sendRedirect("login.jsp");
			
			LOGGER.info("LoginController: End of logout_reqeust");
		} else if (Integer.parseInt(actionDelimiter) == SIGNUP_REQUEST) {
			// we're handling a sign up request
			
			String username = req.getParameter("username");
			String email = req.getParameter("email");
			String psw = req.getParameter("psw"); // validation is done via JS client side!
			
			LOGGER.info("New user signup request for User:" + username + "/" + psw + " associated to " + email );
			
			// do some database stuff
			String status = DatabaseController.registerUser(username, psw, "john", "Doe", email);
			
			LOGGER.info("UserRegistration status from db is: " + status);
			
			// if user was created let the client know
			if (status.equalsIgnoreCase("TRUE")) {
				req.getSession().setAttribute("UserRegistration", "TRUE");
				
			} else {
			// otherwise let them know what went wrong
				req.getSession().setAttribute("UserRegistration", status);
			}
			
			// redirect back to signup.jsp
			resp.sendRedirect("signup.jsp");
			
			LOGGER.info("LoginController: End of signup_request");
		} else if (Integer.parseInt(actionDelimiter) == PW_RESET_REQUEST) {
			LOGGER.info("LoginController: Start of pw_reset_request");
			// we're handling a reset password request
			
			String email = req.getParameter("email");
			
			LOGGER.info("PW Reset request for email:" + email );
			
			// do some database stuff
			boolean status = DatabaseController.emailLookup(email);
			
			LOGGER.info("EmailLookup status from db is: " + status);
			
			// if account found, send an email with a unique token and record it in the DB:
			if (status) {
				LOGGER.info("TODO: send email with reset token URL");
				
				// Generate a UUID token for resetting pw: TODO: setup token with expiration and email user a URL.
				UUID token = UUID.randomUUID();
				LOGGER.info("Reset token generated: " + String.valueOf(token));
				
				// Generate an expire time for the token:
				Date expiry = new Date(System.currentTimeMillis()+10*60*1000);
				LOGGER.info("Reset token expires: " + String.valueOf(expiry));
				
				// Record token and expiration time in DB:
				if (DatabaseController.registerPwReset(email,token, expiry)) {
					// successfully recorded token and expiration time
					// TODO: set session vars?
					
					// TODO: Send email with reset URL:
					sendNewPasswordEmail(email, token);
					LOGGER.info("DBG::: reset_url: http://localhost:8080/GardenMaster/forgot.jsp?token="+token);
					
					
					// redirect back to login site for now.
					resp.sendRedirect("login.jsp");
				} else {
					// something went wrong during DB insert.. 
					req.getSession().setAttribute("ErrorCode", "SQL ERROR: unable to insert reset row.");
					resp.sendRedirect("forgot.jsp");
				}		
				
			} else {
			// otherwise let them know no known account exists
				req.getSession().setAttribute("ErrorCode", "Unknown email address.");
				resp.sendRedirect("forgot.jsp");
			}
			
			
			LOGGER.info("LoginController: End of pw_reset_request");
		} else if (Integer.parseInt(actionDelimiter) == PW_RESET) {
			LOGGER.info("LoginController: Start of pw_reset");
			// we're handling a reset password request
			String strToken = req.getParameter("token");
			UUID token = UUID.fromString(strToken);
			String newPassword = req.getParameter("password1"); //TODO: add client side validation!
			LOGGER.info("DBG:: strToken="+ strToken + " ; newPassword=" + newPassword + "; token=" + token);
			
			// check if token is expired
			if (!DatabaseController.tokenIsExpired(token)) {
				// reset password
				DatabaseController.resetPassword(token, newPassword);
				req.getSession().setAttribute("ErrorCode", "Password reset.");
				resp.sendRedirect("login.jsp");
				
			} else {
				// token isn't valid
				req.getSession().setAttribute("ErrorCode", "Unable to reset password; token is expred.");
				resp.sendRedirect("forgot.jsp");
			}
			
			LOGGER.info("LoginController: End of pw_reset");
			
		} else {
			// Unknown request!
			LOGGER.severe("LoginController: Warning! Unknown LoginController action request encountered.");
			resp.sendRedirect("login.jsp");
		}
		
		
		
	}




	private void sendNewPasswordEmail(String email, UUID token) {
		String body = "<h2>Gardenmaster Password Reset</h2>" + 
					"<p>Hello, a password reset has been requested for your account on gardenmaster. " +
					"Please click <a href=\"http://localhost:8080/GardenMaster/forgot.jsp?token=" + token + "\">Here</a> " +
					"to choose a new password.</p>";
		// TODO: add dynamic domain name handling!
		
		try {
			Postmaster.sendMail(email, "PASSWORD RESET", body);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}




	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// People should get here!
		resp.sendRedirect("login.jsp");
	}
	
	
	

}
