<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="model.Garden"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>GardenMaster Home</title>
<link rel="stylesheet" href="css/style.css">
</head>
<body>
<% Garden g = (Garden) request.getSession().getAttribute("currentGarden");

if (g == null) {
	// redirect to login or sign up page!
	response.sendRedirect("login.jsp");
} else {
//	Debug or set session vars now!

}

%>

<!--  footer content -->
<div id="footer">
<form method="post" action="/GardenMaster/login">
	<input type="hidden" name="action_request" value="2"/>
	<button type="submit" onclick="return confirm('Are you sure?')">Logout</button>
</form>
</div>

</body>
</html>