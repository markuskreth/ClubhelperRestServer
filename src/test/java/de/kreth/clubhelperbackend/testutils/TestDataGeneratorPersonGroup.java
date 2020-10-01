package de.kreth.clubhelperbackend.testutils;

import de.kreth.clubhelperbackend.pojo.PersonGroup;

public class TestDataGeneratorPersonGroup extends AbstractTestDataGenerator<PersonGroup> {

	public TestDataGeneratorPersonGroup() {
		super(PersonGroup.class);
	}

	@Override
	public PersonGroup instance(Class<PersonGroup> pojoClass) {
		PersonGroup pg = new PersonGroup();
		pg.setPersonId(testPerson().getId());
		pg.setGroupId(1L);
		return pg;
	}

	@Override
	public void change(PersonGroup obj) {
		obj.setDeleted(true);
	}

}
