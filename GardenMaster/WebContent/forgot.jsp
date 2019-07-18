<%@page import="control.LoginController"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Password Recovery</title>
<%@include file="/parts/header.jsp" %>
</head>
<body>
<!-- Top branding -->
<%@include file="/parts/logo.jsp" %>


<!--  Content! -->
<% String t = request.getParameter("token"); // get the token, if sent %> 
<% if (t == null) {
	// show the find email address form
	out.println("<div id = \"welcomeMenuItem\">");
	out.println("<form method=\"post\" action=\"/GardenMaster/login\">");
	out.println("<p>Please provide your account email address and we'll send you instructions describing how to reset your password.</p>");
	out.println("<label for=\"email\"><b>Email Address:</b></label>");
	out.println("<input type=\"text\" placeholder=\"user@domain.com\" name=\"email\" required/>");
	out.println("<br>");
	out.println("<input type=\"hidden\" name=\"action_request\" value=\"" + LoginController.PW_RESET_REQUEST + "\"/>");
	out.println("<button type=\"submit\">Find me!</button>");
	String errorCode = (String)request.getSession().getAttribute("ErrorCode"); 
	if (errorCode != null) {
		// display error then clear it for next render.
		out.println("<p style='color:red;'>" + errorCode + "</p>");
		request.getSession().setAttribute("ErrorCode", null);	
	}
	out.println("</form>");
	out.println("</div>");
} else {
	// show the password reset form
	out.println("<div id = \"welcomeMenuItem\">");
	out.println("<form method=\"post\" action=\"/GardenMaster/login\">");
	out.println("<p>Please enter your new password and we'll update your account.</p>");
	out.println("<label for=\"password1\" style=\"padding-right:2em\"><b>New Password:</b></label>");
	out.println("<input type=\"password\" name=\"password1\" required/>");
	out.println("<br>");
	out.println("<label for=\"password2\" style=\"padding-right:1em\"><b>Repeat Password:</b></label>"); 
	out.println("<input type=\"password\" name=\"password2\" required/>");
	out.println("<br><br>");
	out.println("<input type=\"hidden\" name=\"action_request\" value=\"" + LoginController.PW_RESET + "\"/>");
	out.println("<input type=\"hidden\" name=\"token\" value=\"" + t + "\"/>");
	out.println("<button type=\"submit\">Reset!</button>");
	String errorCode = (String)request.getSession().getAttribute("ErrorCode");
	if (errorCode != null) {
		// display error then clear it for next render.
		out.println("<p style='color:red;'>" + errorCode + "</p>");
		request.getSession().setAttribute("ErrorCode", null);	
	}
	out.println("</form>");
	out.println("</div>");
}%>



<!-- Bottom branding -->
<%@include file = "/parts/footer.jsp" %>
</body>
</html>