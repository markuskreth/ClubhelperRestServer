package de.kreth.clubhelperbackend.controller;

import org.junit.Before;

import de.kreth.clubhelperbackend.controller.abstr.AbstractController;
import de.kreth.clubhelperbackend.dao.AbstractDatabaseTests;
import de.kreth.clubhelperbackend.pojo.AbstractData;

public abstract class AbstractControllerWithDbTest<T extends AbstractData>
		extends
			AbstractDatabaseTests<T> {

	AbstractController<T> controller;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		controller = createControllerClass();
	}

	protected abstract AbstractController<T> createControllerClass();
}