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
<a href='person/update?toUpdate={"id":1,"prename":"Markus3","surname":"Kreth2","type":"Trainer","birth":"1432960775"}'>Update Person 1</a><br />
<a href='person/create?toCreate={"id":-1,"prename":"Markus","surname":"Kreth","type":"Trainer","birth":"114766442"}'>Insert Markus</a><br />
<a href='person/delete?toDelete={"id":2,"prename":"Markus","surname":"Kreth","type":"Trainer","birth":"114766442"}'>Delete Markus2</a><br />
</body>
</html>
