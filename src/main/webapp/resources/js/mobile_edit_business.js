
function editPerson() {

	$("#personDetailPersonEdit").empty();
	$("#personDetailContactsEdit").empty();
	$("#personDetailAdressesEdit").empty();
	$("#personDetailRelationsEdit").empty();

	$.mobile.changePage("#personEdit");
	
	Person(currentPersonId, function(person) {

		var bday = person.birthday();
		var datepicker = $("<input />")
			.attr("data-role", "datebox")
			.attr("type", "date")
			.attr("name", "birthday")
			.attr("id", "birthday");
		
		datepicker.val(bday);
		
		$("#personDetailPersonEdit")
			.append($("<label></label>").attr("for", "prename").text("Vorname:"))
			.append($("<input />").attr("type", "text").attr("name", "prename").attr("id", "prename").attr("value", person.prename))
			.append($("<label></label>").attr("for", "surname").text("Nachname:"))
			.append($("<input />").attr("type", "text").attr("name", "surname").attr("id", "surname").attr("value", person.surname))
			.append($("<label></label>").attr("for", "birthday").text("Geburtstag:"))
			.append(datepicker);

		$("#personDetailPerson").trigger("create");

		var obj = $("#personDetailContactsEdit");
		person.contacts(function(items) {

			for ( var index in items) {
				var con = items[index];

				var element = renderEditContact(con);
				
				element = $("<li></li>").append(
						$("<div></div>").attr("data-role", "controlgroup").attr(
								"data-type", "horizontal").append(element));

				obj.append(element);
			}
			obj.trigger("create");
		})
		
		person.relatives(function(relativePerson) {

			var link = $("<a></a>");
			link.attr("width", "100%");
			link.attr("data-role", "button");
			link.attr("data-iconpos", "right");
			link.attr("href", "#");
			link.text(relativePerson.relation.name + ": " + relativePerson.prename + " " + relativePerson.surname);
			
			var group = $("<div></div>")
				.attr("data-role", "controlgroup")
				.attr("data-type", "horizontal")
				.append(link)
				.append($("<a></a>")
					.attr("href", "#")
					.attr("data-role", "button")
					.attr("data-iconpos", "notext")
					.attr("onclick", "editRelation(" + relativePerson.relation.id + ")")
					.attr("data-icon", "edit")
					.text("edit"))
				.append($("<a></a>")
					.attr("href", "#")
					.attr("data-role", "button")
					.attr("data-iconpos", "notext")
					.attr("onclick", "deleteRelation(" + relativePerson.relation.id + ")")
					.attr("data-icon", "delete")
					.text("edit"));
			
			var obj = $("#personDetailRelationsEdit");
			
			obj.append(group);
			
			obj.trigger("create");
		});
	});	

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
				.attr("onclick", "editContact(" + contact.id + ")")
				.attr("data-icon", "edit")
				.text("edit"))
			.append($("<a></a>")
				.attr("href", "#")
				.attr("data-role", "button")
				.attr("data-iconpos", "notext")
				.attr("onclick", "deleteContact(" + contact.id + ")")
				.attr("data-icon", "delete")
				.text("delete"));
		
		return group;
		
	}

}

function deleteRelation(relativeId) {
	Person(currentPersonId, function(person) {
		person.relatives(function(relativePerson) {
			if(relativePerson.relation.id==relativeId) {
				
				var headText = "Beziehung löschen?";
				var contentText = relativeId + "--> "+ relativePerson.relation.name + ": " + relativePerson.prename + " " + relativePerson.surname;
				var action = "alert('deleted');";
				showDialog(headText, contentText, action);
			}
		})
	})
	
}

function showDialog(headText, contentText, action) {

	var editDialog = $("#editDialog");
	editDialog.empty();
	editDialog.append($("<div data-role=\"header\"></div>").append($("<H2></H2>").text(headText)));
	editDialog.append($("<div data-role=\"main\" class=\"ui-content\"></div>").append($("<P></P>").append(contentText)));

	editDialog.append($("<a></a>")
			.attr("href", "#")
			.attr("data-role", "button")
			.attr("onclick", "$(\"#editDialog\").dialog( \"close\" );" + action)
			.attr("data-icon", "ok")
			.text("OK"));
	editDialog.append($("<a></a>")
			.attr("href", "#")
			.attr("data-role", "button")
			.attr("onclick", "$(\"#editDialog\").dialog( \"close\" );")
			.attr("data-icon", "cancel")
			.text("Abbrechen"));
	editDialog.trigger("create");
	$.mobile.changePage( "#editDialog", { role: "dialog" } );
}

function editRelation(relativeId) {
	alert("edit Relative " + relativeId);
}

function deleteContact(contactId) {
	alert("delete Contact " + contactId);
}

function editContact(contactId) {
	Person(currentPersonId, function(person) {
		person.contacts(function(items) {

			for ( var index in items) {
				var con = items[index];
				if(con.id == contactId) {
					var headText = "Kontakt ändern";
					var content = $("<input id='changeContactText'></input>").attr("type", "text").val(con.value);
					var action = "changeContact(" + contactId + ");";
					showDialog(headText, content, action);
				}
			}
		});
	});
}

function changeContact(contactId) {
	var value = $("#changeContactText").val();
	alert("Changing Contact " + contactId + " Value to " + value);
}