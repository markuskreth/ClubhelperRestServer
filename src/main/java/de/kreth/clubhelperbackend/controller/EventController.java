package de.kreth.clubhelperbackend.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.kreth.clubhelperbackend.calendar.CalendarAdapter;

@Controller
@RequestMapping("/events")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'STAFF')")
public class EventController {

	private final CalendarAdapter adapter;

	public EventController() throws GeneralSecurityException, IOException {
		adapter = new CalendarAdapter();
	}

	@RequestMapping(value = { "/", "" }, method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Event> getEvents() throws IOException {
		List<Event> events = new ArrayList<>();
		adapter.getAllEvents().forEach(e -> {
			events.add(new Event(e.getSummary(), e.getStart().getDate().getValue()));
		});
		return events;
	}

	public class Event {
		private String title;
		private long start;

		public Event(String title, long l) {
			super();
			this.title = title;
			this.start = l;
		}

		public String getTitle() {
			return title;
		}

		public long getStart() {
			return start;
		}

	}
}
