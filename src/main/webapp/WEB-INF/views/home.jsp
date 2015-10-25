<%@page import="java.util.Date"%>
<%@page import="de.kreth.clubhelperbackend.aspects.Encryptor"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<h2>
	This is ClubHelper Backend.
</h2>
<P>  The time on the server is ${serverTime}. </P>
<P>  Path to current server directory: ${directory}. </P>
<p>
<a href='person/'>Alle Personen</a><br />
<a href='javascript: sendPost("person/", JSON.stringify({"id":-1,"prename":"Markus","surname":"Kreth","type":"Trainer","birth":"21/08/1973 08:00:00.0 +0100"}))'>Insert Markus</a><br />
</p>

