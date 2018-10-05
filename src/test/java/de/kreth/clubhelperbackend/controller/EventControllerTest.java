package de.kreth.clubhelperbackend.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Event.Creator;
import com.google.api.services.calendar.model.EventDateTime;

import de.kreth.clubhelperbackend.google.calendar.CalendarAdapter;
import de.kreth.clubhelperbackend.testutils.MockedLogger;

public class EventControllerTest {
	
	private EventController controller;
	private EventDateTime eventDateTime;
	private Event event;
	private DateTime dateTime;
	@Mock
	public CalendarAdapter adapter;
	private List<Event> eventResult;
	@Mock
	private ServletRequest request;
	
	private Logger logger;

	@Before
	public void initController() throws GeneralSecurityException, IOException, InterruptedException {
		MockitoAnnotations.initMocks(this);
		logger = MockedLogger.mock();
		eventDateTime = new EventDateTime();
		event = new Event();
		event.setStart(eventDateTime);
				
		dateTime = new DateTime(new Date());
		
		eventResult = new ArrayList<>();
		controller = new EventController(adapter, logger);
		when(adapter.getAllEvents(any(ServletRequest.class))).thenReturn(eventResult);
	}

	@Test
	public void initDefaultConstructor() throws GeneralSecurityException, IOException {
		assertNotEquals(CalendarAdapter.class, controller.adapter.getClass());
		controller = new EventController();
		assertNotNull(controller.adapter);
		assertEquals(CalendarAdapter.class, controller.adapter.getClass());
	}
	
	@Test
	public void testEventMapping() throws IOException, InterruptedException {
		eventResult.add(fromJson(jsonEventDatesOnly));
		eventResult.add(fromJson(jsonEventDateTimes));
		eventResult.add(fromJson(jsonEventWithAttendees));
		List<Map<String, Object>> result = controller.getEvents(request);
		assertNotNull(result);
		assertEquals(3, result.size());
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
	
	@Test
	public void parseJson() throws IOException {
		Event ev = fromJson(jsonEventDatesOnly);
		assertEquals("Sommercamp Wunstorf", ev.getSummary());
		testDate(ev.getStart(), 29, Calendar.JULY, 2017);
		testDate(ev.getEnd(), 2, Calendar.AUGUST, 2017);
		testDateTime(ev.getCreated(), 25, Calendar.MARCH, 2017, 18, 21, 20);
		Creator creator = ev.getCreator();
		testCreator(creator, "bergmann.jasmin@googlemail.com");
	}

	private void testCreator(Creator creator, String email) {
		assertNotNull(creator);
		assertEquals(email, creator.getEmail());
	}

	private void testDateTime(DateTime dateTime, int day, int month, int year, int k, int l, int m) {
		testDate(dateTime, day, month, year);
	}

	public void testDate(EventDateTime dateTime, int day, int month, int year) {
		assertNull(dateTime.getDateTime());
		DateTime date = dateTime.getDate();
		assertTrue(date.isDateOnly());
		testDate(date, day, month, year);
	}

	public void testDate(DateTime date, int day, int month, int year) {
		assertNotNull(date);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date.getValue());
		assertEquals(day, cal.get(Calendar.DAY_OF_MONTH));
		assertEquals(month, cal.get(Calendar.MONTH));
		assertEquals(year, cal.get(Calendar.YEAR));
	}
	
	private Event fromJson(String jsonEventDatesOnly2) throws JsonParseException, JsonMappingException, IOException {
		JsonObjectParser parser = JacksonFactory.getDefaultInstance().createJsonObjectParser();
		return parser.parseAndClose(new StringReader(jsonEventDatesOnly), Event.class);
		
	}

	String jsonEventDatesOnly = "{\n" + 
			"  \"created\" : \"2017-03-25T18:21:20.000Z\",\n" + 
			"  \"creator\" : {\n" + 
			"    \"email\" : \"bergmann.jasmin@googlemail.com\"\n" + 
			"  },\n" + 
			"  \"end\" : {\n" + 
			"    \"date\" : \"2017-08-02\"\n" + 
			"  },\n" + 
			"  \"etag\" : \"\\\"2980932160514000\\\"\",\n" + 
			"  \"htmlLink\" : \"https://www.google.com/calendar/event?eid=a3EwYnFha3QzYjU5aWRkcTkzbmM2NGhoc2MgOTRib2kwYmNvOWwxNG9pbmJqZDNxNDlqNWdAZw\",\n" + 
			"  \"iCalUID\" : \"kq0bqakt3b59iddq93nc64hhsc@google.com\",\n" + 
			"  \"id\" : \"kq0bqakt3b59iddq93nc64hhsc\",\n" + 
			"  \"kind\" : \"calendar#event\",\n" + 
			"  \"organizer\" : {\n" + 
			"    \"displayName\" : \"mtv_allgemein\",\n" + 
			"    \"email\" : \"94boi0bco9l14oinbjd3q49j5g@group.calendar.google.com\",\n" + 
			"    \"self\" : true\n" + 
			"  },\n" + 
			"  \"reminders\" : {\n" + 
			"    \"useDefault\" : false\n" + 
			"  },\n" + 
			"  \"sequence\" : 1,\n" + 
			"  \"start\" : {\n" + 
			"    \"date\" : \"2017-07-29\"\n" + 
			"  },\n" + 
			"  \"status\" : \"tentative\",\n" + 
			"  \"summary\" : \"Sommercamp Wunstorf\",\n" + 
			"  \"updated\" : \"2017-03-25T18:21:20.257Z\",\n" + 
			"  \"colorClass\" : \"color2\"\n" + 
			"}";
	
