package de.kreth.clubhelperbackend.spreadsheet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JumpHeightSheetTest {
	
	AtomicInteger testCount = new AtomicInteger(0);
	
	static final String annasTitle = "Anna";
	private JumpHeightSheet test;

	@Before
	public void createTestSheet() throws IOException {
		test = SheetService.INSTANCE.create(nextTitle());
	}

	private String nextTitle() {
		return "Tempöräres Test Sheet " + testCount.incrementAndGet();
	}
	
	@After
	public void deleteTestSheet() throws IOException {
		SheetService.INSTANCE.delete(test);
	}
	
	@Test
	public void testAnnasSheetAndTitle() throws Exception {
		String title = test.getTitle();
		JumpHeightSheet clone = SheetService.INSTANCE.get(title);
		assertNotNull(clone);
		assertEquals(title, clone.getTitle());

	}

	@Test
	public void testDateList() throws Exception {
		List<Date> dates = test.getDates();
		assertNotNull(dates);
		assertEquals(0, dates.size());
	}
	
	@Test
	public void testCreateAndDeleteSheet() throws Exception {
		String nextTi = nextTitle();
		JumpHeightSheet test = SheetService.INSTANCE.create(nextTi);
		assertNotNull(test);
		assertNotSame(JumpHeightSheet.INVALID, test);
		assertEquals(nextTi, test.getTitle());
		
		SheetService.INSTANCE.delete(test);
	}
}
