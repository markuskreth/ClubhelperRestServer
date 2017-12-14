package de.kreth.clubhelperbackend.google.calendar;

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

import de.kreth.clubhelperbackend.google.GoogleBaseAdapter;

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

	com.google.api.services.calendar.model.Calendar getCalendarBySummaryName(List<CalendarListEntry> items, String calendarSummary) throws IOException {

		String wettkampfId = null;
		for(CalendarListEntry e: items) {
			if(calendarSummary.equals(e.getSummary())) {
				wettkampfId = e.getId();
				break;
			}
		}
		if(wettkampfId == null) {
			throw new IllegalStateException("Calendar " + calendarSummary + " not found!");
		}
		return service.calendars().get(wettkampfId).execute();
	}

	public List<Event> getAllEvents() throws IOException {

		List<CalendarListEntry> items = getCalendarList();
		final long oldest = getOldest();
		
		final List<Event> events = new ArrayList<>();
		ExecutorService exec = Executors.newFixedThreadPool(2);
		exec.execute(new FetchEventsRunner(getCalendarBySummaryName(items, "mtv_wettkampf"), events, oldest, "color1"));
		exec.execute(new FetchEventsRunner(getCalendarBySummaryName(items, "mtv_allgemein"), events, oldest, "color2"));
		exec.shutdown();
		try {
			exec.awaitTermination(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.error("Thread terminated - event list may be incomplete.", e);
		}
		return events;
	}

	private long getOldest() {
		GregorianCalendar oldestCal = new GregorianCalendar();
		oldestCal.set(java.util.Calendar.DAY_OF_MONTH, 1);
		oldestCal.set(java.util.Calendar.HOUR_OF_DAY, 0);
		oldestCal.set(java.util.Calendar.MINUTE, 0);
		oldestCal.add(java.util.Calendar.MONTH, -1);
		oldestCal.add(java.util.Calendar.HOUR_OF_DAY, -1);
		final long oldest = oldestCal.getTimeInMillis();
		return oldest;
	}
	
	List<CalendarListEntry> getCalendarList() throws IOException {
		checkRefreshToken();
		CalendarList calendarList;
		try {
			calendarList = service.calendarList().list().execute();
		} catch (IOException e) {
			if(log.isWarnEnabled()) {
				log.warn("Error fetching Calendar List, trying token refresh", e);
			}
			credential.refreshToken();
			if(log.isInfoEnabled()) {
				log.info("Successfully refreshed Google Security Token.");
			}
			calendarList = service.calendarList().list().execute();
		}
		return calendarList.getItems();
	}

	private final class FetchEventsRunner implements Runnable {
		private final List<Event> events;
		private final long oldest;
		private final com.google.api.services.calendar.model.Calendar calendar;
		private String colorClass;

		private FetchEventsRunner(com.google.api.services.calendar.model.Calendar calendar, List<Event> events, long oldest, String colorClass) {
			this.events = events;
			this.oldest = oldest;
			this.calendar = calendar;
			this.colorClass = colorClass;
		}

		@Override
		public void run() {

			try {
				events.addAll(service.events().list(calendar.getId()).execute().getItems()
						.parallelStream()
						.filter(e -> {
							EventDateTime start = e.getStart();
							e.set("colorClass", colorClass);
							DateTime dateTime = start.getDate()==null?start.getDateTime():start.getDate();
							if(dateTime == null) {
								if(log.isWarnEnabled()) {
									try {
										
										log.warn("Event without startDate: " + e.toPrettyString() + "\n\nStart=" + start.toPrettyString());
									} catch (IOException e1) {
										log.warn("Logging impossible.", e1);
									}
								}
								return false;
							}
							return dateTime.getValue()>oldest;
						})
						.collect(Collectors.toList()));
				
			} catch (IOException e) {
				log.error("Unable to fetch Events from " + calendar.getSummary(), e);
			}
		}
	}

}
