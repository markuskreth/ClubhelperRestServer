
var currentPersonId = null;
var currentPerson = null;

$(document).ready(function() {
	
	$("#collapsibleRelations").collapsible({
		expand : function(event, ui) {
			updateRelations();
		}
	});

	loadPersonList();

	$(document).on("pageshow", "#personDetails", function() {
		if(currentPersonId == null) {
			currentPersonId = readCookie('currentPersonId');
		}
		updateAllPersonDetailData();
	});

//	$(document).on("pageshow", "#personDetails", function() {
//		loadPersonList();
//	});

	var split = location.pathname.replace(/^\/|\/$/g, '').split('/');
	var id = parseInt(split[split.length - 1]);

	if (Number.isInteger(id)) {
		showPerson(id);
		updateAllPersonDetailData();
	}

});

function loadPersonList() {
	$("#personList").empty();

	var x = readCookie('DataRefreshNotNessessary');
	if (!x || sessionStorage.length==0) {
		sessionStorage.clear();
		createCookie('DataRefreshNotNessessary','While existing, cached data is used.',1);

		repo(baseUrl + "person/", function(response) {

			for ( var index in response) {
				var person = response[index];
				sessionStorage.setItem("personId" + person.id, JSON.stringify(person));
				addPersonToList(person);
			}
		});

	} else {
		for (var i = 0; i < sessionStorage.length; i++){
			var key = sessionStorage.key(i);
			if(key.startsWith("personId")) {
				var person = sessionStorage.getItem(key);
				addPersonToList(JSON.parse(person));
			}
		}
	}
	
	$("#personList").listview().listview('refresh');
	
}

function addPersonToList(person) {

	if(!person) return;
	
	var link = $("<a href='#' data-transition=\"flip\" ></a>").text(person.prename + " " + person.surname);
	link.attr("personId", person.id);
	link.click(function() {
			var pId = $(this).attr("personId");
			currentPersonId = pId;
			createCookie("currentPersonId", currentPersonId, 1); 
		});
	var item = $("<li></li>").append(link);
	$("#personList").append(item);
}

function showPerson(personId) {
	currentPersonId = personId;
	createCookie("currentPersonId", currentPersonId, 1); 
	$.mobile.changePage("#personDetails");
}

function updateRelations() {
	updateRelationsFor(currentPerson);
}

function updateRelationsFor(person) {
	person.relatives(function(rel) {
		showPersonRelations(rel);
	});
}

function updateAllPersonDetailData() {

	$('.personPrename').text('');
	$(".personSurname").text('');
	$("#personBirthday").text('');
	$("#personAge").text('');
	
	$("#personDetailRelations").empty();
	$("#personDetailContacts").empty();

	Person(currentPersonId, function(person) {
		currentPerson = person;
		showPersonPerson(person);
		showPersonContacts(person);
	});	
}

function showGroups(withDelete) {

	currentPerson.groups(function(groups, allGroupResult) {
		var content = $("<div></div>");
		
		var part = $("<div></div>").attr("data-role","controlgroup").attr("id","personGroups");
		content.append(part);
		
		for (i = 0, len=groups.length; i<len; i++) {
			if(withDelete) {
				part.attr("data-type","horizontal");
			}
			
			part.append($("<button></button>")
					.attr("data-mini","true")
					.text(groups[i].name));
			if(withDelete) {
				var delBtn = $("<button></button>")
				.attr("data-mini","true")
				.attr("data-icon","delete")
				.attr("data-iconpos","notext")
				.attr("groupid", groups[i].id)
				.on("click", function(e) {
					currentPerson.removeGroup($(this).attr("groupid"));
					$(this).parent().remove();
				})
				.text(groups[i].name);
				part.append(delBtn);
			}
		}
		if(withDelete) {
			content.append($("<H3 />").attr("class","ui-bar ui-bar-a").html("Verfügbar"));
			var wrapper = $("<div  class=\"ui-body\"></div>").attr("data-role","controlgroup");
			content.append(wrapper);
			for (var i = 0, allLen = allGroupResult.length; i < allLen; i++) {
				wrapper.append($("<button></button>")
						.attr("data-mini","true")
						.attr("data-icon","add")
						.text(allGroupResult[i].name)
						.attr("Groupname", allGroupResult[i].name)
						.attr("groupid", allGroupResult[i].id)
						.on("click", function(e) {
							var me = $(this);
							if(me.attr("lastEventTimestamp") == e.timeStamp) {
								return;
							}
							var added = $("<div></div>")
								.attr("data-role","controlgroup")
								.attr("data-type","horizontal")
								.append($("<button></button>")
										.attr("data-mini","true")
										.attr("data-icon","delete")
										.attr("data-iconpos","notext")
										.attr("groupid", me.attr("groupid"))
										.on("click", function(e) {
//											currentPerson.removeGroup(me.attr("groupid"));
											me.parent().remove();
										})
										.text(me.attr("Groupname")).trigger("create"));
							added.trigger("create");
							$("#personGroups").append(added);
							currentPerson.persGroups.push({"id":-1, "personId":currentPerson.id, "groupId":me.attr("groupid")});
							if(!currentPerson.type) {
								currentPerson.processGroups(function(groups, allGroups) {
									currentPerson.type=groups[0].name;
								});
							}
							me.attr("lastEventTimestamp", e.timeStamp);
						}));

			}
		}

		showDialog("#editGroupDialog", "Gruppen für " + currentPerson.prename + " " + currentPerson.surname, content, function(){log.debug("Clicked ok for Persongroup.")});
	});
}

function showPersonPerson(person) {
	log.info("Showing " + person.prename + " " + person.surname);
	$('.personPrename').text(person.prename);
	$(".personSurname").text(person.surname);
	$("#personBirthday").text(person.birthday());
	$("#personAge").text(person.age());
	
//	$("#personDetailPerson").trigger("create");
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
		var onCl = "switchToRelation(" + relativePerson.id + ")";

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
	currentPersonId = id;
	createCookie("currentPersonId", id, 1); 
	updateAllPersonDetailData();
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


function showDialog(dialogId, headText, contentText, action) {

	if(!dialogId) {
		dialogId = "#editDialog";
	}

	var editDialog = $(dialogId);
	editDialog.empty();
	editDialog.append($("<div data-role=\"header\"></div>")
			.append($("<H2></H2>").text(headText)))
			.append($("<div data-role=\"main\" class=\"ui-content\"></div>")
			.append($("<P></P>").append(contentText)));

	if(action != null) {
		editDialog.append($("<a></a>")
				.attr("href", "#")
				.attr("data-role", "button")
				.click(function() {
					editDialog.dialog("close");
					action();
				})
				.attr("data-icon", "ok").text("OK"));
	}
	editDialog.append($("<a></a>")
			.attr("href", "#")
			.attr("data-role", "button")
			.click(function() {
				editDialog.dialog( "close" );
			})
			.attr("data-icon", "cancel")
			.text("Abbrechen"));
	editDialog.trigger("create");
	$.mobile.changePage(dialogId, {
		role : "dialog"
	});
}

function printPhoneList() {
	alert("printPhoneList aus mainmenu");
}

function printPhoneList2() {
	alert("printPhoneList aus popup");
}
