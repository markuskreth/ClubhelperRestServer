<%@page import="de.kreth.clubhelperbackend.aspects.Encryptor"%>
<%@page import="java.util.Date"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Club Helper Web</title>

<script type="text/javascript"
	src="http://code.jquery.com/jquery-1.10.1.min.js"></script>
<script type="text/javascript">

		$( document ).ready( function() {
			$('a').click(sendWithHeader);
		});
		
		function sendWithHeader() {
			var text = $(this).text();
			var href = $(this).attr("href") + "?ajax=true";

			var client = new XMLHttpRequest();
			client.open("GET", href);
			client.setRequestHeader("localtime", "<%=theDate.getTime()%>");
			client.setRequestHeader("token", "<%=calcToken(request.getHeader("user-agent"))%>");
			client.onreadystatechange = function() {
				if (client.readyState == 4 && client.status == 200) {
					var responseText = client.responseText.trim();
					$("body").html(responseText);
					$('a').click(sendWithHeader);
				}
			};
			
			client.send(null);
			
			return false;
		}
</script>

<%!public Date theDate = new Date();

	public String calcToken(String userAgent) {
		Encryptor enc = new Encryptor();
		String token = "";
		try {
			token = enc.encrypt(theDate, userAgent);
		} catch (Exception e) {

		}
		return token;
	}%>
	
<script type="text/javascript">
function sendPost(uri, data)
{
	var href = uri;

	var client = new XMLHttpRequest();
	client.open("POST", href);
	client.setRequestHeader("localtime", "<%=theDate.getTime()%>");
	client.setRequestHeader("token", "<%=calcToken(request.getHeader("user-agent"))%>");
	client.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	client.onreadystatechange = function() {
		if (client.readyState == 4 && client.status == 200) {
			var person = JSON.parse(client.responseText.trim());
			var text = person.prename + " " + person.surname + "\n gespeichert. ID=" + person.id;
			alert(text);
		}
	};
	
	client.send(data);
	
}
</script>
</head>
<body>
	<tiles:insertAttribute name="top"></tiles:insertAttribute>
	<tiles:insertAttribute name="content"></tiles:insertAttribute>
</body>
</html>