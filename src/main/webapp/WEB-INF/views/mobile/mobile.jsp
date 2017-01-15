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
<script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
<script src=<c:url value='/resources/js/moment-with-locales.min.js' />></script>
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
var baseUrl = null;

baseUrl = location.protocol + '//' + location.host;

var split = location.pathname.replace(/^\/|\/$/g, '').split( '/' );
for (var i = 0; i < split.length-1; i++) {
	baseUrl += "/" + split[i];
}
var id = parseInt(split[split.length-1]);

if(Number.isInteger(id)) {
	baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf('/'));
	storeObject.personId = id;
	$(":mobile-pagecontainer").pagecontainer( "change", $("#personDetails" ));
	updateAllPersonDetailData();
}

$(document).on("pageshow","#personDetails",function(){
	updateAllPersonDetailData();
	});

function updateAllPersonDetailData() {

	$("#personDetailPerson").empty();
	$("#personDetailContacts").empty();
	$.ajax({
		  url: baseUrl + "/person/" + storeObject.personId,
		  dataType: "json",
		  success : function(response) {  
			  storeObject.person = response;
			  showPersonPerson();
		  }
		});
	$.ajax({
		  url: baseUrl + "/contact/for/" + storeObject.personId,
		  dataType: "json",
		  success : function(response) {  
			  storeObject.contacts = response;		
			  showPersonContacts();
		  }
		});
	  
}

function printPhoneList() {
	alert("printPhoneList aus mainmenu");
}

function printPhoneList2() {
	alert("printPhoneList aus popup");
}

function showPerson(personId, prename, surname) {
	storeObject.personId=personId;
	storeObject.prename=prename;
	storeObject.surname=surname;
	$.mobile.changePage("#personDetails");
}

function showPersonPerson() {
	var date = moment(storeObject.person.birth,"YYYY/MM/DD HH:mm:ss.SSS ZZ");
	date.locale('DE_de');
	var validDate = date.isValid();
	$("#personDetailPerson")
		.append("<p>Name:</p><p>" + storeObject.person.prename + " " + storeObject.person.surname + "</p>")
		.append("<p>Geburtstag:" + date.format('L') + " Alter: " + date.fromNow(true) + "</p>");
}

function showPersonContacts() {
	var obj = $("#personDetailContacts");
	for (var index in storeObject.contacts) {
		var con = storeObject.contacts[index];
		var element = renderContact(con);
		obj.append(element);
	}
}

function renderContact(contact) {

	var link = $("<a data-mini='true' data-role='button' data-inline='true'></a>").append(contact.value);

	if(contact.type == 'Email') {
		link.attr("href", "mailto:" + contact.value);
		link.attr("data-icon", "mail");
	} else if(contact.type == 'Mobile') {
		link.attr("href", "tel:" + contact.value);
		link.attr("data-icon", "phone");
	} else {
		link.attr("href", "tel:" + contact.value);
		link.attr("data-icon", "phone");
	}
	
	return $("<li></li>").append($("<div data-role='controlgroup' data-type='horizontal'></div>").append(link));
}
</script>
</head>
<body>
	<div data-role="page" id="personList">
		<div data-role="header" data-position="fixed">
			<h1>Clubhelper Mobile</h1>
			<a href="#mainMenu" data-rel="popup" data-role="button"
				data-icon="bars" data-mini="true" data-rel="popup"></a>
			<div data-role="popup" id="mainMenu">
				<ul id="mainMenuItems">
					<li><a href="#" onclick="printPhoneList" data-rel="popup" class="ui-btn ui-icon-bulletes ui-btn-icon-left">Listen</a></li>
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
							    <li><a href="#" onclick="printLists()" class="ui-btn ui-icon-bulletes">Listen</a></li>
							  </ul>
							</div>
							<p>Personen</p>
							<ul data-role="listview" data-inset="true" data-filter="true">
								<c:forEach var="person" items="${PersonList}">
										<li><a href="#" onclick="showPerson(${person.id}, '${person.prename}', '${person.surname}')">${person.prename} ${person.surname}</a></li>
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
							    <li><a href="#addDetail" class="ui-btn ui-icon-plus">Hinzufügen</a></li>
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