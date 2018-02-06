package de.kreth.clubhelperbackend.google.calendar;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;

import de.kreth.clubhelperbackend.google.GoogleBaseAdapter;

public class CalendarAdapter extends GoogleBaseAdapter {

	com.google.api.services.calendar.Calendar service;
	private Lock lock = new ReentrantLock();

	public CalendarAdapter() throws GeneralSecurityException, IOException {
		super();
		ExecutorService exec = Executors.newSingleThreadExecutor();
		exec.execute(new Runnable() {
			
			@Override
			public void run() {
				lock.lock();
				try {
					service = createService();
					if(log.isInfoEnabled()) {
						log.info(service.getClass().getName() + " created successfully.");
					}
				} catch (IOException e) {
					log.error("unable to create service for " + getClass(), e);
				} finally {
					lock.unlock();
					if(log.isDebugEnabled()) {
						log.debug("unlock " + CalendarAdapter.class.getName());
					}
				}
			}
			
		});
		exec.shutdown();
	}

	private com.google.api.services.calendar.Calendar createService()
			throws IOException {
		Credential credential = authorize();
		return new com.google.api.services.calendar.Calendar.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, credential)
						.setApplicationName(APPLICATION_NAME).build();
	}

	Calendar getCalendarBySummaryName(List<CalendarListEntry> items,
			String calendarSummary) throws IOException {

		String calendarId = null;
		for (CalendarListEntry e : items) {
			if (calendarSummary.equals(e.getSummary())) {
				calendarId = e.getId();
				break;
			}
		}
		if (calendarId == null) {
			throw new IllegalStateException(
					"Calendar " + calendarSummary + " not found!");
		}
		Calendar cal = service.calendars().get(calendarId).execute();
		return cal;
	}

	public List<Event> getAllEvents(String calendarName) throws IOException, InterruptedException {

		final List<Event> events = new ArrayList<>();
		if(lock.tryLock(10, TimeUnit.SECONDS)) {

			try {

				List<CalendarListEntry> items = getCalendarList();
				final long oldest = getOldest();

				ExecutorService exec;
				if(calendarName == null) {
					exec = Executors.newFixedThreadPool(2);
					exec.execute(new FetchEventsRunner(items, "mtv_wettkampf", events,
							oldest, "color1"));
					exec.execute(new FetchEventsRunner(items, "mtv_allgemein", events,
							oldest, "color2"));
					
				} else {
					exec = Executors.newSingleThreadExecutor();
					exec.execute(new FetchEventsRunner(items, calendarName, events,
							oldest, "color1"));
				}
				exec.shutdown();
				try { 
					exec.awaitTermination(20, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					log.error("Thread terminated - event list may be incomplete.", e);
				}
			} finally {
				lock.unlock();
			}
		} else {
			log.error("Unable to lock " + getClass() + " for Event List after");
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
			if (log.isWarnEnabled()) {
				log.warn("Error fetching Calendar List, trying token refresh",
						e);
			}
			credential.refreshToken();
			if (log.isInfoEnabled()) {
				log.info("Successfully refreshed Google Security Token.");
			}
			calendarList = service.calendarList().list().execute();
		}
		return calendarList.getItems();
	}

	private final class FetchEventsRunner implements Runnable {
		private final List<Event> events;
		private final long oldest;
		private String colorClass;
		private List<CalendarListEntry> items;
		private String summary;

		private FetchEventsRunner(List<CalendarListEntry> items, String summary,
				List<Event> events, long oldest, String colorClass) {
			this.events = events;
			this.oldest = oldest;
			this.items = items;
			this.summary = summary;
			this.colorClass = colorClass;
		}

		@Override
		public void run() {

			try {
				log.debug("Fetching events of calendar \"" + summary + "\"");
				Calendar calendar = getCalendarBySummaryName(items, summary);
				DateTime timeMin = new DateTime(oldest);
				List<Event> items = service.events().list(calendar.getId())
						.setTimeMin(timeMin).execute().getItems();
				items.forEach(item -> {
					item.set("colorClass", colorClass);
					item.set("calendarName", summary);
				});
				events.addAll(items);
				log.debug("Added " + items.size() + " Events for \"" + summary + "\"");
				
			} catch (IOException e) {
				log.error("Unable to fetch Events from " + summary, e);
			}
		}
	}

	static Event createDefaultEvent(String summary) {
		Event ev = new Event().setGuestsCanInviteOthers(false)
				.setGuestsCanModify(false).setGuestsCanSeeOtherGuests(true)
				.setSummary(summary);
		List<EventAttendee> attendees = new ArrayList<>();
		ev.setAttendees(attendees);
		return ev;
	}

}
