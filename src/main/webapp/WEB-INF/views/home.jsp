<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Home</title>
</head>
<body>
<h1>
	Hello world!  
</h1>

<P>  The time on the server is ${serverTime}. </P>
<P>  Directory: ${directory}. </P>
<P>  This is ClubHelper Backend. </P>
<p>
<a href='person/all'>Alle Personen</a><br />
<a href='person/create?toCreate={"id":-1,"prename":"Markus","surname":"Kreth","type":"Trainer","birth":"114766442"}'>Insert Markus</a><br />
</body>
</html>
