<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@page import="de.kreth.encryption.Encryptor"%>
<%@page import="java.util.Date"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Club Helper Web</title>
<script type="text/javascript" src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
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
				} else if (client.readyState == 4) {
					var responseText = client.responseText.trim();
					$("body").html(responseText);
					alert("Fehler Status " + client.status + ": " + client.statusText);
				}
			};
			
			client.send(null);
			
			return false;
		}
</script>

<%!
	public Date theDate = new Date();

	public String calcToken(String userAgent) {
		Encryptor enc = new Encryptor();
		String token = "";
		try {
			token = enc.encrypt(theDate, userAgent);
		} catch (Exception e) {
			
		}
		return token;
	}
%>

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
			var text = person.prename + " " + person.surname
					+ "\n gespeichert. ID=" + person.id;
			alert(text);
		} else if (client.readyState == 4) {
			alert("Fehler Status " + client.status + ": " + client.statusText);
		}
	};

	client.send(data);

}
</script>
<link
	href="http://fonts.googleapis.com/css?family=Open+Sans:400,300,600"
	rel="stylesheet" type="text/css" />
<link href='http://fonts.googleapis.com/css?family=Abel|Satisfy'
	rel='stylesheet' type='text/css' />
<link href="<c:url value='/resources/0906/css/style.css' />"
	rel="stylesheet" type="text/css" media="screen" />
</head>
<body>
	<div id="content">
		<div id="header">
			<div id="title">
				<tiles:insertAttribute name="top"></tiles:insertAttribute>
			</div>
		</div>
		<div id="page">
			<div id="content" class="width25 floatRight leftColumn">

				<div class="width75 floatLeft">
					<div class="gradient">
						<tiles:insertAttribute name="content"></tiles:insertAttribute>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div id="footer">
		<center>Design by <a href="http://www.dreamtemplate.com" title="Website Templates" target="_blank">Website templates</a></center>
	</div>
</body>
</html>