<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
			<p>Personen</p>
			<ul data-role="listview" data-inset="true" data-filter="true">
				<c:forEach var="person" items="${PersonList}">
						<li><a href="<s:url value="/person/${person.id}" />">${person.prename} ${person.surname}</a></li>
				</c:forEach>
			</ul>

<script type="text/javascript">
		$( document ).ready( function() {
			var mainMenuItems = $('#mainMenuItems');
			mainMenuItems.empty();
			
		});	
</script>