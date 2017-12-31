<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%><!doctype html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<html lang="de">
<head>
<title>Clubhelper Mobile</title>
<meta charset="utf-8">
<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1">

<link rel="stylesheet" href=<c:url value='/resources/css/custom.css' /> />
<link rel="stylesheet" type="text/css" href="//fonts.googleapis.com/css?family=Inconsolata" />
<link rel="stylesheet" href=<c:url value='/resources/css/clndr.css' /> />

<link rel="stylesheet" href=<c:url value='/resources/js/libs/jquery.mobile-1.4.5.min.css' /> />

<!-- <link rel="stylesheet/less" type="text/css" href=<c:url value='/resources/css/clndr.less' /> /> -->
<%-- <script src=<c:url value='/resources/js/less.js' />></script> --%>
<%-- <script src="//cdnjs.cloudflare.com/ajax/libs/less.js/2.7.2/less.min.js"></script> --%>

<script src=<c:url value='/resources/js/libs/jquery-1.11.1.min.js' />></script>
<script src=<c:url value='/resources/js/libs/jquery.mobile-1.4.5.min.js' />></script>

<script src=<c:url value='/resources/js/libs/moment-with-locales.min.js' />></script>
<script charset="utf-8" src=<c:url value='/resources/js/libs/log4javascript.js' />></script>
<script charset="utf-8" src=<c:url value='/resources/js/libs/clndr.min.js' />></script>
<script charset="utf-8" src=<c:url value='/resources/js/libs/underscore-min.js' />></script>

<script charset="utf-8" src=<c:url value='/resources/js/general.js' />></script>
<script charset="utf-8" src=<c:url value='/resources/js/ajax_base.js' />></script>
<script charset="utf-8" src=<c:url value='/resources/js/storage.js' />></script>
<script charset="utf-8" src=<c:url value='/resources/js/jumpheights.js' />></script>
<script charset="utf-8" src=<c:url value='/resources/js/person.js' />></script>
<script charset="utf-8" src=<c:url value='/resources/js/mobile_business.js' />></script>
<script charset="utf-8" src=<c:url value='/resources/js/mobile_calendar.js' />></script>
<script charset="utf-8" src=<c:url value='/resources/js/mobile_edit_business.js' />></script>
<script type="text/javascript">
	var baseUrl = location.protocol + '//' + location.host
			+ <c:url value='/' />;
	var log = log4javascript.getDefaultLogger();
	log4javascript.setEnabled(false);
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
	<div data-role="page" id="personListPage">
		<div data-role="header" data-position="fixed">
			<h1>Clubhelper Mobile</h1>
			<a href="#personListMenu" data-rel="popup" data-role="button"
				data-icon="bars" data-rel="popup"></a>
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
										class="ui-btn ui-btn-icon-left ui-icon-plus">Hinzufügen</a></li>
									<li><a href="#" onclick="showCalendar()"
										class="ui-btn ui-btn-icon-left ui-icon-calendar">Kalender</a></li>
								</ul>
							</div>
							<div data-role="navbar">
								<ul>
									<li class="ui-field-contain">
										<select name="flip-checkbox-attendance" id="flip-checkbox-attendance" data-mini="true">
											<option value="loadPersonList">Personen</option>
											<option value="showAttendanceList">Anwesenheit</option>
										</select>
									</li>
									<li id="sendAttendance"><a href="#" onclick="sendAttendance()"
										class="ui-btn ui-btn-icon-left ui-icon-action">Abschicken</a></li>
								</ul>
							</div>
							<div>
								<ul id="personList" data-role="listview" data-inset="true"
								data-filter="true">
								</ul>
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

	<div data-role="page" id="personDetails">
		<div data-role="header" data-position="fixed" data-add-back-btn="true"
			data-back-btn-text="Zurück">
			<h1>Clubhelper Mobile</h1>
		</div>
		<!-- /header -->

		<div role="main" class="ui-content">
			<div>
				<div id="contentDetail" class="width25 floatRight leftColumn">
					<div class="width75 floatLeft">
						<div class="gradient">
							<div data-role="navbar">
								<ul>
									<li><a href="#" onclick="editPerson()"
										id="showGroupButton" data-role="button" data-icon="edit"
										data-theme="b" data-transition="flip" data-inline="true">Bearbeiten</a></li>
									<li><a href="#" onclick="showGroups()" id="showGroupButton"
										data-role="button" data-transition="flip" data-icon="edit" data-theme="b"
										data-inline="true">Gruppen</a></li>
									<li><a href="#" onclick="showJumpHeights()" id="showJumpHeights"
										data-role="button" data-transition="flip" data-icon="edit" data-theme="b"
										data-inline="true">Höhenmessung</a></li>
								</ul>
							</div>
							<div id="personDetailPerson">
								<p>
									Name:
								</p>
								<p>
									<span class="personPrename">dummy1</span> <span class="personSurname">dummy2</span>
								</p>
								<p>
									Geburtstag:<span id="personBirthday"></span> Alter: <span
										id="personAge"></span>
								</p>
							</div>
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
			Copyright Markus Kreth - MTV Groß-Buchholz</div>
		<!-- /footer -->

	</div>
	<!-- /page -->

	<div data-role="page" id="personEdit">
		<div data-role="header" data-position="fixed" data-add-back-btn="true"
			data-back-btn-text="Zurück">
			<h1>Clubhelper Mobile</h1>
		</div>
		<!-- /header -->

		<div role="main" class="ui-content">
			<div>
				<div id="contentEdit" class="width25 floatRight leftColumn">
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
							
							<div data-role="navbar">
								<ul>
									<li><a href="#" onclick="deletePerson()"
										class="ui-btn ui-icon-delete">Löschen</a></li>
								</ul>
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

	<div data-role="page" id="personJumpHeight">
		<div data-role="header" data-position="fixed" data-add-back-btn="true"
			data-back-btn-text="Zurück">
			<h1>Clubhelper Mobile</h1>
		</div>
		<!-- /header -->

		<div role="main" class="ui-content">
			<div>
				<div id="contentJumpHeight" class="width25 floatRight leftColumn">
					<div class="width75 floatLeft">
						<div class="gradient">
							<div data-role="navbar">
								<ul>
									<li><a href="#" onclick="addFlightTime()"
										data-role="button" data-icon="edit"
										data-theme="b" data-inline="true">Neuer Wert</a></li>
