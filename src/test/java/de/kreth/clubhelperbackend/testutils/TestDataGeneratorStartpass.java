package de.kreth.clubhelperbackend.testutils;

import de.kreth.clubhelperbackend.pojo.Startpass;

public class TestDataGeneratorStartpass extends AbstractTestDataGenerator<Startpass> {

	public TestDataGeneratorStartpass() {
		super(Startpass.class);
	}

	@Override
	public Startpass instance(Class<Startpass> pojoClass) {
		Startpass startpass = new Startpass();
		startpass.setPersonId(testPerson().getId());
		startpass.setStartpassNr("startpassNr");
		return startpass;
	}

	@Override
	public void change(Startpass obj) {
		obj.setStartpassNr("startpassNr2");
	}

}
