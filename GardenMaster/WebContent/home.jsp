<%@page import="model.Garden"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>GardenMaster Home</title>
<%@include file="/parts/header.jsp" %>
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

<!-- Top branding -->
<%@include file="/parts/logo.jsp" %>

<!-- page content -->

<!--  footer content -->
<div id="footer">
<form method="post" action="/GardenMaster/login">
	<input type="hidden" name="action_request" value="2"/>
	<button type="submit" onclick="return confirm('Are you sure?')">Logout</button>
</form>
</div>

<!-- Bottom branding -->
<%@include file = "/parts/footer.jsp" %>
</body>
</html>