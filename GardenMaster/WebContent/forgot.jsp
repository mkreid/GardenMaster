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
<div id = "welcomeMenuItem">
<form method="post" action="/GardenMaster/login">
	<p>Please provide your account email address and we'll send you instructions describing how to reset your password.</p>
	<label for="email"><b>Email Address:</b></label>
	<input type="text" placeholder="user@domain.com" name="email" required/>
	<br>
	<input type="hidden" name="action_request" value="${LoginController.PW_RESET_REQUEST}"/>
	<button type="submit">Login</button>
	<% 
	String errorCode = (String)request.getSession().getAttribute("ErrorCode"); 
	if (errorCode != null) {
		// display error then clear it for next render.
		out.println("<p style='color:red;'>" + errorCode + "</p>");
		request.getSession().setAttribute("ErrorCode", null);	
	}	
	%>
</form>
</div>
<p> Token ID = <%= request.getParameter("token") %></p>


<!-- Bottom branding -->
<%@include file = "/parts/footer.jsp" %>
</body>
</html>