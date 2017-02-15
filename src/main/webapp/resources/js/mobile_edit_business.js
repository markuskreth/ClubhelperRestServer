
function editPerson() {

	$("#personDetailPersonEdit").empty();
	$("#personDetailContactsEdit").empty();
	$("#personDetailAdressesEdit").empty();
	$("#personDetailRelationsEdit").empty();

	$.mobile.changePage("#personEdit");
	
	Person(currentPersonId, function(person) {

		$("#personDetailPersonEdit")
			.append("<p>Name:</p><p>" + person.prename 
			+ " " + person.surname + "</p>")
			.append("<p>Geburtstag:" + person.birthday() 
			+ "</p>");
		
		$("#personDetailPerson").trigger("create");

		var obj = $("#personDetailContactsEdit");
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
		
		person.relatives(function(relativePerson) {

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
			
			var obj = $("#personDetailRelationsEdit");
			
			obj.append(element);
			
			obj.trigger("create");
		});
	});	
	
}
