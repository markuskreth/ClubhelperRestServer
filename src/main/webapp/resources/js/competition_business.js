/*jshint esversion: 6 */

/**
 * 
 */
var competitionParticipants = (function () {

	// private
	var competitionId;
	var calendarId;
		
	function CompetitorList() {
		
		this.competition = function (calId, compId) {
			competitionId = compId;
			calendarId = calId;
		};
		
		this.combine = function(personId) {
			return {"competitionId": competitionId, "calendarId": calendarId, "personId":personId};
		}
		
		this[Symbol.iterator] = function() {

			var index = 0;
			var sub = [];
			for(var el of this) {
				sub.push(el);
			}

		    const iterator = {
	            next() {
	                while (index < sub.length) {
	                	return { value: {competitionId, personId:sub[index++]}, done: false  };
	                } 

	                return { done: true };
	            }
	        };
		    return iterator;
		};
		
	}
	
	CompetitorList.prototype = new ExtendableItemList();
	
	// public
	return new CompetitorList();
})();

function ExtendableItemList () {
	
	var _list = [];
	
	const instance = {
		push: function (item) {
			_list.push(item);
		},
		length: function() {
			return _list.length;
		},
		remove: function (item) {
			var index = _list.indexOf(pId);
			if (index > -1) {
				_list.splice(index, 1);
			}
		},
		toJSON : function () {
			return _list;
		},
		[Symbol.iterator]: function() {

			var index = 0;

		    const iterator = {
		            next() {
		                while (index < _list.length) {
		                	return { value: _list[index++], done: false  };
		                } 

		                return { done: true };
		            }
		        };
		    return iterator;
		}
	};

//	instance.prototype;
	return instance;
}

ExtendableItemList.prototype[Symbol.iterator] = function() {

	var index = 0;

    const iterator = {
            next() {
                while (index < _list.length) {
                	return _list[index++];
                } 

                return { done: true };
            }
        };
    return iterator;
};

function addCompetitionCompetitorsToList(person) {

	if(!person) return;
	var id = "checkboxAttendance" + person.id;
	var link = $("<input data-iconpos=\"left\" type=\"checkbox\" class=\"competitionPersonCheckbox\"></input>");
	link.attr("personId", person.id);
	link.attr("id", id);
	link.attr("name", id);
	
	link.click(function() {
		var me = $(this);
		var pId = me.attr("personId");
		var checked = me.prop("checked");
		if(checked) {
			competitionParticipants.push(pId);
			var tripel = competitionParticipants.combine(pId);
			ajax(baseUrl + "competitions/"+tripel.calendarId+"/"+tripel.competitionId, pId, "put", function(obj) {
				alert(JSON.stringify(obj));
			});
		} else {
			competitionParticipants.remove(pId);
		}
		
	});
	var hull = $("<div></div>");
	var label = $("<label></label>").attr("for", id).text(person.prename + " " + person.surname);
	hull.append(link);
	hull.append(label);
	var item = $("<li></li>").append(label.append(link));

	$("#personList").append(item);
}

function initCompetitionSelector(){

	repo(baseUrl + "events/mtv_wettkampf", function(response) {
		var selector = $("#choosen-competition");
		response.forEach(function(event){
			selector.append(
				$("<option></option>")
					.text(event.title + " " + moment(event.start).format("DD.MM.YY"))
					.attr("value", event.id)
					.attr("eventId", event.id)
					.attr("calendarName", event.calendarName)
			);
		});

		$(".competitionPersonCheckbox").prop( "disabled", true );
		$("#chooseCompetition").show();
		selector.bind( "change", function(event, ui) {
			
			$(".competitionPersonCheckbox").prop("checked", false);
			
			ui = $( "#choosen-competition  option[value=\"" +
					$( "#choosen-competition" ).val() +
					"\"]" );
			
			var eventId = ui.attr("eventId");
			var calendarName = ui.attr("calendarName");

			if(eventId && calendarName) {
				$(".competitionPersonCheckbox").prop( "disabled", false );
				competitionParticipants.competition(calendarName, eventId);
				ajax(baseUrl + "competitions/"+calendarName+"/"+eventId, null, "get", function(list) {
					$.each(list, function(index, item) {
						competitionParticipants.push({"val":item.personId, send:true});
						$("#personList input[personId="+item.personId+"]").attr("checked", true);
					});
				});
			} else {

				$(".competitionPersonCheckbox").prop( "disabled", true );
			}
			
		});
//		if(competitionParticipants.contains(link.attr("personId"))) {
//			link.attr("checked", true);
//		}
	});
}