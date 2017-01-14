<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<script type="text/javascript">
		$( document ).ready( function() {
			$('#mainMenuItems')
				.empty()
				.append("<li><a href=\"#printLists\" data-rel=\"popup\" class=\"ui-btn ui-icon-bulletes ui-btn-icon-left\">Listen</a></li>");
		});
		function printLists() {
			alert("PrintLists!")
		}
		
		function addPerson() {
			alert("AddPerson");
		}
</script>
<div data-role="navbar">
  <ul>
    <li><a href="#addPerson" class="ui-btn ui-icon-plus">Hinzufügen</a></li>
    <li><a href="#printLists" class="ui-btn ui-icon-bulletes">Listen</a></li>
  </ul>
</div>
	
			<p>Personen</p>
			<ol data-role="listview" data-inset="true" data-filter="true">
				<c:forEach var="person" items="${PersonList}">
						<li><a href="<s:url value="/person/${person.id}" />">${person.prename} ${person.surname}</a></li>
				</c:forEach>
			</ol>
