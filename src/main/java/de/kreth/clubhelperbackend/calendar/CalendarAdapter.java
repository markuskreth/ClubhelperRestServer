package de.kreth.clubhelperbackend.calendar;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import de.kreth.clubhelperbackend.spreadsheet.GoogleBaseAdapter;

public class CalendarAdapter extends GoogleBaseAdapter {

	Calendar service;

	public CalendarAdapter() throws GeneralSecurityException, IOException {
		super();
		service = createService();
	}

	private Calendar createService() throws IOException {
		Credential credential = authorize();
		return new com.google.api.services.calendar.Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME).build();
	}

	com.google.api.services.calendar.model.Calendar getWettkampf(List<CalendarListEntry> items) throws IOException {

		String wettkampfId = null;
		for(CalendarListEntry e: items) {
			if("mtv_wettkampf".equals(e.getSummary())) {
				wettkampfId = e.getId();
				break;
			}
		}
		if(wettkampfId == null) {
			throw new IllegalStateException("Calendar mtv_wettkampf not found!");
		}
		return service.calendars().get(wettkampfId).execute();
	}

	public List<Event> getAllEvents() throws IOException {
		List<CalendarListEntry> items = getCalendarList();
		com.google.api.services.calendar.model.Calendar wettkampf = getWettkampf(items);
		final List<Event> events = new ArrayList<>();
		ExecutorService exec = Executors.newFixedThreadPool(2);
		GregorianCalendar now = new GregorianCalendar();
		now.set(java.util.Calendar.DAY_OF_MONTH, 1);
		now.set(java.util.Calendar.HOUR_OF_DAY, 0);
		now.add(java.util.Calendar.MONTH, -1);
		now.add(java.util.Calendar.HOUR_OF_DAY, -1);
		final long oldest = now.getTimeInMillis();
		exec.execute(new Runnable() {
			
			@Override
			public void run() {
				
				try {
					events.addAll(service.events().list(wettkampf.getId()).execute().getItems()
							.parallelStream()
							.filter(e -> {
								EventDateTime start = e.getStart();
								DateTime dateTime = start.getDate()==null?start.getDateTime():start.getDate();
								if(dateTime == null) {

									try {
										log.warn("Event without startDate: " + e.toPrettyString() + "\n\nStart=" + start.toPrettyString());
									} catch (IOException e1) {
										log.warn("Logging impossible.", e1);
									}
									return false;
								}
								return dateTime.getValue()>oldest;
							})
							.collect(Collectors.toList()));
					
				} catch (IOException e) {
					log.error("Unable to fetch Events from " + wettkampf.getSummary(), e);
				}
			}
		});
		exec.shutdown();
		try {
			exec.awaitTermination(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.error("Thread terminated - event list may be incomplete.", e);
		}
		return events;
	}
	
	List<CalendarListEntry> getCalendarList() throws IOException {
			CalendarList calendarList = service.calendarList().list().execute();
			return calendarList.getItems();
	}
}
