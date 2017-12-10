
var templateString = 
		"<div class=\"clndr-controls\">\n" + 
		"              <a href=\"#\" class=\"ui-btn ui-icon-carat-l ui-btn-icon-notext clndr-previous-button\">prev</a>\n" + 
		"              <a href=\"#\" class=\"ui-btn ui-icon-carat-r ui-btn-icon-notext clndr-next-button\">next</a>\n" + 
		"              <div class=\"current-month\"><%= month %> <%= year %></div>\n" + 
		"\n" + 
		"            </div>\n" + 
		"            <div class=\"clndr-grid\">\n" + 
		"              <div class=\"days-of-the-week clearfix\">\n" + 
		"                <% _.each(daysOfTheWeek, function(day) { %>\n" + 
		"                  <div class=\"header-day\"><%= day %></div>\n" + 
		"                <% }); %>\n" + 
		"              </div>\n" + 
		"              <div class=\"days\">\n" + 
		"                <% _.each(days, function(day) { %>\n" + 
		"                  <div class=\"<%= day.classes %>\" id=\"<%= day.id %>\"><span class=\"day-number\"><%= day.day %></span></div>\n" + 
		"                <% }); %>\n" + 
		"              </div>\n" + 
		"            </div>\n" + 
		"            <div class=\"event-listing\">\n" + 
		"              <div class=\"event-listing-title\">Veranstaltungen</div>\n" + 
		"              <% _.each(eventsThisMonth, function(event) { %>\n" + 
		"                  <div class=\"event-item\">\n" + 
		"                    <div class=\"event-item-date\"><%= moment(event.start, \"YYYY-MM-DD\").format(\"L\") %><%= typeof(event.end)!== \'undefined\' ?  \' - \'+moment(event.end).format(\'L\') : \'\' %></div> " + 
		"                    <div class=\"event-item-name, <%= event.colorClass %>\"><%= event.title %></div>" + 
		"                    <div class=\"event-item-location\"><%= event.location %></div>\n" + 
		"                  </div>\n" + 
		"                <% }); %>\n" + 
		"</div>";

$(document).ready(function() {

	$(document).on("pageshow", "#calendarpage", function() {
		loadCalendarData();
	});
	moment.locale('de');

});

function showCalendar(){
	$.mobile.changePage("#calendarpage");
}

function loadCalendarData() {
	$("#full-clndr").empty();
	
	var theCalendarInstance = $("#full-clndr").clndr({
		startWithMonth: moment(),
        daysOfTheWeek: ['Mo', 'Di', 'Mi', 'Do', 'Fr', 'Sa', 'So'],
        constraints: {
            startDate: moment().subtract(1, 'months').date(0).format('YYYY-MM-DD')
        },
        multiDayEvents: {
            endDate: 'end',
            singleDay: 'start',
            startDate: 'start'
        },
        showAdjacentMonths: true,
        adjacentDaysChangeMonth: false,
        template: templateString,
        clickEvents: {
	        click: function (target) {
	        	var text = "";
	        	target.events.forEach(function(elt, i) {
	        		text+=elt.title +"\n";
	        	})
	        	alert(target.date.format("DD.MM.YY") + ": " + text);
	        },
	        onMonthChange: function(month) {
	        	theCalendarInstance.eventsThisInterval.forEach(function(event){
					var theKey = moment(event.start).format("[.calendar-day-]YYYY-MM-DD");
					$(theKey).append("<br>"+event.title);
				});
	          }
    	}
	});
	repo(baseUrl + "events", function(response) {
		theCalendarInstance.setEvents(response);
		theCalendarInstance.eventsThisInterval.forEach(function(event){
			var theKey = moment(event.start).format("[.calendar-day-]YYYY-MM-DD");
			$(theKey).append("<br><span>"+event.title+"</span>");
		});
	});
	
}