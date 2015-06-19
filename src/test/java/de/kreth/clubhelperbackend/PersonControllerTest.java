package de.kreth.clubhelperbackend;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.kreth.clubhelperbackend.controller.PersonController;
import de.kreth.clubhelperbackend.pojo.Person;

public class PersonControllerTest {

	private final Date birth = new GregorianCalendar(2000, Calendar.JUNE, 19, 13, 40, 0).getTime();
	private final Date now = new GregorianCalendar(2015, Calendar.JUNE, 19, 13, 40, 0).getTime();
	private final ObjectMapper mapper = new ObjectMapper();
	private PersonController controller;
	private MockDao<Person> dao;
	private ExtendedModelMap model;
	
	@Before
	public void setUp() {
		dao = new MockDao<Person>(); 
		controller = new PersonController(dao);
		model = new ExtendedModelMap();
	}
	
	@Test
	public void testInsert() throws JsonParseException, JsonMappingException, IOException {
		
		String input = "{\"prename\":\"Markus\",\"surname\":\"Kreth\",\"type\":\"Trainer\",\"birth\":" + birth.getTime() + "}";
		String viewName = controller.create(input, model);
		
		assertEquals("output", viewName);
		assertEquals(1, dao.inserted.size());
		assertTrue("LastInsertId in Dao wasn't incremented!", dao.lastInsertId>0);
		
		assertTrue("Model enthält kein Output!", model.containsKey("output"));
		String out = (String) model.get("output");
		Person p = mapper.readValue(out, Person.class);
		assertEquals("Markus", p.getPrename());
		assertEquals("Kreth", p.getSurname());
		assertEquals("Trainer", p.getType());
		assertEquals(birth, p.getBirth());
		assertEquals(dao.lastInsertId, p.getId());
		
	}

	@Test
	public void testUpdate() throws JsonParseException, JsonMappingException, IOException {
		String input = "{\"id\":2,\"prename\":\"Markus\",\"surname\":\"Kreth\",\"type\":\"Trainer\",\"birth\":" + birth.getTime() + ",\"created\":" + now.getTime() + ",\"changed\":" + now.getTime() + "}";
		String viewName = controller.update(input, model);
		
		assertEquals("output", viewName);
		assertEquals(1, dao.updated.size());

		assertTrue("Model enthält kein Output!", model.containsKey("output"));
		String out = (String) model.get("output");
		Person p = mapper.readValue(out, Person.class);

		assertEquals((long)2, p.getId().longValue());
		assertEquals("Markus", p.getPrename());
		assertEquals("Kreth", p.getSurname());
		assertEquals("Trainer", p.getType());
		assertEquals(birth, p.getBirth());
		assertEquals(now, p.getCreated());
		assertTrue("Created not before changed!", p.getCreated().before(p.getChanged()));
	}
}
