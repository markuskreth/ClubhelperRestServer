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

import org.apache.tiles.context.MapEntry;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.common.collect.Maps;

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
	public List<Map<String, Object>> getEvents() throws IOException {
		List<Map<String, Object>> result = new ArrayList<>();
		adapter.getAllEvents().forEach(e -> {
			Map<String, Object> events = new HashMap<>();

			adjustExcludedEndDate(e);
			for(Entry<String, Object> entry: e.entrySet()) {
				
				entry = map(entry);
				if(entry != null) {
					events.put(entry.getKey(), entry.getValue());
				}
			}

			result.add(events);
		});
		return result;
	}

	private void adjustExcludedEndDate(com.google.api.services.calendar.model.Event e) {
		if(e.isEndTimeUnspecified() == false && (e.getStart().getDate() !=null || e.getStart().getDateTime().isDateOnly())) {
			EventDateTime end = e.getEnd();
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(end.getDate()!=null?end.getDate().getValue():end.getDateTime().getValue());
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			end.setDate(new DateTime(String.format("%d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH))));
		}
	}

	private Entry<String, Object> map(Entry<String, Object> entry) {
		Object value = entry.getValue();
		switch(entry.getKey()) {
		case "summary":
			entry = new MapEntry<String, Object>("title", value, false);
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
			entry = Maps.immutableEntry(entry.getKey(), value.toString());
			break;
		default: 
			entry = null;
		}
		return entry;
	}

	private String firstValue(Object value) {
		int index = -1;
		index = value.toString().indexOf(':')+2;
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
