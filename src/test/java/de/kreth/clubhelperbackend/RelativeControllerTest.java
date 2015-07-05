package de.kreth.clubhelperbackend;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.kreth.clubhelperbackend.controller.RelativeController;
import de.kreth.clubhelperbackend.pojo.Relative;

public class RelativeControllerTest {

	private StubDao<Relative> dao;
	private RelativeController controller;
	
	@Before
	public void setUp() throws Exception {
		dao = new StubDao<Relative>();
		controller = new RelativeController(dao);
	}

	@Test
	public void ensureGetForIdUsesCorrectColumns() {
		controller.getForId(6);
		assertEquals(1, dao.getByWhere.size());
		String whereClause = dao.getByWhere.get(0).toLowerCase();
		assertTrue(whereClause.contains("person1=6"));
		assertTrue(whereClause.contains("person2=6"));
		assertTrue(whereClause.contains(" or "));
		
	}

}
