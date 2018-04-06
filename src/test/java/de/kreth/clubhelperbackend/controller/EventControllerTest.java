package de.kreth.clubhelperbackend.controller;

import static org.junit.Assert.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

public class EventControllerTest {
	
	private EventController controller;
	private EventDateTime eventDateTime;
	private Event event;
	private DateTime dateTime;

	@Before
	public void initController() throws GeneralSecurityException, IOException {
		eventDateTime = new EventDateTime();
		event = new Event();
		event.setStart(eventDateTime);
		
		dateTime = new DateTime(new Date());
		
		controller = new EventController();
	}

	@Test
	public void testStartIsFullDateDateOnly() {
		eventDateTime.setDate(dateTime);
		eventDateTime.setDateTime(null);
		assertTrue(controller.startIsFullDate(event));
	}

	@Test
	public void testStartIsFullDateDatetime() {
		eventDateTime.setDate(null);
		eventDateTime.setDateTime(dateTime);
		assertFalse(controller.startIsFullDate(event));
	}

	@Test
	public void testDateIsFullDate() {
		dateTime = new DateTime(new GregorianCalendar(2018, Calendar.APRIL, 13, 17, 7, 7).getTime());
		assertFalse(dateTime.isDateOnly());
		dateTime = new DateTime(true, new GregorianCalendar(2018, Calendar.APRIL, 13).getTime().getTime(), null);
		assertTrue(dateTime.isDateOnly());
	}
}
