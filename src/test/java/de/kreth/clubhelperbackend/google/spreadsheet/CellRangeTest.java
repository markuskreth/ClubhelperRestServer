package de.kreth.clubhelperbackend.google.spreadsheet;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class CellRangeTest {

	CellRange range;
	
	@Before
	public void initRange() {
		range = new CellRange.Builder()
				.add(2, 3, "2,3")
				.add(2, 4, "2,4")
				.add(2, 5, "2,5")
				.add(3, 3, "3,3")
				.add(3, 4, "3,4")
				.add(3, 5, "3,5")
				.build();
	}
	@Test
	public void testGetValues() {
		List<List<String>> values = range.getValues();
		assertEquals(6, values.size());
		assertEquals(0, values.get(0).size());
		assertEquals(0, values.get(1).size());
		assertEquals(0, values.get(2).size());
		assertEquals(4, values.get(3).size());
		assertEquals(4, values.get(4).size());
	}

	@Test
	public void testGetValue() {
		for (int column = 2; column<4;column++) {
			for (int row = 3; row<6; row++) {
				assertEquals(new StringBuilder().append(column).append(",").append(row).toString(), range.getValue(column, row));
			}
		}
	}

	@Test
	public void testToString() {
		assertEquals(", , 2,3, 3,3\n" + 
				", , 2,4, 3,4\n" + 
				", , 2,5, 3,5", range.toString());
	}

}
