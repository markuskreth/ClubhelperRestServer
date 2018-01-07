package de.kreth.clubhelperbackend.testutils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.kreth.clubhelperbackend.pojo.Person;

public enum TestDataPerson {

	INSTANCE;

	public final Date birth = new GregorianCalendar(2000, Calendar.JUNE, 19, 13,
			40, 0).getTime();
	public final Date now = new GregorianCalendar(2015, Calendar.JUNE, 19, 13,
			40, 0).getTime();

	public static Person getPerson() {
		return new Person(2L, "Markus", "Kreth", INSTANCE.birth, INSTANCE.now,
				INSTANCE.now);
	}

	public static Person getPerson2() {
		return new Person(3L, "Jane", "Doe",
				new GregorianCalendar(1980, Calendar.JANUARY, 1).getTime(),
				INSTANCE.now, INSTANCE.now);
	}

	public static Person getPersonWithoutCreateChange() {
		return new Person(-1L, "Markus", "Kreth", INSTANCE.birth, null, null);
	}
}
