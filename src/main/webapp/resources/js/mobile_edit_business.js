var currentPerson;

function editPerson() {

	$("#personDetailPersonEdit").empty();
	$("#personDetailContactsEdit").empty();
	$("#personDetailAdressesEdit").empty();
	$("#personDetailRelationsEdit").empty();

	$.mobile.changePage("#personEdit");

	Person(
			currentPersonId,
			function(person) {
				currentPerson = person;
				updatePersonView();

				$.mobile.activePage.find(".ui-header [data-rel=back]").click(function () {

					if(currentPerson.hasChanges()) {
						currentPerson.update(function (person) {
							currentPerson = person;
						});
					}
				});
			});
}

function updatePersonView() {

	$("#personDetailPersonEdit").empty();
	$("#personDetailContactsEdit").empty();
	$("#personDetailAdressesEdit").empty();
	$("#personDetailRelationsEdit").empty();

	var bday = currentPerson.birthday();
	var datepicker = $("<input />")
			.attr("data-role", "datebox")
			.attr("type", "date")
			.attr("name", "birthday")
			.attr("id", "birthday");

	datepicker.val(bday);

	$("#personDetailPersonEdit").append(
			$("<label></label>").attr("for", "prename").text(
					"Vorname:")).append(
			$("<input />").attr("type", "text").attr("name",
					"prename").attr("id", "prename").attr("value",
					currentPerson.prename).on('input',function(e){
						currentPerson.prename = $("#prename").val();
						currentPerson.hasChanged();
				    })).append(
			$("<label></label>").attr("for", "surname").text(
					"Nachname:")).append(
			$("<input />").attr("type", "text").attr("name",
					"surname").attr("id", "surname").attr("value",
					currentPerson.surname).on('input',function(e){
						currentPerson.surname = $("#surname").val();
						currentPerson.hasChanged();
				    })).append(
			$("<label></label>").attr("for", "birthday").text(
					"Geburtstag:")).append(datepicker);

	$("#personDetailPerson").trigger("create");

	var obj = $("#personDetailContactsEdit");
	currentPerson.contacts(function(items) {

		for ( var index in items) {
			var con = items[index];

			var element = renderEditContact(con);

			element = $("<li></li>").append(
					$("<div></div>").attr("data-role",
							"controlgroup").attr("data-type",
							"horizontal").append(element));

			obj.append(element);
		}
		obj.trigger("create");
	})

	currentPerson
			.relatives(function(relativePerson) {

				var link = $("<a></a>");
				link.attr("width", "100%");
				link.attr("data-role", "button");
				link.attr("data-iconpos", "right");
				link.attr("href", "#");
				link.text(relativePerson.relation.name + ": "
						+ relativePerson.prename + " "
						+ relativePerson.surname);

				var group = $("<div></div>")
						.attr("data-role", "controlgroup")
						.attr("data-type", "horizontal")
						.append(link)
						.append(
								$("<a></a>")
										.attr("href", "#")
										.attr("data-role", "button")
										.attr("data-iconpos", "notext")
										.click(function() {
											editRelation(relativePerson.relation.id);
										})
										.attr("data-icon", "edit")
										.text("edit"))
						.append(
								$("<a></a>")
										.attr("href", "#")
										.attr("data-role", "button")
										.attr("data-iconpos", "notext")
										.click(function() {
											deleteRelation(relativePerson.relation.id);
										})
										.attr("data-icon", "delete")
										.text("edit"));

				var obj = $("#personDetailRelationsEdit");

				obj.append(group);

				obj.trigger("create");
			});	
}

function addContact() {


		var headText = unescape("Kontakt hinzufügen für " + currentPerson.prename + " " + currentPerson.surname);
		var con = {id:-1, personId:currentPersonId, type:"", value:""};

		var content = createEditContactContent(con);
		var action = function() {
			con.value = $("#changeContactText").val();
			con.type = $("#changeContactTypeSelect").val();

			var url = baseUrl + "contact/" + con.id;
			var me = currentPerson;
			ajax(url, con, "post",
					function() {
						me.setContacts(null);
						var text = JSON.stringify(me);
						sessionStorage.setItem("personId" + me.personId, text);
						updatePersonView();
					});
			
		};
		showDialog(headText, content, action);
}

function addRelation() {

		alert("addRelation to " + currentPerson.prename + " "
				+ currentPerson.surname);
}

function addAdress() {
		alert("addAdress to " + currentPerson.prename + " "
				+ currentPerson.surname);
}

function deleteRelation(relativeId) {

		currentPerson.relatives(function(relativePerson) {

			if (relativePerson.relation.id == relativeId) {

				var headText = unescape("Beziehung l%F6schen%3F");
				var contentText = relativeId + "--> "
						+ relativePerson.relation.name + ": "
						+ relativePerson.prename + " "
						+ relativePerson.surname;
				var action = function() {

					var url = baseUrl + "/relative/" + relativeId;
					var me = currentPerson;
					ajax(url, relativePerson.relation, "delete",
							function() {
								me.deleteRelatives();
								var text = JSON.stringify(me);
								sessionStorage.setItem("personId"
										+ me.personId, text);
								updatePersonView();
							});
				};
				showDialog(headText, contentText, action);
			}
		})

}

