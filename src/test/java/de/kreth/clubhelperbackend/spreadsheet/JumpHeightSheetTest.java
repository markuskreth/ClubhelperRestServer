package de.kreth.clubhelperbackend.spreadsheet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.Date;
import java.util.List;

import org.junit.Test;

public class JumpHeightSheetTest {
	
	static final String annasTitle = "Anna";

	@Test
	public void testAnnasSheetAndTitle() throws Exception {
		JumpHeightSheet anna = SheetService.INSTANCE.get(annasTitle);
		assertNotNull(anna);
		assertEquals(annasTitle, anna.getTitle());
	}

	@Test
	public void testAnnasDateList() throws Exception {
		JumpHeightSheet anna = SheetService.INSTANCE.get(annasTitle);
		List<Date> dates = anna.getDates();
		assertNotNull(dates);
		assertEquals(12, dates.size());
	}
	
	@Test
	public void testCreateSheet() throws Exception {
		JumpHeightSheet test = SheetService.INSTANCE.create("Temp Test Person");
		assertNotNull(test);
		assertNotSame(JumpHeightSheet.INVALID, test);
		assertEquals("Temp Test Person", test.getTitle());
		
	}
}
