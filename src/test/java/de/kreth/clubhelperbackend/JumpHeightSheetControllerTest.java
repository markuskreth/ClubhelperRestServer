package de.kreth.clubhelperbackend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import de.kreth.clubhelperbackend.google.spreadsheet.CellValue;
import de.kreth.clubhelperbackend.google.spreadsheet.SheetService;

@Ignore
public class JumpHeightSheetControllerTest extends AbstractGoogleTests {

	@Test
	public void DateComparissonMatches() throws Exception {
		List<CellValue<Date>> dates = SheetService.get(request, "Langenhagen,Anna")
				.getDates();
		System.out.println(dates.get(0).getObject());
		Calendar date = new GregorianCalendar(2015, Calendar.MAY, 11, 17, 13,
				12);

		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		assertEquals(dates.get(0).getObject().toString(),
				date.getTime().toString());
		assertTrue(dates.get(0).getObject().equals(date.getTime()));
	}

}
