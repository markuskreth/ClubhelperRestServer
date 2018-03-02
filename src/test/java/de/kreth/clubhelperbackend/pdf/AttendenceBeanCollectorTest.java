package de.kreth.clubhelperbackend.pdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Arrays;
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
		assertFalse(attendenceBean.set);
		attendenceBean = list.get(3);
		assertEquals("Surname2, Prename2", attendenceBean.name);
		assertTrue(attendenceBean.set);
	}
	
	@Test
	public void allDatesDontDublicateAtt() throws SQLException {
		insertTestValues(1);
		insertTestValues(2);
		insertTestValues(1, 2);
		insertTestValues(3);

		List<AttendenceBean> list = creator.getList(rs);
		assertEquals(list.toString(), 9, list.size());
		List<Date> dates = Arrays.asList(
				new java.sql.Date(new GregorianCalendar(2018, Calendar.JANUARY, 1).getTime().getTime())
				,new java.sql.Date(new GregorianCalendar(2018, Calendar.JANUARY, 2).getTime().getTime())
				,new java.sql.Date(new GregorianCalendar(2018, Calendar.JANUARY, 3).getTime().getTime()));
		
		assertEquals("Surname1, Prename1", list.get(0).name);
		assertEquals(dates.get(0), list.get(0).date);
		assertTrue(list.get(0).set);
		
		assertEquals("Surname1, Prename1", list.get(1).name);
		assertEquals(dates.get(1), list.get(1).date);
		assertTrue(list.get(1).set);

		assertEquals("Surname1, Prename1", list.get(2).name);
		assertEquals(dates.get(2), list.get(2).date);
		assertFalse(list.get(2).set);

		assertEquals(dates.get(0), list.get(3).date);
		assertEquals("Surname2, Prename2", list.get(3).name);
		assertFalse(list.get(3).set);

		assertEquals("Surname2, Prename2", list.get(4).name);
		assertTrue(list.get(4).set);
	}
	
	private void insertTestValues(int index) {
		insertTestValues(index, index);
	}
	
	private void insertTestValues(int index, int day) {
		Map<String, Object> values = new HashMap<>();
		values.put("prename", "Prename" + index);
		values.put("surname", "Surname" + index);
		values.put("on_date", new java.sql.Date(new GregorianCalendar(2018, Calendar.JANUARY, day).getTime().getTime()));
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
	
	@Test
	public void testListFormatterTwoValuesReverse() throws SQLException {

		insertTestValues(1, 5);
		insertTestValues(2, 2);
		insertTestValues(3, 2);
		insertTestValues(3, 5);

		List<AttendenceBean> list = creator.getList(rs);
		String formatted = creator.format(list);
		assertNotNull(formatted);
		
		assertEquals(";02.01.2018;05.01.2018"
				+ "\nSurname1, Prename1;;X"
				+ "\nSurname2, Prename2;X;"
				+ "\nSurname3, Prename3;X;X", formatted);
	}
}
