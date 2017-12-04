package de.kreth.clubhelperbackend.calendar;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.junit.Test;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;

public class CalendarAdapterTest {

	@Test
	public void testInit() throws GeneralSecurityException, IOException {
		CalendarAdapter adapter = new CalendarAdapter();
		assertNotNull(adapter.service);
		
		com.google.api.services.calendar.Calendar.CalendarList exec = adapter.service.calendarList();

		CalendarList calendarList = exec.list().execute();

		List<CalendarListEntry> items = calendarList.getItems();
		assertTrue(items.size()>0);
		String wettkampfId = null;
		for(CalendarListEntry e: items) {
			if("mtv_wettkampf".equals(e.getSummary())) {
				wettkampfId = e.getId();
			}
		}
		Calendar wettkampf = adapter.service.calendars().get(wettkampfId).execute();
		assertNotNull(wettkampf);
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
