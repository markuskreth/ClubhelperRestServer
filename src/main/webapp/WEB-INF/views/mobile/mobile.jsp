<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<!doctype html>
<html>
<head>
<title>Clubhelper Mobile</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="http://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.css" />
<link rel="stylesheet"
	href=<c:url value='/resources/css/custom.css' /> />
<script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
<script src=<c:url value='/resources/js/moment-with-locales.min.js' />></script>
<script src=<c:url value='/resources/js/personuiajax.js' />></script>
<script src=<c:url value='/resources/js/personwritesupport.js' />></script>
<script src=<c:url value='/resources/js/personlistsupport.js' />></script>
<script
	src="http://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.js"></script>
<script type="text/javascript">
var storeObject = {
	    personId: null,
	    person : null,
	    contacts : null,
	    adresses : null,
	    ralations : null
}

var baseUrl = location.protocol + '//' + location.host + <c:url value='/' />;

</script>
</head>
<body>
	<div data-role="page" id="personList">
		<div data-role="header" data-position="fixed">
			<h1>Clubhelper Mobile</h1>
			<a href="#personListMenu" data-rel="popup" data-role="button"
				data-icon="bars" data-mini="true" data-rel="popup"></a>
			<div data-role="popup" id="personListMenu">
				<ul id="mainMenuItems">
					<li><a href="#" onclick="printPhoneList2()" data-rel="popup" class="ui-btn ui-icon-bulletes ui-btn-icon-left">Listen</a></li>
				</ul>
			</div>
		</div>
		<!-- /header -->

		<div role="main" class="ui-content">
			<div>
				<div id="content" class="width25 floatRight leftColumn">
					<div class="width75 floatLeft">
						<div class="gradient">	
							<div data-role="navbar">
							  <ul>
							    <li><a href="#" onclick="addPerson()" class="ui-btn ui-icon-plus">Hinzufügen</a></li>
							    <li><a href="#" onclick="printPhoneList()" class="ui-btn ui-icon-bulletes">Listen</a></li>
							  </ul>
							</div>
							<p>Personen</p>
							<ul data-role="listview" data-inset="true" data-filter="true">
								<c:forEach var="person" items="${PersonList}">
										<li><a href="#" onclick="showPerson(${person.id})">${person.prename} ${person.surname}</a></li>
								</c:forEach>
							</ul>	
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- /content -->

		<div data-role="footer" data-position="fixed" data-mini="true">
			Copyright Markus Kreth - MTV Groß-Buchholz</div>
		<!-- /footer -->

	</div>
	<!-- /page -->
	
	<div data-role="page" id="personDetails">
		<div data-role="header" data-position="fixed" data-add-back-btn="true">
			<h1>Clubhelper Mobile</h1>
<!-- 			<a href="#mainMenu" data-rel="popup" data-role="button" -->
<!-- 				data-icon="bars" data-mini="true" data-rel="popup"></a> -->
<!-- 			<div data-role="popup" id="mainMenu"> -->
<!-- 				<ul id="mainMenuItems"> -->
<!-- 					<li><a href="#" onclick="printPhoneList" data-rel="popup" class="ui-btn ui-icon-bulletes ui-btn-icon-left">Listen</a></li> -->
<!-- 				</ul> -->
<!-- 			</div> -->
		</div>
		<!-- /header -->

		<div role="main" class="ui-content">
			<div>
				<div id="content" class="width25 floatRight leftColumn">
					<div class="width75 floatLeft">
						<div class="gradient">	
							<div data-role="navbar">
							  <ul>
							    <li><a href="#" onclick="addPersonDetail()" class="ui-btn ui-icon-plus">Hinzufügen</a></li>
							  </ul>
							</div>
							<div id="personDetailPerson"></div>
							<div data-collapsed="false">
							<h4>Kontakte:</h4>
								<ul id="personDetailContacts" data-role="listview" data-inset="true" data-filter="false">
								</ul>
							</div>
							<div id="personDetailAdresses" data-role="collapsible">
							<h4>Adresse:</h4>
							</div>
							<div id="personDetailRelations" data-role="collapsible">
							<h4>Beziehungen:</h4>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- /content -->

		<div data-role="footer" data-position="fixed" data-mini="true">
			Copyright Markus Kreth - MTV Groß-Buchholz</div>
		<!-- /footer -->

	</div>
	<!-- /page -->
<div id="printLists" data-role="popup">
	<a href="#" data-role="button" onclick="printPhoneList2">Telefonliste</a>
</div>
</body>
</html>