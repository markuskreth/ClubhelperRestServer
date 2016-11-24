package de.kreth.clubhelperbackend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.kreth.clubhelperbackend.controller.PersonController;
import de.kreth.clubhelperbackend.pojo.Person;

public class PersonControllerTest {

	private final Date birth = new GregorianCalendar(2000, Calendar.JUNE, 19, 13, 40, 0).getTime();
	private final Date now = new GregorianCalendar(2015, Calendar.JUNE, 19, 13, 40, 0).getTime();

	private PersonController controller;
	private StubDao<Person> dao;
	private ExtendedModelMap model;

	@Before
	public void setUp() {
		dao = new StubDao<Person>();
		controller = new PersonController(dao);
		model = new ExtendedModelMap();
	}

	@Test
	public void testInsert() throws JsonParseException, JsonMappingException, IOException {

		Person toCreate = new Person(-1L, "Markus", "Kreth", "Trainer", birth, null, null);

		Person created = controller.post(toCreate);

		assertEquals(1, dao.inserted.size());
		assertTrue("LastInsertId in Dao wasn't incremented!", dao.lastInsertId > 0);

		assertEquals("Markus", created.getPrename());
		assertEquals("Kreth", created.getSurname());
		assertEquals("Trainer", created.getType());
		assertEquals(birth, created.getBirth());
		assertEquals(dao.lastInsertId, created.getId());

	}

	@Test
	public void testUpdate() throws JsonParseException, JsonMappingException, IOException {

		Person p = new Person(2L, "Markus", "Kreth", "Trainer", birth, now, now);
		Person out = controller.put(2, p);
		assertEquals(1, dao.updated.size());

		assertEquals(2L, out.getId().longValue());
		assertEquals("Markus", out.getPrename());
		assertEquals("Kreth", out.getSurname());
		assertEquals("Trainer", out.getType());
		assertEquals(birth, out.getBirth());
		assertEquals(now, out.getCreated());
		assertTrue("Created not before changed!", out.getCreated().before(out.getChanged()));
	}

	@Test
	public void testDelete() {

		Person p = new Person(2L, "Markus", "Kreth", "Trainer", birth, now, now);
		dao.byId.put(2L, p);
		Person out = controller.delete(2L);

		// assertEquals(0, dao.deleted.size());
		// assertEquals(1, dao.updated.size());

		assertEquals((long) 2, out.getId().longValue());
		assertEquals("Markus", out.getPrename());
		assertEquals("Kreth", out.getSurname());
		assertEquals("Trainer", out.getType());
		assertEquals(birth, out.getBirth());
	}
}
