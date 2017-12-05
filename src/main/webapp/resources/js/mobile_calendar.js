
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
	$("#calendar_container").empty();
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
	
	var theCalendarInstance = $("#calendar_container").clndr({
		startWithMonth: moment(),
        daysOfTheWeek: ['Mo', 'Di', 'Mi', 'Do', 'Fr', 'Sa', 'So'],
        constraints: {
            startDate: moment().subtract(1, 'months').date(0)
        },
//        events: lotsOfMixedEvents,
        multiDayEvents: {
            endDate: 'end',
            singleDay: 'start',
            startDate: 'start'
        },
        showAdjacentMonths: true,
        adjacentDaysChangeMonth: false,

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