package de.kreth.clubhelperbackend.testutils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.kreth.clubhelperbackend.pojo.Person;

public enum TestDataPerson {

	INSTANCE;

	public final Date birth = new GregorianCalendar(2000, Calendar.JUNE, 19, 13, 40, 0).getTime();
	public final Date now = new GregorianCalendar(2015, Calendar.JUNE, 19, 13, 40, 0).getTime();

	public final Person person;
	public final Person personWithoutCreateChange;
	public final Person person2;

	TestDataPerson() {
		personWithoutCreateChange = new Person(-1L, "Markus", "Kreth", birth, null, null);
		person = new Person(2L, "Markus", "Kreth", birth, now, now);
		person2 = new Person(3L, "Jane", "Doe", 
				new GregorianCalendar(1980, Calendar.JANUARY, 1).getTime(), now, now);
	}
}
