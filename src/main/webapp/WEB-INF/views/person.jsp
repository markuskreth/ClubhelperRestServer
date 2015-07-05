<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
Name: ${Person.prename}  ${Person.surname}<br/>
Typ:  ${Person.type}<br />
Geburtstag:  ${Person.birth}
<h4>Kontakte:</h4>
		<c:forEach var="item" items="${ContactList}">
			${item.type}: ${item.value}<br/> 
		</c:forEach>
		
<h4>Adresse:</h4>
		<c:forEach var="item" items="${AdressList}">
			${item.type}: ${item.value}<br/> 
		</c:forEach>
<h4>Beziehungen:</h4>
		<c:forEach var="item" items="${RelativeList}">
			${item.type}: ${item.value}<br/> 
		</c:forEach>