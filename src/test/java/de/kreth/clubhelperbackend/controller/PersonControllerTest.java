package de.kreth.clubhelperbackend.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.kreth.clubhelperbackend.controller.abstr.AbstractController;
import de.kreth.clubhelperbackend.dao.PersonDao;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.pojo.Person;
import de.kreth.clubhelperbackend.testutils.TestDataPerson;

public class PersonControllerTest extends AbstractControllerWithDbTest<Person> {

	private AdressController adressController;
	private ContactController contactController;
	private RelativeController relativeController;

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void deleteTwice() throws Exception {
		Person p1 = TestDataPerson.getPerson();
		p1 = controller.post(p1);
		assertFalse(p1.isDeleted());

		controller.delete(p1.getId());
		p1 = controller.getById(p1.getId());

		assertTrue(p1.isDeleted());

		// Second delete is just accepted.
		ResponseEntity<Person> result = controller.delete(p1.getId());
		assertNotNull(result);

		assertEquals(HttpStatus.OK, result.getStatusCode());
		p1 = result.getBody();
		assertTrue(p1.isDeleted());
	}

	@Override
	public AbstractDao<Person> initDao() {
		return new PersonDao();
	}

	@Override
	protected AbstractController<Person> createControllerClass() {
		PersonController personController = new PersonController(dao);

		adressController = mock(AdressController.class);
		personController.setAdressController(adressController);

		contactController = mock(ContactController.class);
		personController.setContactController(contactController);

		relativeController = mock(RelativeController.class);
		personController.setRelativeController(relativeController);

		return personController;
	}

}
