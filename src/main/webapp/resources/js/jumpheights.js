 var tasks;
function showJumpHeights () {

	tasks = null;
	repo(baseUrl + "jumpheights/" + currentPerson.prename + "/" + currentPerson.surname, function(response) {
		tasks = response['tasks'];
		if(!tasks) {
			ajax(baseUrl + "jumpheights/" + currentPerson.prename + "/" + currentPerson.surname, null, "post", function(response){

				tasks = response['tasks'];
				if(tasks) {
					showTaskPage();
				} else {
					alert("Sheet not found, unable to create!");
				}
			});
			return;
		}
		showTaskPage();
	});

}

function showTaskPage() {

	var taskList = $("#personJumpHeightTasks");
	taskList.empty();
	for (index = 0; index < tasks.length; ++index) {
		var element = $("<li></li>");
		element.attr("index", index);
		if(tasks[index].info) {
			element.append(tasks[index].name + "<br />" + tasks[index].info);
		} else {
			element.append(tasks[index].name);
		}
		taskList.append(element);
	}

	$.mobile.changePage("#personJumpHeight");
}

function addFlightTime() {
	var listView = $("<ul data-role=\"listview\" data-inset=\"true\"></ul>");
	listView.attr("id", "chooseTask");
	$("#personJumpHeightTasks li").each(function() {
		listView.append($("<li />").text(tasks[$(this).attr("index")].name).on('click', function() {
			var task = $(this).text();
			log.debug("Choosen Task: " + task);
			$("#contentText").empty();
			$("#chooseTaskDialog").dialog( "close" );
			var div = $("<div></div>")
				.append("<label for=\"TaskInputValue\">" + task +" Wert:</label>")
				.append("<input type=\"text\" name=\"TaskInputValue\" id=\"TaskInputValue\" value=\"\">");

			showTaskDialog(task, div, function(){
				var value =  $("#chooseTaskDialog #TaskInputValue").val().replace(",", ".");
				var url = baseUrl + "jumpheights/" + currentPerson.prename + "/" + currentPerson.surname + "/" + task;
				log.debug("Storing value in Task: " + task + "=" + value);
				log.trace(url);
				ajax(url, value, "post", function(response){

					$("#chooseTaskDialog").dialog( "close" );
					var popup = $("#popupBasic");
					popup.empty();
					popup.append("Neuer Wert für " + task + " gespeichert.");
					popup.popup().popup( "open" );
					
				});
			});
		}));
	});
	
	showTaskDialog("Welche Übung?", listView, null);
	
}

function showTaskDialog(headText, mainView, okAction) {

	var dlg = $("#chooseTaskDialog");
	dlg.find("#TaskHeadText").text(headText);
	dlg.find("#TaskContentText").empty();
	dlg.find("#TaskContentText").append(mainView);
	if(okAction == null) {
		dlg.find("#TaskOkbutton").hide();
	} else {
		dlg.find("#TaskOkbutton").show();
		dlg.find("#TaskOkbutton").on('click', okAction);
	}
	dlg.find("#TaskCancelbutton").on('click', function() { $("#chooseTaskDialog").dialog( "close" ) });
	dlg.trigger("create");
	dlg.dialog();
	$.mobile.changePage("#chooseTaskDialog");
}
