
var currentPersonId = null;

$(document).ready(function() {

	$("#collapsibleRelations").collapsible({
		expand : function(event, ui) {
			updateRelations();
		}
	});

	loadPersonList();

	$(document).on("pageshow", "#personDetails", function() {
		updateAllPersonDetailData();
	});

	$(document).on("pageshow", "#personDetails", function() {
		loadPersonList();
	});

	var split = location.pathname.replace(/^\/|\/$/g, '').split('/');
	var id = parseInt(split[split.length - 1]);

	if (Number.isInteger(id)) {
		showPerson(id);
		updateAllPersonDetailData();
	}

});

function loadPersonList() {

	repo(baseUrl + "person/", function(response) {

		for ( var index in response) {
			var person = response[index];

			sessionStorage.setItem("personId" + person.id, JSON.stringify(person));
			var link = $("<a href='#'></a>").text(person.prename + " " + person.surname);
			link.attr("personId", person.id);
			link.click(function() {
					var pId = $(this).attr("personId");
					showPerson(pId);
				});
			var item = $("<li></li>").append(link);
			$("#personList").append(item);
		}
		$("#personList").listview().listview('refresh');
	});

}

function showPerson(personId) {
	currentPersonId = personId;
	$.mobile.changePage("#personDetails");
}

function updateRelations() {
	Person(currentPersonId, function(person) {
		updateRelationsFor(person);
	});
}

function updateRelationsFor(person) {
	person.relatives(function(rel) {
		showPersonRelations(rel);
	});
}

function updateAllPersonDetailData() {

	$("#personDetailPerson").empty();
	$("#personDetailRelations").empty();
	$("#personDetailContacts").empty();

	Person(currentPersonId, function(person) {
		showPersonPerson(person);
		showPersonContacts(person);
	});	
}

function showPersonPerson(person) {
	$("#personDetailPerson")
		.append("<p>Name:</p><p>" + person.prename 
		+ " " + person.surname + "</p>")
		.append("<p>Geburtstag:" + person.birthday() 
		+ " Alter: " + person.age() + "</p>");
	
	$("#personDetailPerson").trigger("create");
}

function showPersonRelations(relativePerson) {

	relativePerson.contacts(function(items) {

		var group = $("<div></div>").attr("data-role", "controlgroup").attr(
				"data-type", "horizontal");

		for ( var index in items) {
			var con = items[index];
			var conLink = renderContact(con, true);
			group.append(conLink);
		}

		var element = $("<li></li>");

		var link = $("<a></a>");
		link.attr("width", "100%");
		link.attr("data-role", "button");
		link.attr("data-iconpos", "right");
		link.attr("data-icon", "info");
		link.attr("href", "#");
		var onCl = "switchToRelation(" + relativePerson.personId + ")";

		link.attr("onclick", onCl);
		link.text(relativePerson.prename + " " + relativePerson.surname);
		
		element.append(link).append("<br />");
		
		var obj = $("#personDetailRelations");
		
		obj.append(element).append(group);
		
		obj.trigger("create");
	});

}

function switchToRelation(id) {
	$("#collapsibleRelations").collapsible( "collapse" );
	$.mobile.changePage("#personList");
	showPerson(id);
	updateRelations();
}

function showPersonContacts(person) {
	var obj = $("#personDetailContacts");
	person.contacts(function(items) {

		for ( var index in items) {
			var con = items[index];
			var element = renderContact(con);

			element = $("<li></li>").append(
					$("<div></div>").attr("data-role", "controlgroup").attr(
							"data-type", "horizontal").append(element));

			obj.append(element);
		}
		obj.trigger("create");
	})
}

function renderContact(contact) {
	renderContact(contact, false, "right");
}

function renderContact(contact, withMiniAttr, iconAlign) {

	var link = $("<a></a>");
	link.attr("data-role", "button");
	link.attr("data-iconpos", iconAlign);
	link.attr("data-inline", "true");
	link.attr("data-corners", "true");

	if(withMiniAttr) {
		link.attr("data-mini", "true");
	}
	if (contact.type == 'Email') {
		link.attr("href", "mailto:" + contact.value);
		link.attr("data-icon", "mail");
	} else if (contact.type == 'Mobile') {
		link.attr("href", "tel:" + contact.value);
		link.attr("data-icon", "phone");
	} else {
		link.attr("href", "tel:" + contact.value);
		link.attr("data-icon", "phone");
	}
	
	link.text(contact.value);
	if (contact.type == 'Mobile') {
		link = [ link, link.clone() ];
		link[1].attr("href", "sms:" + contact.value);
		link[1].attr("data-icon", "mail");
		link[1].attr("data-iconpos", "notext");
	}

	return link;
}

function printPhoneList() {
	alert("printPhoneList aus mainmenu");
}

function printPhoneList2() {
	alert("printPhoneList aus popup");
}

