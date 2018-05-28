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
		} else {
			// Unknown request!
			LOGGER.severe("LoginController: Warning! Unknown LoginController action request encountered.");
		}
		
	}
	
	
	

}
