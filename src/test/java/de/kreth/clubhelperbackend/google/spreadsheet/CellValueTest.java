package de.kreth.clubhelperbackend.google.spreadsheet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;

import org.junit.Test;

public class CellValueTest {

	@Test
	public void testCellValue() {
		new CellValue<>("test1", 1, 1);
		new CellValue<>(10, 1, 1);
		new CellValue<>(new GregorianCalendar(), 1, 1);
	}

	@Test(expected=AssertionError.class)
	public void testNullException() {
		new CellValue<>((String)null, 1, 1);
	}
	@Test
	public void testGetObject() {

		CellValue<String> v1 = new CellValue<>("test1", 1, 1);
		assertEquals("test1", v1.getObject());
		CellValue<Integer> v2 = new CellValue<>(10, 1, 1);
		assertEquals(10, v2.getObject().intValue());
		GregorianCalendar object = new GregorianCalendar();
		CellValue<Calendar> v3 = new CellValue<>(object, 1, 1);
		assertSame(object, v3.getObject());
	}

	@Test
	public void testGetColumn() {
		for (int row=1; row<3; row++) {
			for (int column = 1; column<3; column++) {
				CellValue<Boolean> v = new CellValue<Boolean>(true, column, row);
				assertEquals(row, v.getRow());
				assertEquals(column, v.getColumn());
			}
		}
	}

	@Test
	public void testToString() {
		assertEquals("CellValue A1=test1", new CellValue<>("test1", 1, 1).toString());
		assertEquals("CellValue M13=test1", new CellValue<>("test1", 13, 13).toString());
	}

	@Test
	public void testEquals() {

		CellValue<String> v1 = new CellValue<>("test1", 1, 1);
		assertTrue(v1.equals(v1));
		assertTrue(v1.equals(new CellValue<>("test1", 1, 1)));
		assertFalse(v1.equals(new CellValue<>("test", 1, 1)));
		assertFalse(v1.equals(new CellValue<>("test1", 1, 2)));
		assertFalse(v1.equals(new CellValue<>("test1", 2, 1)));
		assertFalse(v1.equals(null));
		CellValue<Integer> v2 = new CellValue<>(10, 1, 1);
		assertFalse(v2.equals(v1));
		assertTrue(new HashSet<>(Arrays.asList(v1)).contains(v1));
	}
}
