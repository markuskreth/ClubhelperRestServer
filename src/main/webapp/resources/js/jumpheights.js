function showJumpHeights () {

	repo(baseUrl + "jumpheights/" + currentPerson.prename + "/" + currentPerson.surname, function(response) {
		var tasks = response['tasks'];
		if(!tasks) {
			ajax(baseUrl + "jumpheights/" + currentPerson.prename + "/" + currentPerson.surname, null, "post", function(response){

				var tasks = response['tasks'];
				if(tasks) {
					showTaskPage(tasks);
				} else {
					alert("Sheet not found, unable to create!");
				}
			});
			return;
		}
		showTaskPage(tasks);
	});

}

function showTaskPage(tasks) {

	var taskList = $("#personJumpHeightTasks");
	for (index = 0; index < tasks.length; ++index) {
		var element = $("<li></li>");
		element.append(tasks[index]);
		taskList.append(element);
	}

	$.mobile.changePage("#personJumpHeight");
}

function addFlightTime() {
	var optionTexts = [];
	var listView = $("<ul data-role=\"listview\" data-inset=\"true\"></ul>");
	listView.attr("id", "chooseTask");
	$("#personJumpHeightTasks li").each(function() {
		optionTexts.push($(this).text());
		listView.append($("<li />").text($(this).text()).on('click', function() {
			var task = $(this).text();
			log.debug("Choosen Task: " + task);
			$("#chooseTaskDialog").dialog( "close" );
			
//			var dlg = $("#chooseTaskDialog");
//			dlg.find("#headText").text("Welche Übung?");
//			dlg.find("#contentText").append(listView);
//			dlg.find("#okbutton")
//				.show()
//				.on('click', function() { $("#chooseTaskDialog").dialog( "close" ) });
		}));
	});
	
	var dlg = $("#chooseTaskDialog");
	dlg.find("#headText").text("Welche Übung?");
	dlg.find("#contentText").append(listView);
	dlg.find("#okbutton").hide();
	dlg.find("#cancelbutton").on('click', function() { $("#chooseTaskDialog").dialog( "close" ) });
	dlg.trigger("create");
	
	$.mobile.changePage("#chooseTaskDialog");
	
}
