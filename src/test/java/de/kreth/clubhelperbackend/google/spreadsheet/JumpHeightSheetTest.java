package de.kreth.clubhelperbackend.google.spreadsheet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;

public class JumpHeightSheetTest {

	private static final Calendar testDate = new GregorianCalendar(2017,
			Calendar.OCTOBER, 30);
	private static final String SHEET_TITLE = "Testsheet Title";
	private JumpHeightSheet test;
	private Sheet mockedSheet;
	private SheetProperties properties;
	private GoogleSpreadsheetsAdapter service;

	@Before
	public void createTestSheet() throws IOException {
		mockedSheet = mock(Sheet.class);
		properties = new SheetProperties();
		properties.setTitle(SHEET_TITLE);
		when(mockedSheet.getProperties()).thenReturn(properties);
		test = new JumpHeightSheet(mockedSheet);
		service = mock(GoogleSpreadsheetsAdapter.class);
		when(service.sendRequest(ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())).thenReturn(null);
		SheetService.INSTANCE.setService(service);
	}

	@Test
	public void testGetTitle() throws Exception {
		assertEquals(SHEET_TITLE, test.getTitle());
	}

}
