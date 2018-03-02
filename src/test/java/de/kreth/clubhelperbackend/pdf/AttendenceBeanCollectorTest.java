package de.kreth.clubhelperbackend.pdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.ibm.icu.util.Calendar;

import de.kreth.testutils.sql.TestResultset;

public class AttendenceBeanCollectorTest {

	private AttendenceBeanCollector creator;
	private TestResultset rs;
	
	@Before
	public void init() {
		creator = new AttendenceBeanCollector();
		rs = new TestResultset();
	}
	
	@Test
	public void listWithDifferentDates() throws SQLException {
		insertTestValues(1);
		insertTestValues(2);
		List<AttendenceBean> list = creator.getList(rs);
		assertEquals(4, list.size());
		AttendenceBean attendenceBean = list.get(0);
		assertEquals("Surname1, Prename1", attendenceBean.name);
		assertTrue(attendenceBean.set);
		attendenceBean = list.get(1);
		assertEquals("Surname1, Prename1", attendenceBean.name);
		assertFalse(attendenceBean.set);
		attendenceBean = list.get(2);
		assertEquals("Surname2, Prename2", attendenceBean.name);
		assertTrue(attendenceBean.set);
		attendenceBean = list.get(3);
		assertEquals("Surname2, Prename2", attendenceBean.name);
		assertFalse(attendenceBean.set);
	}

	private void insertTestValues(int index) {
		Map<String, Object> values = new HashMap<>();
		values.put("prename", "Prename" + index);
		values.put("surname", "Surname" + index);
		values.put("on_date", new java.sql.Date(new GregorianCalendar(2018, Calendar.JANUARY, index).getTime().getTime()));
		rs.add(values);
	}

	@Test
	public void testListFormatter() throws SQLException {

		insertTestValues(1);
		List<AttendenceBean> list = creator.getList(rs);
		String formatted = creator.format(list);
		assertNotNull(formatted);
		
		assertEquals(";01.01.2018\n"
				+ "Surname1, Prename1;X", formatted);
	}
	
	@Test
	public void testListFormatterTwoValues() throws SQLException {

		insertTestValues(1);
		insertTestValues(2);
		
		List<AttendenceBean> list = creator.getList(rs);
		String formatted = creator.format(list);
		assertNotNull(formatted);
		
		assertEquals(";01.01.2018;02.01.2018"
				+ "\nSurname1, Prename1;X;"
				+ "\nSurname2, Prename2;;X", formatted);
	}
}
