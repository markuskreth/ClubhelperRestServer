package de.kreth.clubhelperbackend.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.common.collect.Maps;

import de.kreth.clubhelperbackend.google.calendar.CalendarAdapter;

@Controller
@RequestMapping("/events")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'STAFF')")
public class EventController {

	private final CalendarAdapter adapter;
	private final Logger log;

	public EventController() throws GeneralSecurityException, IOException {
		adapter = new CalendarAdapter();
		log = LoggerFactory.getLogger(getClass());
	}

	@RequestMapping(value = { "/", "" }, method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Map<String, Object>> getEvents() throws IOException, InterruptedException {
		return getEvents(null);
	}
	
	@RequestMapping(value = { "/{calendarName}", "" }, method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Map<String, Object>> getEvents(@PathVariable("calendarName") String calendarName) throws IOException, InterruptedException {
		List<Map<String, Object>> result = new ArrayList<>();
		adapter.getAllEvents(calendarName).forEach(e -> {
			Map<String, Object> properties = new HashMap<>();

			adjustExcludedEndDate(e);
			StringBuilder msg = new StringBuilder();
			msg.append("Event: ").append(e.getSummary()).append(", Start=").append(e.getStart())
					.append(" skipped properties:");
			for (Entry<String, Object> entry : e.entrySet()) {

				Entry<String, Object> ev = map(entry);
				if (ev != null) {
					properties.put(ev.getKey(), ev.getValue());
				} else if (log.isTraceEnabled()) {
					msg.append("\n\t\"").append(entry.getKey()).append("\", value: ").append(entry.getValue());
				}
			}
			if (log.isTraceEnabled()) {
				log.trace(msg.toString());
			}
			result.add(properties);
		});
		return result;
	}

	private void adjustExcludedEndDate(com.google.api.services.calendar.model.Event e) {
		if (e.isEndTimeUnspecified() == false
				&& (e.getStart().getDate() != null || e.getStart().getDateTime().isDateOnly())) {
			EventDateTime end = e.getEnd();
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(end.getDate() != null ? end.getDate().getValue() : end.getDateTime().getValue());
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			end.setDate(new DateTime(String.format("%d-%02d-%02d", calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))));
		}
	}

	private Entry<String, Object> map(Entry<String, Object> entry) {
		Object value = entry.getValue();
		switch (entry.getKey()) {
		case "summary":
			entry = Maps.immutableEntry("title", value);
			break;
		case "start":
		case "end":
		case "creator":
		case "organizer":
			entry = Maps.immutableEntry(entry.getKey(), firstValue(value));
			break;
		case "created":
		case "updated":
		case "status":
		case "colorClass":
		case "id":
		case "location":
		case "description":
		case "sequence":
		case "calendarName":
		case "attendees":
			entry = Maps.immutableEntry(entry.getKey(), value);
			break;
		default:
			entry = null;
		}
		return entry;
	}

	private String firstValue(Object value) {
		int index = -1;
		index = value.toString().indexOf(':') + 2;
		String substring = value.toString().substring(index, value.toString().indexOf('\"', index));
		return substring;
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
