<%@page import="java.io.Writer"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf8">
<title>Welcome to GardenMaster</title>
<link rel="stylesheet" href="css/style.css">
</head>
<body>
<div id = "header">
<a href="login.jsp">Welcome to GardenMaster!</a></div>

<div id = "welcomeMenu">

<form method="post" action="/GardenMaster/login">
<div id = "welcomeMenuItem">
	<p>Welcome! Please sign in:</p>
	<label for="username"><b>Username:</b></label>
	<input type="text" placeholder="Enter Username" name="username" required/>
	<br>
	<label for="password"><b>Password:</b></label>
	<input type="password" placeholder="Enter Password" name="password" required/>
	<br>
	<input type="hidden" name="action_request" value="1"/>
	<button type="submit">Login</button>
	<% 
	String errorCode = (String)request.getSession().getAttribute("ErrorCode"); 
	if (errorCode != null) {
		// display error then clear it for next render.
		out.println("<p style='color:red;'>" + errorCode + "</p>");
		request.getSession().setAttribute("ErrorCode", null);	
	}	
	%>
	<label>
		<input type="checkbox" checked="checked" name="remember">Remember me
	</label>

</div>



<br>
<div id = "welcomeMenuItem">
	<p>Forgot your password?</p><a href="forgot.jsp">Click Here</a>
    <br>
	<p>Not a member yet?</p><a href="signup.jsp">Click here to create an account.</a><br>
</div>
</form>

</div>

<%@include file = "/parts/footer.jsp" %>
</body>
</html>