<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
Name: ${Person.prename}  ${Person.surname}<br/>
Typ:  ${Person.type}<br />
Geburtstag:  <fmt:formatDate value="${Person.birth}" pattern="dd.MM.yyyy" /><br />
Created:  <fmt:formatDate value="${Person.created}" pattern="dd.MM.yyyy HH:mm:ss" /><br />
Changed:  <fmt:formatDate value="${Person.changed}" pattern="dd.MM.yyyy HH:mm:ss" /><br />

<h4>Kontakte:</h4>
<c:forEach var="item" items="${ContactList}">
<c:choose>
<c:when test="${item.type == 'Email'}">
	${item.type}: <a href="mailto:${item.value}">${item.value}</a><br/>
</c:when>
<c:otherwise>
	${item.type}: <a href="tel:${item.value}">${item.value}</a><br/>
</c:otherwise>
</c:choose>
</c:forEach>

<h4>Adresse:</h4>
<c:forEach var="item" items="${AdressList}">
	${item.adress1}<br/>
	${item.adress2}<br/>
	${item.plz} ${item.city}<br/> <br/> 
</c:forEach>

<h4>Beziehungen:</h4>
<c:forEach var="item" items="${PersonRelativeList}">
	<a href="<s:url value="/person/${item.toPerson.id}" />">${item.relation}: ${item.toPerson.prename} ${item.toPerson.surname}</a><br/>
</c:forEach>


<div data-role="popup" id="popupMenu">
    <p>Popup für ${Person.prename}  ${Person.surname}</p>
</div>