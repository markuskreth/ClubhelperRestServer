package de.kreth.clubhelperbackend.spreadsheet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JumpHeightSheetTest {
	
	static AtomicInteger testCount = new AtomicInteger(0);
	private static final Calendar testDate = new GregorianCalendar(2017, Calendar.OCTOBER, 30);
	private JumpHeightSheet test;

	@Before
	public void createTestSheet() throws IOException {
		test = SheetService.create(nextTitle());
		assertNotSame(JumpHeightSheet.INVALID, test);
	}

	private String nextTitle() {
		return "Tempöräres Test Sheet " + testCount.incrementAndGet();
	}
	
	@After
	public void deleteTestSheet() throws IOException {
		SheetService.delete(test);
	}
	
	@Test
	public void testGetSheetAndTitle() throws Exception {
		String title = test.getTitle();
		JumpHeightSheet clone = SheetService.get(title);
		assertNotNull(clone);
		assertEquals(title, clone.getTitle());

	}

	@Test
	public void addTask() throws Exception {
		String taskName = "TestTask";
		List<String> tasks = test.addTask(taskName);
		assertEquals(taskName, tasks.get(tasks.size() - 1));
	}

	@Test
	public void testDateList() throws Exception {
		List<Date> dates = test.getDates();
		assertNotNull(dates);
		assertEquals(0, dates.size());
	}
	
	@Test
	public void testDefaultTasks() throws Exception {
		List<String> tasks = test.getTasks();
		assertEquals(6, tasks.size());
		assertEquals("10Sprünge", tasks.get(0));
		assertEquals("10Hocken", tasks.get(1));
		assertEquals("P3", tasks.get(2));
		assertEquals("P4", tasks.get(3));
		assertEquals("P5", tasks.get(4));
		assertEquals("P6", tasks.get(5));
	}
	
	@Test
	public void addTaskValue() throws Exception {
		CellValue value = test.add("10Sprünge", testDate, 13.1);
		assertNotNull(value);
		assertEquals(13.1, value.getDouble(), .01);
	}
	@Test
	public void renameSheet() throws Exception {
		String name = "Renamed Sheet";
		test.setTitle(name);
		assertEquals(name, test.getTitle());
	}
	
	@Test
	public void testCreateAndDeleteSheet() throws Exception {
		String nextTi = nextTitle();
		JumpHeightSheet test = SheetService.create(nextTi);
		assertNotNull(test);
		assertNotSame(JumpHeightSheet.INVALID, test);
		assertEquals(nextTi, test.getTitle());
		
		SheetService.delete(test);
	}
}