<!-- 									<li><a href="#" onclick=""  -->
<!-- 										data-role="button" data-icon="edit" data-theme="b" -->
<!-- 										data-inline="true">???</a></li> -->
								</ul>
							</div>
							<div id="personJumpHeights">
								<p>
									Name: <span class="personPrename">dummy1</span> <span class="personSurname">dummy2</span>
								</p>
							</div>
							<div data-collapsed="false" data-role="collapsible">
								<h4>Übungen:</h4>
								<ul id="personJumpHeightTasks" data-role="listview"
									data-inset="true" data-filter="false">
								</ul>
							</div>
<!-- 							<div data-collapsed="false" data-role="collapsible"> -->
<!-- 								<h4>Daten:</h4> -->
<!-- 								<ul id="personDetailAdresses" data-role="listview" -->
<!-- 									data-inset="true" data-filter="false"> -->
<!-- 								</ul> -->
<!-- 							</div> -->
						</div>
					</div>
				</div>
			</div>
		</div> <!-- /content -->

		<div data-role="footer" data-position="fixed" data-mini="true">
			Copyright Markus Kreth - MTV Groß-Buchholz</div>
		<!-- /footer -->

	</div>
	<!-- /page -->

	<div data-role="page" id="calendarpage">
		<div data-role="header" data-position="fixed" data-add-back-btn="true"
			data-back-btn-text="Zurück">
			<h1>Clubhelper Mobile</h1>
		</div>
		<!-- /header -->

		<div role="main" class="ui-content">
			<div>
				<div id="contentCalendar" class="width25 floatRight leftColumn">
					<div class="width75 floatLeft">
						<div class="gradient">
<!-- 							<div data-role="navbar"> -->
<!-- 								<ul> -->
<!-- 									<li><a href="#" onclick="addFlightTime()" -->
<!-- 										data-role="button" data-icon="edit" -->
<!-- 										data-theme="b" data-inline="true">Neuer Termin</a></li> -->
<!-- 								</ul> -->
<!-- 							</div> -->
							<div id="full-clndr"></div>
<!-- 							<div id="event_container" style="background-color:powderblue;"><h3>Termine</h3></div> -->
						</div>
					</div>
				</div>
			</div>
		</div> <!-- /content -->

		<div data-role="footer" data-position="fixed" data-mini="true">
			Copyright Markus Kreth - MTV Groß-Buchholz</div>
		<!-- /footer -->

	</div>
	<!-- /page -->

<!-- 	<div id="printLists" data-role="popup"> -->
<!-- 		<a href="#" data-role="button" onclick="printPhoneList2">Telefonliste</a> -->
<!-- 	</div> -->

	<div id="editDialog" data-role="page"></div>
	<div id="editGroupDialog" data-role="page"></div>
	
	<div id="templates" style="display:none;">
		<input class="datepicker" type="date" data-role="datebox" data-options='{"mode": "datebox", "useNewStyle":true,"zindex":1200}' />
		<div data-role="popup" id="popupBasic"></div>
	</div>
	<div id="chooseTaskDialog" data-role="page">
		<div data-role="header" data-position="fixed" data-add-back-btn="true"
			data-back-btn-text="Zurück">
			<H2 id="TaskHeadText"></H2>
		</div>
		<div data-role="main" class="ui-content">
			<div id="TaskContentText"></div>
		</div>
		<a id="TaskOkbutton" href="#" data-role="button" data-icon="ok">OK</a>
		<a id="TaskCancelbutton" href="#" data-role="button" data-icon="cancel">Abbrechen</a>
	</div>
</body>
</html>