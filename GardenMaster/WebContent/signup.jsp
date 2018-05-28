<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>GardenMaster: Create an Account</title>
<link rel="stylesheet" href="css/signup.css">
<script>
function validation() {
   var x = document.forms["signup"]["psw"].value;
   var y = document.forms["signup"]["psw-repeat"].value;
    if (x != y) {
        alert("Passwords must match..!!");
        return false;
    }
}
</script>
</head>
<body>
<% 
	String errorCode = (String)request.getSession().getAttribute("UserRegistration"); 
	if (errorCode != null && errorCode.equalsIgnoreCase("TRUE")) {
		// announce succes 
		out.println("<script>");
		out.println("window.alert('Success!');");
		out.println("window.location.href='/GardenMaster/login.jsp';");
		out.println("</script>");
		// redirect to login
		//response.sendRedirect("login.jsp");
	} else if (errorCode != null && errorCode.equalsIgnoreCase("EXISTS")) {
		// username is taken
		out.println("<script>");
		out.println("window.alert('Error - that username is taken!');");
		out.println("</script>");
	}
	%>
<form name="signup" action="/GardenMaster/login" style="border:1px solid #ccc" method="post" onsubmit="return validation();">
  <div class="container">
    <h1>Sign Up</h1>
    <p>Please fill in this form to create an account.</p>
    <hr>
    <label for="username"><b>Username</b></label>
    <input type="text" placeholder="Enter Username" name="username" required/>

    <label for="email"><b>Email</b></label>
    <input type="text" placeholder="Enter Email" name="email" required>

    <label for="psw"><b>Password</b></label>
    <input type="password" placeholder="Enter Password" name="psw" required>

    <label for="psw-repeat"><b>Repeat Password</b></label>
    <input type="password" placeholder="Repeat Password" name="psw-repeat" required>
    
    <input type="hidden" name="action_request" value="3"/>

    <label>
      <input type="checkbox" checked="checked" name="remember" style="margin-bottom:15px"> Remember me
    </label>

    <p>By creating an account you agree to our <a href="#" style="color:dodgerblue">Terms &amp; Privacy</a>.</p>

    <div class="clearfix">
      <a href="login.jsp"><button type="button" class="cancelbtn">Cancel</button></a>
      <button type="submit" class="signupbtn">Sign Up</button>
    </div>
  </div>
</form>
</body>
</html>