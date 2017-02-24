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
	href=<c:url value='/resources/css/jquery.mobile-1.4.5.min.css' /> />
<link rel="stylesheet" href=<c:url value='/resources/css/jquery.mobile.datepicker.css' /> />
<link rel="stylesheet" href=<c:url value='/resources/css/custom.css' /> />
<script src=<c:url value='/resources/js/jquery-1.11.1.min.js' />></script>
<script src=<c:url value='/resources/js/moment-with-locales.min.js' />></script>
<script src=<c:url value='/resources/js/jquery.mobile-1.4.5.min.js' />></script>
<script src=<c:url value='/resources/js/jquery.mobile.datepicker.js' />></script>
<script src=<c:url value='/resources/js/person.js' />></script>
<script src=<c:url value='/resources/js/mobile_business.js' />></script>
<script src=<c:url value='/resources/js/mobile_edit_business.js' />></script>
<script type="text/javascript">
var baseUrl = location.protocol + '//' + location.host + <c:url value='/' />;
</script>
<style>
.ui-icon-person {
	background: url(resources/img/person.png) 100% 100% no-repeat;
	/* 	background-size: 30px 30px; */
	width: 18px;
	height: 18px;
	box-shadow: none;
	-webkit-box-shadow: none;
	margin: 0 !important;
}
</style>
</head>
<body>
	<div data-role="page" id="personList">
		<div data-role="header" data-position="fixed">
			<h1>Clubhelper Mobile</h1>
			<a href="#personListMenu" data-rel="popup" data-role="button"
				data-icon="bars" data-mini="true" data-rel="popup"></a>
			<div data-role="popup" id="personListMenu">
				<ul id="mainMenuItems">
					<li><a href="#" onclick="printPhoneList2()" data-rel="popup"
						class="ui-btn ui-icon-bulletes ui-btn-icon-left">Listen</a></li>
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
									<li><a href="#" onclick="addPerson()"
										class="ui-btn ui-icon-plus">Hinzuf¸gen</a></li>
									<li><a href="#" onclick="printPhoneList()"
										class="ui-btn ui-icon-bulletes">Listen</a></li>
								</ul>
							</div>
							<p>Personen</p>
							<ul data-role="listview" data-inset="true" data-filter="true">
								<c:forEach var="person" items="${PersonList}">
									<li><a href="#" onclick="showPerson(${person.id})">${person.prename}
											${person.surname}</a></li>
								</c:forEach>
							</ul>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- /content -->

		<div data-role="footer" data-position="fixed" data-mini="true">
			Copyright Markus Kreth - MTV Groﬂ-Buchholz</div>
		<!-- /footer -->

	</div>
	<!-- /page -->

	<div data-role="page" id="personDetails">
		<div data-role="header" data-position="fixed" data-add-back-btn="true">
			<h1>Clubhelper Mobile</h1>
		</div>
		<!-- /header -->

		<div role="main" class="ui-content">
			<div>
				<div id="content" class="width25 floatRight leftColumn">
					<div class="width75 floatLeft">
						<div class="gradient">
							<div data-role="navbar">
								<ul>
									<li><a href="#" onclick="editPerson()"
										data-role="button" data-icon="edit" data-theme="b" data-inline="true">Bearbeiten</a></li>
								</ul>
							</div>
							<div id="personDetailPerson"></div>
							<div data-collapsed="false" data-role="collapsible">
								<h4>Kontakte:</h4>
								<ul id="personDetailContacts" data-role="listview"
									data-inset="true" data-filter="false">
								</ul>
							</div>
							<div id="collapsibleAdresses" data-role="collapsible">
								<h4>Adresse:</h4>
								<ul id="personDetailAdresses" data-role="listview"
									data-inset="true" data-filter="false">
								</ul>
							</div>
							<div id="collapsibleRelations" data-role="collapsible">
								<h4>Beziehungen:</h4>
								<ul id="personDetailRelations" data-role="listview"
									data-inset="true" data-filter="false">
								</ul>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- /content -->

		<div data-role="footer" data-position="fixed" data-mini="true">
			Copyright Markus Kreth - MTV Groﬂ-Buchholz</div>
		<!-- /footer -->

	</div>
	<!-- /page -->
	
	<div data-role="page" id="personEdit">
		<div data-role="header" data-position="fixed" data-add-back-btn="true">
			<h1>Clubhelper Mobile</h1>
		</div>
		<!-- /header -->

		<div role="main" class="ui-content">
			<div>
				<div id="content" class="width25 floatRight leftColumn">
					<div class="width75 floatLeft">
						<div class="gradient">
							<div id="personDetailPersonEdit"></div>
							<div data-role="header" data-position="inline">
								<h4>Kontakte:</h4>
								<a href="#" onclick="addContact();"
								class="ui-btn ui-btn-right ui-icon-plus ui-shadow ui-corner-all ui-btn-icon-notext"></a>
							</div>
							<ul id="personDetailContactsEdit" data-role="listview"
								data-inset="true" data-filter="false">
							</ul>
							<div data-role="header" data-position="inline">
								<h4>Adresse:</h4>
								<a href="#" onclick="addAdress();"
								class="ui-btn ui-btn-right ui-icon-plus ui-shadow ui-corner-all ui-btn-icon-notext"></a>
							</div>
							<ul id="personDetailAdressesEdit" data-role="listview"
								data-inset="true" data-filter="false">
							</ul>
							<div data-role="header" data-position="inline">
								<h4>Beziehungen:</h4>
								<a href="#" onclick="addRelation();"
								class="ui-btn ui-btn-right ui-icon-plus ui-shadow ui-corner-all ui-btn-icon-notext"></a>
							</div>
							<ul id="personDetailRelationsEdit" data-role="listview"
								data-inset="true" data-filter="false">
							</ul>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- /content -->

		<div data-role="footer" data-position="fixed" data-mini="true">
			Copyright Markus Kreth - MTV Groﬂ-Buchholz</div>
		<!-- /footer -->

	</div>
	<!-- /page -->
	
	
	<div id="printLists" data-role="popup">
		<a href="#" data-role="button" onclick="printPhoneList2">Telefonliste</a>
	</div>
	
	<div id="editDialog" data-role="page">
		
	</div>
</body>
</html>