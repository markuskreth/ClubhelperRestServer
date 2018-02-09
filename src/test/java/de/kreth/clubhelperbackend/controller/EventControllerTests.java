package de.kreth.clubhelperbackend.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import de.kreth.clubhelperbackend.google.calendar.CalendarAdapter;

public class EventControllerTests {

	private EventController controller;
	private CalendarAdapter adapter;

	@Before
	public void setUp() throws Exception {
		adapter = mock(CalendarAdapter.class);
		controller = new EventController(adapter);
	}

	@Test
	public void testEventMapping() throws IOException, InterruptedException {
		List<Event> eventValues = new ArrayList<>();
		Event ev = new Event();

		ev.setStart(new EventDateTime().setDate(new DateTime("2018-01-01")));
		ev.setEnd(new EventDateTime().setDateTime(new DateTime("2018-01-03")));
		
		eventValues.add(ev);
		when(adapter.getAllEvents(ArgumentMatchers.<String>isNull())).thenReturn(eventValues);
		
		List<Map<String, Object>> events = controller.getEvents();
		assertNotNull(events);
		assertEquals(1, events.size());
	}

	@Test
	public void testDateTimeRecognition() {
		Object o = new EventDateTime().setDate(new DateTime("2018-01-01"));
		assertEquals(EventDateTime.class, o.getClass());
		EventDateTime date = (EventDateTime) o;
		assertTrue(date.getDate().isDateOnly());
		Calendar cal = new GregorianCalendar(2018, Calendar.JANUARY, 1);

		cal.setTimeZone(TimeZone.getTimeZone("Etc/GMT+0"));
		assertEquals(cal.getTimeInMillis(), date.getDate().getValue());
	}
}
