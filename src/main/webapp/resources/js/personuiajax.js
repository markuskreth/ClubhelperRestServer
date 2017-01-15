
$(document).on("pageshow","#personDetails",function(){
	updateAllPersonDetailData();
});

var split = location.pathname.replace(/^\/|\/$/g, '').split( '/' );
var id = parseInt(split[split.length-1]);

if(Number.isInteger(id)) {
	storeObject.personId = id;
	updateAllPersonDetailData();
	$.mobile.changePage("#personDetails");
}

function showPerson(personId) {
	storeObject.personId=personId;
	$.mobile.changePage("#personDetails");
}

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

function showPersonPerson() {
	var date = moment(storeObject.person.birth,"YYYY/MM/DD HH:mm:ss.SSS ZZ");
	date.locale('DE_de');
	var validDate = date.isValid();
	$("#personDetailPerson")
		.append("<p>Name:</p><p>" + storeObject.person.prename + " " + storeObject.person.surname + "</p>")
		.append("<p>Geburtstag:" + date.format('L') + " Alter: " + date.fromNow(true) + "</p>");
	$("#personDetailPerson").trigger("create");
}

function showPersonContacts() {
	var obj = $("#personDetailContacts");
	for (var index in storeObject.contacts) {
		var con = storeObject.contacts[index];
		var element = renderContact(con);
		obj.append(element);
	}
	obj.trigger("create");
}

function renderContact(contact) {

	var link = $("<a></a>");
	link.attr("data-role", "button");
	link.attr("data-iconpos", "right");
// 	link.attr("data-mini", "true");
	link.attr("data-inline", "true");
	link.attr("data-corners", "true");

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
	link.text(contact.value);
	if(contact.type == 'Mobile') {
		link = [link, link.clone()];
		link[1].attr("href", "sms:" + contact.value);
		link[1].attr("data-icon", "mail");
		link[1].attr("data-iconpos", "notext");
	}
	return $("<li></li>")
		.append($("<div></div>").attr("data-role", "controlgroup").attr("data-type", "horizontal")
				.append(link));
}