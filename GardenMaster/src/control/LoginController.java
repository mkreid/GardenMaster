package control;

import java.io.IOException;
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
	
	public static final int LOGIN_REQUEST = 1;
	public static final int LOGOUT_REQUEST = 2;
	public static final int SIGNUP_REQUEST = 3;
	
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
			
			
			boolean loginAttempt = DatabaseController.authenticateUser(username, password);
			
			if (loginAttempt) {
				// successful!
				LOGGER.info("LoginController: found corrosponding username and password in the database");
				Garden g = new Garden();
				
				req.getSession().setAttribute("currentGarden", g);
				
				resp.sendRedirect("home.jsp");
			} else {
				// login failure
				LOGGER.info("LoginController: WRN! Did not find the corrosponding username and password in the database.");
				// send a message saying something was wrong with credentials
				req.getSession().setAttribute("ErrorCode","Bad username or password!");
				
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
		} else {
			// Unknown request!
			LOGGER.severe("LoginController: Warning! Unknown LoginController action request encountered.");
			resp.sendRedirect("login.jsp");
		}
		
		
		
	}




	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// People should get here!
		resp.sendRedirect("login.jsp");
	}
	
	
	

}
