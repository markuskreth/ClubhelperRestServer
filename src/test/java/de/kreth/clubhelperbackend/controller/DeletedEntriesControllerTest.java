package de.kreth.clubhelperbackend.controller;

import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class DeletedEntriesControllerTest {

	private DeletedEntriesController controller;
	
	@Before
	public void initController() {
		MockitoAnnotations.initMocks(this);
		controller = new DeletedEntriesController(null);
	}
	
	@Test
	public void testGetByParentIdLong() {
		assertNull(controller.getByParentId(1));
	}

}