	String jsonEventDateTimes = "{\n" + 
			"  \"created\" : \"2017-12-12T10:13:22.000Z\",\n" + 
			"  \"creator\" : {\n" + 
			"    \"displayName\" : \"Markus Kreth\",\n" + 
			"    \"email\" : \"mk21hann2@googlemail.com\"\n" + 
			"  },\n" + 
			"  \"description\" : \"Weihnachtsfeier der Trampolliner. Alle Verwandten sind herzlich eingeladen! Weihnachtsgebäck und/oder -Früchte bringen die richtige Stimmung.\",\n" + 
			"  \"end\" : {\n" + 
			"    \"dateTime\" : \"2017-12-20T19:30:00.000+01:00\"\n" + 
			"  },\n" + 
			"  \"etag\" : \"\\\"3026147204982000\\\"\",\n" + 
			"  \"htmlLink\" : \"https://www.google.com/calendar/event?eid=NTMybmxrY2gwcnVhbjNyaHFnaXNmZ24yZ3QgOTRib2kwYmNvOWwxNG9pbmJqZDNxNDlqNWdAZw\",\n" + 
			"  \"iCalUID\" : \"532nlkch0ruan3rhqgisfgn2gt@google.com\",\n" + 
			"  \"id\" : \"532nlkch0ruan3rhqgisfgn2gt\",\n" + 
			"  \"kind\" : \"calendar#event\",\n" + 
			"  \"location\" : \"Halle 2 IGS Roderbruch\",\n" + 
			"  \"organizer\" : {\n" + 
			"    \"displayName\" : \"mtv_allgemein\",\n" + 
			"    \"email\" : \"94boi0bco9l14oinbjd3q49j5g@group.calendar.google.com\",\n" + 
			"    \"self\" : true\n" + 
			"  },\n" + 
			"  \"reminders\" : {\n" + 
			"    \"useDefault\" : true\n" + 
			"  },\n" + 
			"  \"sequence\" : 0,\n" + 
			"  \"start\" : {\n" + 
			"    \"dateTime\" : \"2017-12-20T18:00:00.000+01:00\"\n" + 
			"  },\n" + 
			"  \"status\" : \"confirmed\",\n" + 
			"  \"summary\" : \"Weihnachtsfeier\",\n" + 
			"  \"updated\" : \"2017-12-12T10:13:22.491Z\",\n" + 
			"  \"colorClass\" : \"color2\"\n" + 
			"}";
	
	String jsonEventWithAttendees = "{\n" + 
			"  \"attendees\" : [ {\n" + 
			"    \"email\" : \"markus.kreth@web.de\",\n" + 
			"    \"responseStatus\" : \"needsAction\"\n" + 
			"  }, {\n" + 
			"    \"email\" : \"bergmann.jasmin@googlemail.com\",\n" + 
			"    \"responseStatus\" : \"needsAction\"\n" + 
			"  } ],\n" + 
			"  \"created\" : \"2017-12-18T07:11:59.000Z\",\n" + 
			"  \"creator\" : {\n" + 
			"    \"email\" : \"bergmann.jasmin@googlemail.com\"\n" + 
			"  },\n" + 
			"  \"end\" : {\n" + 
			"    \"dateTime\" : \"2018-01-12T20:00:00.000+01:00\",\n" + 
			"    \"timeZone\" : \"Europe/Berlin\"\n" + 
			"  },\n" + 
			"  \"etag\" : \"\\\"3028141632568000\\\"\",\n" + 
			"  \"guestsCanInviteOthers\" : false,\n" + 
			"  \"htmlLink\" : \"https://www.google.com/calendar/event?eid=azN0ZGI4amRnbXBudm11a3N0NHI2OXZpcGsgNTFmZ2F0cm5pcWV1MGd2MzlhOGtsYWlndjBAZw\",\n" + 
			"  \"iCalUID\" : \"k3tdb8jdgmpnvmukst4r69vipk@google.com\",\n" + 
			"  \"id\" : \"k3tdb8jdgmpnvmukst4r69vipk\",\n" + 
			"  \"kind\" : \"calendar#event\",\n" + 
			"  \"organizer\" : {\n" + 
			"    \"displayName\" : \"mtv_wettkampf\",\n" + 
			"    \"email\" : \"51fgatrniqeu0gv39a8klaigv0@group.calendar.google.com\",\n" + 
			"    \"self\" : true\n" + 
			"  },\n" + 
			"  \"reminders\" : {\n" + 
			"    \"useDefault\" : true\n" + 
			"  },\n" + 
			"  \"sequence\" : 1,\n" + 
			"  \"start\" : {\n" + 
			"    \"dateTime\" : \"2018-01-12T18:00:00.000+01:00\",\n" + 
			"    \"timeZone\" : \"Europe/Berlin\"\n" + 
			"  },\n" + 
			"  \"status\" : \"tentative\",\n" + 
			"  \"summary\" : \"Training Laatzen: Nika, evtl. Katharina\",\n" + 
			"  \"updated\" : \"2017-12-23T23:13:36.284Z\",\n" + 
			"  \"colorClass\" : \"color1\"\n" + 
			"}";
}
