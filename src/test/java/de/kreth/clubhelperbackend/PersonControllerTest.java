package de.kreth.clubhelperbackend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.kreth.clubhelperbackend.controller.AdressController;
import de.kreth.clubhelperbackend.controller.ContactController;
import de.kreth.clubhelperbackend.controller.PersonController;
import de.kreth.clubhelperbackend.controller.PersonController.PersonRelative;
import de.kreth.clubhelperbackend.controller.RelativeController;
import de.kreth.clubhelperbackend.pojo.Adress;
import de.kreth.clubhelperbackend.pojo.Contact;
import de.kreth.clubhelperbackend.pojo.Person;
import de.kreth.clubhelperbackend.pojo.Relative;
import de.kreth.clubhelperbackend.testutils.TestDataDetails;
import de.kreth.clubhelperbackend.testutils.TestDataPerson;
import de.kreth.clubhelperbackend.testutils.TestDevices;

public class PersonControllerTest {

	private PersonController controller;
	private StubDao<Person> dao;
	private StubDao<Relative> relativeDao;
	private StubDao<Contact> contactDao;
	private StubDao<Adress> adressDao;

	@Before
	public void setUp() {
		dao = new StubDao<Person>();
		relativeDao = new StubDao<Relative>();
		contactDao = new StubDao<Contact>();
		adressDao = new StubDao<Adress>();
		controller = new PersonController(dao);
		controller.setRelativeController(new RelativeController(relativeDao));
		controller.setContactController(new ContactController(contactDao));
		controller.setAdressController(new AdressController(adressDao));

	}

	@Test
	public void testPersonProperties() throws Exception {
		List<Person> persons = controller.getAll();
		assertNotNull(persons);
		assertEquals(0, persons.size());

	}

	@Test
	public void testInsert()
			throws JsonParseException, JsonMappingException, IOException {

		Person created = controller
				.post(TestDataPerson.getPersonWithoutCreateChange());

		assertEquals(1, dao.inserted.size());
		assertTrue("LastInsertId in Dao wasn't incremented!",
				dao.lastInsertId > 0);

		assertEquals("Markus", created.getPrename());
		assertEquals("Kreth", created.getSurname());
		assertEquals(TestDataPerson.INSTANCE.birth, created.getBirth());
		assertEquals(dao.lastInsertId, created.getId());

	}

	@Test
	public void testUpdate()
			throws JsonParseException, JsonMappingException, IOException {

		Person out = controller.put(2, TestDataPerson.getPerson());
		assertEquals(1, dao.updated.size());

		assertEquals(2L, out.getId().longValue());
		assertEquals("Markus", out.getPrename());
		assertEquals("Kreth", out.getSurname());
		assertEquals(TestDataPerson.INSTANCE.birth, out.getBirth());
		assertEquals(TestDataPerson.INSTANCE.now, out.getCreated());
		assertTrue("Created not before changed!",
				out.getCreated().before(out.getChanged()));
	}

	@Test
	public void testDelete() {

		Person person = TestDataPerson.getPerson();
		dao.byId.put(person.getId(), person);
		ResponseEntity<Person> deleted = controller.delete(person.getId());
		Person out = deleted.getBody();

		assertEquals(1, dao.deleted.size());
		assertEquals(0, dao.updated.size());

		assertEquals((long) 2, out.getId().longValue());
		assertEquals("Markus", out.getPrename());
		assertEquals("Kreth", out.getSurname());
		assertEquals(TestDataPerson.INSTANCE.birth, out.getBirth());
	}

	@Test
	public void testGetAllAsView() {
		Model model = mock(Model.class);
		assertEquals("PersonAllAjax", controller.getAllAsView(true, null, model));
		assertEquals("PersonAll", controller.getAllAsView(false, null, model));
	}
	
	@Test
	public void testPost() {
		Person toCreate = new Person(3L, "prename", "surname", new Date());
		controller.post(3L, toCreate);
		assertEquals(1, dao.inserted.size());
		dao.byId.put(3l, toCreate);
		controller.post(3L, toCreate);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void personGetAsView() {
		dao.byId.put(TestDataPerson.getPerson().getId(),
				TestDataPerson.getPerson());
		dao.byId.put(TestDataPerson.getPerson2().getId(),
				TestDataPerson.getPerson2());

		contactDao.toGetByWhere = new ArrayList<>();
		contactDao.toGetByWhere.add(TestDataDetails.INSTANCE.EMAIL);
		relativeDao.toGetByWhere = new ArrayList<>();
		Relative r = new Relative(1L, TestDataPerson.getPerson().getId(),
				TestDataPerson.getPerson2().getId(), "Mutter", "Kind", null,
				null);
		relativeDao.toGetByWhere.add(r);

		ExtendedModelMap model = new ExtendedModelMap();

		controller.getAsView(TestDataPerson.getPerson().getId(), true,
				TestDevices.MOBILE, model);

		Set<String> keys = model.keySet();
		assertEquals(4, keys.size());
		assertTrue(keys.contains(Person.class.getSimpleName()));
		assertTrue(keys.contains("PersonRelativeList"));
		assertTrue(keys.contains("AdressList"));
		assertTrue(keys.contains("ContactList"));
		Object object = model.get(Person.class.getSimpleName());
		assertEquals(TestDataPerson.getPerson(), object);
		List<Contact> contactList = (List<Contact>) model.get("ContactList");

		assertEquals(1, contactDao.getByWhere.size());
		assertEquals(1, contactList.size());
		assertEquals(TestDataDetails.INSTANCE.EMAIL, contactList.get(0));

		List<PersonRelative> relative = (List<PersonRelative>) model
				.get("PersonRelativeList");
		assertEquals(1, relative.size());

		assertEquals(TestDataPerson.getPerson2(),
				relative.get(0).getToPerson());
	}
	
	@Test
	public void testGetChangedSince() {
		List<Person> changedPersons = controller.getChangedSince(1L);
		assertNotNull(changedPersons);
	}
	
}
