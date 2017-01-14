<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/javascript">
		$( document ).ready( function() {
			$('#mainMenuItems')
				.empty()
				.append("<li><a href=\"#deletePerson\" class=\"ui-btn ui-icon-delete ui-btn-icon-left\">Löschen</a></li>")
				.append("<li><a href=\"#addContact\" class=\"ui-btn ui-icon-plus ui-btn-icon-left\">Telefon/Mail</a></li>");
		});
		
		function deletePerson() {
			alert("deletePerson ${Person.prename} ${Person.surname}");
		}
		
		function addContact() {
			alert("addContact");
		}
</script>

Name:<br />
${Person.prename} ${Person.surname}<br/>
Geburtstag:  <fmt:formatDate value="${Person.birth}" pattern="dd.MM.yyyy" /><br />

<h4>Kontakte:</h4>

<ul data-role="listview" data-inset="true" data-filter="false">
<c:forEach var="item" items="${ContactList}">
<c:choose>
<c:when test="${item.type == 'Email'}">
	<li>
	<div data-role="controlgroup" data-type="horizontal">
	<a href="mailto:${item.value}"  data-icon="mail" data-inline="true" data-role="button" data-inline="true">${item.value}</a>
	</div>
	</li>
</c:when>
<c:when test="${item.type == 'Mobile'}">
	<li>
	<div data-role="controlgroup" data-type="horizontal">
	<a href="tel:${item.value}" data-icon="phone" data-role="button" data-inline="true">${item.value}</a>
	<a href="sms:${item.value}" data-icon="mail" data-role="button" data-inline="true" data-iconpos="notext"></a>
	</div>	
	</li>
</c:when>
<c:otherwise>
	<li>
	<div data-role="controlgroup" data-type="horizontal">
	<a href="tel:${item.value}" data-icon="phone" data-mini="true" data-role="button" data-inline="true">${item.value}</a>
	</div>	
	</li>
</c:otherwise>
</c:choose>
</c:forEach>
</ul>

<h4>Adresse:</h4>
<c:forEach var="item" items="${AdressList}">
	${item.adress1}<br/>
	${item.adress2}<br/>
	${item.plz} ${item.city}<br/>
	<br/> 
</c:forEach>

<h4>Beziehungen:</h4>
<ol data-role="listview" data-inset="true" data-filter="false">
	<c:forEach var="item" items="${PersonRelativeList}">
		<li><a href="<s:url value="/person/${item.toPerson.id}"/>" data-role="button">${item.relation}: ${item.toPerson.prename} ${item.toPerson.surname}</a></li>
	</c:forEach>
</ol>
