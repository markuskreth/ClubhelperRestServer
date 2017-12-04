package de.kreth.clubhelperbackend.calendar;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.calendar.Calendar;

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
}
