
var templateString = 
		"<div class=\"clndr-controls\">\n" + 
		"              <div class=\"clndr-previous-button\">&lt;</div>\n" + 
		"              <div class=\"clndr-next-button\">&gt;</div>\n" + 
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
		"              <div class=\"event-listing-title\">EVENTS THIS MONTH</div>\n" + 
		"              <% _.each(eventsThisMonth, function(event) { %>\n" + 
		"                  <div class=\"event-item\">\n" + 
		"                    <div class=\"event-item-name\"><%= event.title %></div>\n" + 
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
	var lotsOfMixedEvents = [
	    {
	        end: '2017-11-08',
	        start: '2017-11-04',
	        title: 'Monday to Friday Event'
	    }, {
	        end: '2017-12-20',
	        start: '2017-12-15',
	        title: 'Another Long Event'
	    }, {
	        title: 'Birthday',
	        start: '2017-12-28'
	    }
	];
	
	var theCalendarInstance = $("#full-clndr").clndr({
		startWithMonth: moment(),
        daysOfTheWeek: ['Mo', 'Di', 'Mi', 'Do', 'Fr', 'Sa', 'So'],
        constraints: {
            startDate: moment().subtract(1, 'months').date(0)
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
	        	alert(target.date + ": " + JSON.stringify(target.events));
	        }
        }
	});
	repo(baseUrl + "events", function(response) {
		theCalendarInstance.setEvents(response);
	});
	
}