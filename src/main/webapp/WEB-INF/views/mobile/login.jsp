<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Anmeldung</title>

<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href=<c:url value='/resources/css/jquery.mobile-1.4.5.min.css' /> />
	
<script src=<c:url value='/resources/js/jquery-1.11.1.min.js' />></script>
<script src=<c:url value='/resources/js/jquery.mobile-1.4.5.min.js' />></script>

</head>
<body onload='document.loginForm.username.focus();'>

	<div id="login-box" data-role="page">

	<h1>Clubhelper MTV Groß-Buchholz Trampolinturnen</h1>

		<h2>Anmeldung</h2>

		<c:if test="${not empty error}">
			<h3>Error: </h3>
			<div class="error">${error}</div>
		</c:if>
		<c:if test="${not empty msg}">
			<h3>Msg: </h3>
			<div class="msg">${msg}</div>
		</c:if>

		<form name='loginForm'
		  action="login" method='POST'>

		  <table>
			<tr>
				<td>Anmeldename:</td>
				<td><input type='text' name='username' value=''></td>
			</tr>
			<tr>
				<td>Password:</td>
				<td><input type='password' name='password' /></td>
			</tr>
			<tr>
				<td>Anmeldung speichern:</td>
				<td><input type="checkbox" name="remember-me" /></td>
			</tr>
			<tr>
				<td colspan='2'><input name="submit" type="submit"
					value="Anmelden" /></td>
			</tr>
		  </table>

<%-- 		  <input type="hidden" name="${_csrf.parameterName}" --%>
<%-- 			value="${_csrf.token}" /> --%>

		</form>
	</div>

</body>
</html>