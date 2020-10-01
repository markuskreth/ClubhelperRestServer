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

import javax.servlet.ServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.common.collect.Maps;

import de.kreth.googleconnectors.calendar.CalendarAdapter;

@Controller
@RequestMapping("/events")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'STAFF')")
public class EventController {

	final CalendarAdapter adapter;
	private final Logger log;

	public EventController() throws GeneralSecurityException, IOException {
		this(new CalendarAdapter(), LoggerFactory.getLogger(EventController.class));
	}

	EventController(CalendarAdapter calendarAdapter, Logger logger) {
		adapter = calendarAdapter;
		log = logger;
	}

	@RequestMapping(value = {"/",
			""}, method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Map<String, Object>> getEvents(ServletRequest request)
			throws IOException, InterruptedException {
		List<Map<String, Object>> result = new ArrayList<>();
		adapter.getAllEvents(request.getServerName()).forEach(e -> {
			if (e.getSummary() != null) {

				Map<String, Object> properties = new HashMap<>();

				adjustExcludedEndDate(e);
				StringBuilder msg = new StringBuilder();
				EventDateTime start = e.getStart();
<<<<<<< HEAD
				if (start == null) {
					start = e.getOriginalStartTime();
				}
				msg.append("Event: ").append(e.getSummary()).append(", Start=")
						.append(start).append(" skipped properties:");
=======
				msg.append("Event: ").append(e.getSummary()).append(", Start=").append(start)
						.append(" skipped properties:");
>>>>>>> master
				for (Entry<String, Object> entry : e.entrySet()) {

					Entry<String, Object> ev = map(entry);
					if (ev != null) {
						properties.put(ev.getKey(), ev.getValue());
					} else if (log.isTraceEnabled()) {
						msg.append("\n\t\"").append(entry.getKey())
								.append("\", value: ").append(entry.getValue());
					}
				}
				if (log.isTraceEnabled()) {
					log.trace(msg.toString());
				}
				result.add(properties);
			}
		});
		return result;
	}

<<<<<<< HEAD
	private void adjustExcludedEndDate(
			com.google.api.services.calendar.model.Event e) {
=======
	private void adjustExcludedEndDate(com.google.api.services.calendar.model.Event e) {
>>>>>>> master
		if (e.isEndTimeUnspecified() == false && startIsFullDate(e)) {
			EventDateTime end = e.getEnd();
			if (end == null) {
				return;
			}
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(end.getDate() != null
					? end.getDate().getValue()
					: end.getDateTime().getValue());
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			end.setDate(new DateTime(
					String.format("%d-%02d-%02d", calendar.get(Calendar.YEAR),
							calendar.get(Calendar.MONTH) + 1,
							calendar.get(Calendar.DAY_OF_MONTH))));
		}
	}

	boolean startIsFullDate(com.google.api.services.calendar.model.Event e) {

		EventDateTime start = e.getStart();
		if (start == null) {
			start = e.getOriginalStartTime();
		}
<<<<<<< HEAD
		return (start.getDate() != null || (start.getDateTime() != null
				&& start.getDateTime().isDateOnly()));
=======
		return (start.getDate() != null || (start.getDateTime() != null && start.getDateTime().isDateOnly()));
>>>>>>> master
	}

	private Entry<String, Object> map(Entry<String, Object> entry) {
		Object value = entry.getValue();
		switch (entry.getKey()) {
			case "summary" :
				entry = Maps.immutableEntry("title", value);
				break;
			case "start" :
			case "end" :
			case "creator" :
			case "organizer" :
				entry = Maps.immutableEntry(entry.getKey(), firstValue(value));
				break;
			case "created" :
			case "updated" :
			case "status" :
			case "colorClass" :
			case "id" :
			case "location" :
			case "description" :
			case "sequence" :
			case "attendees" :
				entry = Maps.immutableEntry(entry.getKey(), value);
				break;
			default :
				entry = null;
		}
		return entry;
	}

	private String firstValue(Object value) {
		if (value == null) {
			return "";
		}
		String string = value.toString();
		if (string.contains(":") == false || string.contains("\"") == false) {
			return "";
		}
		int index = -1;
<<<<<<< HEAD
		index = value.toString().indexOf(':') + 2;
		String substring = value.toString().substring(index,
				value.toString().indexOf('\"', index));
=======
		index = string.indexOf(':') + 2;
		String substring = string.substring(index, string.indexOf('\"', index));
>>>>>>> master
		return substring;
	}

}
