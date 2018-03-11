package de.kreth.clubhelperbackend.google.calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.ServletRequest;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar.Calendars;
import com.google.api.services.calendar.Calendar.Calendars.Get;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;

import de.kreth.clubhelperbackend.AbstractGoogleTests;

@Ignore
public class CalendarAdapterTest extends AbstractGoogleTests {

	private static final String summaryText = "AutomatedTestCalendar";

	private CalendarAdapter adapter;
	private Calendar current;
	private com.google.api.services.calendar.Calendar service;
	
	@Before
	public void initAdapter() throws GeneralSecurityException, IOException {
		service= mock(com.google.api.services.calendar.Calendar.class);

	@After
	public void deleteCreatedCalendars() throws IOException {
		if (current != null) {
			adapter.service.calendars().delete(current.getId()).execute();
		}
	}

	@AfterClass
	public static void deleteAllCreatedCalendars()
			throws IOException, GeneralSecurityException {

		ServletRequest request = mock(ServletRequest.class);
		when(request.getLocalName()).thenReturn("localhost");
		when(request.getServerName()).thenReturn("localhost");
		when(request.getRemoteHost()).thenReturn("localhost");
		
		CalendarAdapter calendarAdapter = new CalendarAdapter();
		List<CalendarListEntry> items = calendarAdapter.getCalendarList(request);
		items.forEach(calEntr -> {
			if (summaryText.equals(calEntr.getSummary())) {
				System.out.println("Deleting " + calEntr.getSummary() + ": "
						+ calEntr.getId());
				try {
					calendarAdapter.service.calendars().delete(calEntr.getId())
							.execute();
				} catch (IOException e) {
					System.err.println(e);
					throw new RuntimeException(e);
				}
			}
			@Override
			protected void checkRefreshToken() throws IOException {
			}
		};
	}

	@Test
	public void testInit() throws GeneralSecurityException, IOException {

		List<CalendarListEntry> items = adapter.getCalendarList(request);
		assertTrue(items.size() > 0);
		Calendar wettkampf = adapter.getCalendarBySummaryName(items,
				"mtv_wettkampf");
		assertNotNull(wettkampf);
	}

	@Test
	public void getWettkampfEvents() throws IOException {
		Calendar wettkampf = adapter.getCalendarBySummaryName(
				adapter.getCalendarList(request), "mtv_wettkampf");
		List<Event> events = adapter.service.events().list(wettkampf.getId())
				.execute().getItems();
		assertNotNull(events);
		assertTrue("No events found!", events.size() > 0);
		Event first = events.get(0);
		System.out.println(String.join(", ",
				first.getStart().getDateTime().toStringRfc3339(),
				first.getEnd().getDateTime().toStringRfc3339(),
				first.getSummary(), first.getLocation(),
				first.getCreator().getDisplayName()));
		System.out.println(first.toPrettyString());
		System.out.println("found: " + events.size());
	}

	private void mockServiceWith(CalendarListEntry... entries) throws IOException {

		CalendarList list = mock(CalendarList.class);
		List<CalendarListEntry> values = new ArrayList<>();
		for(CalendarListEntry e: entries) {
			values.add(e);
		}
		when(list.getItems()).thenReturn(values);

		com.google.api.services.calendar.Calendar.CalendarList listQuery = mock(com.google.api.services.calendar.Calendar.CalendarList.class);
		assertNotNull(listQuery);
		when(service.calendarList()).thenReturn(listQuery);
		assertNotNull(service);
		com.google.api.services.calendar.Calendar.CalendarList.List queryExecutor = mock(com.google.api.services.calendar.Calendar.CalendarList.List.class);
		when(listQuery.list()).thenReturn(queryExecutor);
		when(queryExecutor.execute()).thenReturn(list);
	}

	@Test
	@Ignore
	public void testCreateCalendar()
			throws GeneralSecurityException, IOException {

		current = adapter.service.calendars().insert(createTestCalendar())
				.execute();
		assertNotNull(current);
	}

	private Calendar createTestCalendar() {
		Calendar cal = new Calendar();
		cal.setDescription(summaryText);
		cal.setSummary(summaryText);
		return cal;
	}

	@Test
	@Ignore
	public void addAttendeeToEvent() throws Exception {

		current = adapter.service.calendars().insert(createTestCalendar())
				.execute();
		Event ev = CalendarAdapter.createDefaultEvent("Testevent");

		GregorianCalendar time = new GregorianCalendar();
		time.set(GregorianCalendar.HOUR_OF_DAY, 16);
		EventDateTime start = new EventDateTime();
		start.setDateTime(new DateTime(time.getTimeInMillis()));

		ev.setStart(start);
		time.set(GregorianCalendar.HOUR_OF_DAY, 18);
		EventDateTime end = new EventDateTime();
		end.setDateTime(new DateTime(time.getTimeInMillis()));
		ev.setEnd(end);

		EventAttendee att1 = new EventAttendee();
		att1.setEmail("markus.kreth@web.de");
		att1.setDisplayName("Kreth, Markus");
		ev.getAttendees().add(att1);

		adapter.service.events().insert(current.getId(), ev).execute();

		List<Event> events = adapter.service.events().list(current.getId())
				.execute().getItems();
		assertNotNull(events);
		assertEquals(1, events.size());
		ev = events.get(0);
		List<EventAttendee> attendees = ev.getAttendees();
		assertNotNull(attendees);
		assertEquals(1, attendees.size());
		att1 = attendees.get(0);
		assertEquals("markus.kreth@web.de", att1.getEmail());
		assertEquals("Kreth, Markus", att1.getDisplayName());
	}

	@Test
	@Ignore
	public void addInvalidAttendeeToEvent() throws Exception {

		current = adapter.service.calendars().insert(createTestCalendar())
				.execute();
		Event ev = CalendarAdapter.createDefaultEvent("Testevent");

		GregorianCalendar time = new GregorianCalendar();
		time.set(GregorianCalendar.HOUR_OF_DAY, 16);
		EventDateTime start = new EventDateTime();
		start.setDateTime(new DateTime(time.getTimeInMillis()));

		ev.setStart(start);
		time.set(GregorianCalendar.HOUR_OF_DAY, 18);
		EventDateTime end = new EventDateTime();
		end.setDateTime(new DateTime(time.getTimeInMillis()));
		ev.setEnd(end);

		EventAttendee att1 = new EventAttendee();
		att1.setEmail("Single.Test@test.net");
		att1.setDisplayName("Person Name");
		ev.getAttendees().add(att1);

		adapter.service.events().insert(current.getId(), ev).execute();

		List<Event> events = adapter.service.events().list(current.getId())
				.execute().getItems();
		assertNotNull(events);
		assertEquals(1, events.size());
		ev = events.get(0);
		List<EventAttendee> attendees = ev.getAttendees();
		assertNotNull(attendees);
		assertEquals(1, attendees.size());

		att1 = attendees.get(0);
		assertEquals("Single.Test@test.net".toLowerCase(), att1.getEmail());
		assertEquals("Person Name", att1.getDisplayName());
	}
}
