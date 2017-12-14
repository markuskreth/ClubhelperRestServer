package de.kreth.clubhelperbackend.google.calendar;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;

import de.kreth.clubhelperbackend.google.calendar.CalendarAdapter;

public class CalendarAdapterTest {

	private CalendarAdapter adapter;

	@Before
	public void initAdapter() throws GeneralSecurityException, IOException {

		adapter = new CalendarAdapter();
		assertNotNull(adapter.service);
	}
	
	@Test
	public void testInit() throws GeneralSecurityException, IOException {
		
		List<CalendarListEntry> items = adapter.getCalendarList();
		assertTrue(items.size()>0);
		Calendar wettkampf = adapter.getCalendarBySummaryName(items, "mtv_wettkampf");
		assertNotNull(wettkampf);
	}

	@Test
	public void getWettkampfEvents() throws IOException {
		List<Event> events = adapter.service.events().list(adapter.getCalendarBySummaryName(adapter.getCalendarList(), "mtv_wettkampf").getId()).execute().getItems();
		assertNotNull(events);
		assertTrue("No events found!", events.size()>0);
		Event first = events.get(0);
		System.out.println(String.join(", ", 
				first.getStart().getDateTime().toStringRfc3339(), 
				first.getEnd().getDateTime().toStringRfc3339(), 
				first.getSummary(),
				first.getLocation(),
				first.getCreator().getDisplayName()
				)
			);
		System.out.println(first.toPrettyString());
		System.out.println("found: " + events.size());
	}
	
	@Test
	public void testCreateCalendar() throws GeneralSecurityException, IOException {
		CalendarAdapter adapter = new CalendarAdapter();
		Calendar cal = new Calendar();
		String id = "AutomatedTestCalendar";
		cal.setDescription(id);
		cal.setSummary(id);

		cal = adapter.service.calendars().insert(cal).execute();
		assertNotNull(cal);
		adapter.service.calendars().delete(cal.getId()).execute();
	}
}