function renderEditContact(contact) {

	var link = $("<a></a>");
	link.attr("data-role", "button");
	link.attr("data-iconpos", "left");
	link.attr("data-inline", "true");
	link.attr("data-corners", "true");

	link.attr("href", "#");

	if (contact.type == 'Email') {
		link.attr("data-icon", "mail");
	} else if (contact.type == 'Mobile') {
		link.attr("data-icon", "phone");
	} else {
		link.attr("data-icon", "phone");
	}

	link.text(contact.value);

	var group = $("<div></div>")
		.attr("data-role", "controlgroup")
		.attr("data-type", "horizontal")
		.append(link)
		.append($("<a></a>")
				.attr("href", "#")
				.attr("data-role", "button")
				.attr("data-iconpos", "notext")
				.click(function() {
					editContact(contact.id);
				})
				.attr("data-icon", "edit")
				.text("edit"))
		.append($("<a></a>")
				.attr("href", "#")
				.attr("data-role", "button")
				.attr("data-iconpos", "notext")
				.click(function() {
					deleteContact(contact.id)
				})
				.attr("data-icon", "delete")
				.text("delete"));

	return group;

}

function showDialog(headText, contentText, action) {

	var editDialog = $("#editDialog");
	editDialog.empty();
	editDialog.append($("<div data-role=\"header\"></div>")
			.append($("<H2></H2>").text(headText)))
			.append($("<div data-role=\"main\" class=\"ui-content\"></div>")
			.append($("<P></P>").append(contentText)));

	editDialog.append($("<a></a>")
			.attr("href", "#")
			.attr("data-role", "button")
			.click(function() {
				editDialog.dialog("close");
				action();
			})
			.attr("data-icon", "ok").text("OK"));
	editDialog.append($("<a></a>")
			.attr("href", "#")
			.attr("data-role", "button")
			.click(function() {
				editDialog.dialog( "close" );
			})
			.attr("data-icon", "cancel")
			.text("Abbrechen"));
	editDialog.trigger("create");
	$.mobile.changePage("#editDialog", {
		role : "dialog"
	});
}

function editRelation(relativeId) {
	alert("edit Relative " + relativeId);
}

function deleteContact(contactId) {

		currentPerson.contacts(function(items) {
			for ( var index in items) {

				var con = items[index];
				if (con.id == contactId) {

					var headText = unescape("Kontakt l%F6schen%3F");
					var contentText = contactId + "--> " + con.type + ": "
							+ con.value;
					var action = function() {

						var url = baseUrl + "contact/" + contactId;
						var me = currentPerson;
						ajax(url, con, "delete",
								function() {
									me.setContacts(null);
									var text = JSON.stringify(me);
									sessionStorage.setItem("personId" + me.personId, text);
									updatePersonView();
								});
					}

					showDialog(headText, contentText, action);
				}
			}

		});

}

function editContact(contactId) {

		currentPerson.contacts(function(items) {

			for ( var index in items) {
				var con = items[index];
				if (con.id == contactId) {
					var headText = unescape("Kontakt anlegen.");

					var content = createEditContactContent(con);
					var action = function() {

						con.value = $("#changeContactText").val();
						con.type = $("#changeContactTypeSelect").val();

						var url = baseUrl + "contact/" + con.id;
						var me = currentPerson;
						ajax(url, con, "put",
								function() {
									me.setContacts(null);
									var text = JSON.stringify(me);
									sessionStorage.setItem("personId" + me.personId, text);
									updatePersonView();
								});
					};
					showDialog(headText, content, action);
					break;
				}
			}
		});
}

function createEditContactContent(con) {

	var typeSelect = $("<select></select>").attr("id",
	"changeContactTypeSelect").attr("name",
	"changeContactTypeSelect");
	var values = [ "Telefon", "Email", "Mobile" ];
	
	for (var i = 0; i < values.length; i++) {
		var name = values[i];
		var item = $("<option></option>").attr("value", name)
				.text(name);
		if (name == con.type) {
			item.attr("selected", "selected");
		}
		typeSelect.append(item);
	}

	var textfield = $("<input id='changeContactText'></input>")
			.attr("type", "text").val(con.value);

	var content = $("<div></div>").append(
			$("<label></label>").attr("for",
					"changeContactTypeSelect").text("Art:"))
			.append(typeSelect).append(
					$("<label></label>").attr("for",
							"changeContactText").text("Wert:"))
			.append(textfield);
	return content;
}

function changeContact(contactId) {
	var type = $("#changeContactTypeSelect").val();
	var value = $("#changeContactText").val();

	currentPerson.contacts(function(contacts) {
			for (var i = 0; i < contacts.length; i++) {
				var con = contacts[i];
				if (con.id == contactId) {
					con.type = type;
					con.value = value;
					currentPerson.updateContact(con, function(contact) {
						updatePersonView();
					});
				}
			}
		})
}

function addPerson() {
	alert("addPerson clicked!");
}