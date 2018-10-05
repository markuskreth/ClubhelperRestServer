package de.kreth.clubhelperbackend.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;

import de.kreth.clubhelperbackend.google.spreadsheet.CellRange;
import de.kreth.clubhelperbackend.google.spreadsheet.CellValue;
import de.kreth.clubhelperbackend.google.spreadsheet.JumpHeightSheet;
import de.kreth.clubhelperbackend.google.spreadsheet.Sheets;
import de.kreth.clubhelperbackend.testutils.MockedLogger;

public class JumpHeightSheetControllerTest {

	@Mock
	Sheets service;
	@Mock
	private ServletRequest request;
	private Logger logger;
	
	private JumpHeightSheetController controller;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		logger = MockedLogger.mock();
		controller = new JumpHeightSheetController(service, logger);
	}

	
	@Test
	public void test() throws IOException, InterruptedException {
		List<String> titles = controller.getTitles(request);
		assertNotNull(titles);
	}

	@Test
	public void testGetByTitlesUnknown() throws IOException, InterruptedException {
		String title = "surname,prename";
		Sheet googleSheet = new Sheet();
		SheetProperties sheetProperties = new SheetProperties();
		sheetProperties.setTitle(title);
		googleSheet.setProperties(sheetProperties );
		JumpHeightSheet sheet = new JumpHeightSheet(googleSheet);

		when(service.get(request, title)).thenThrow(new IOException("Sheet with title \"" + title + "\" not found."));
		when(service.create(request, title)).thenReturn(sheet);

		controller.getByName(request, "prename", "surname");

		verify(service).create(any(ServletRequest.class), eq(title));
	}

	@Test
	public void testGetByTitles() throws IOException, InterruptedException {

		JumpHeightSheet sheet = mock(JumpHeightSheet.class);
		String title = "surname,prename";
		when(sheet.getTasks()).thenReturn(Arrays.asList("Task 1"));
		CellValue<Date> date = new CellValue<Date>(new Date(), 4, 4);
		when(sheet.getDates()).thenReturn(Arrays.asList(date));
		CellRange range = new CellRange.Builder()
				.add(0, 0, "3.5")
				.add(0, 1, "3.5")
				.add(0, 2, "3.5").build();
		when(sheet.getValues(anyString())).thenReturn(range );
		
		when(service.get(request, title)).thenReturn(sheet);
		
		Map<String, List<?>> result = controller.getByName(request, "prename", "surname");
		assertNotNull(result);
		assertFalse(result.isEmpty());
		verify(service).get(any(ServletRequest.class), eq(title));
		
	}
	
	@Test
	public void testCreateCompetitor() throws IOException, InterruptedException {
		String title = "surname,prename";
		Sheet googleSheet = new Sheet();
		SheetProperties sheetProperties = new SheetProperties();
		sheetProperties.setTitle(title);
		googleSheet.setProperties(sheetProperties );
		JumpHeightSheet sheet = new JumpHeightSheet(googleSheet);

		when(service.create(any(ServletRequest.class), anyString())).thenReturn(sheet);
		
		controller.createCompetitor(request, "prename", "surname");
		
		verify(service).create(request, title);
	}
}
