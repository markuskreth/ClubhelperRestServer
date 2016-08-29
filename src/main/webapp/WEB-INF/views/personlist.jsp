<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<c:forEach var="person" items="${PersonList}">
	<a href="<s:url value="/person/${person.id}" />">${person.prename} ${person.surname}</a><br/> 
</c:forEach>

Updated!