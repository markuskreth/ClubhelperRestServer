package de.kreth.clubhelperbackend.testutils;

import de.kreth.clubhelperbackend.pojo.Group;

public class TestDataGeneratorGroup extends AbstractTestDataGenerator<Group> {

	public TestDataGeneratorGroup() {
		super(Group.class);
	}

	@Override
	public Group instance(Class<Group> pojoClass) {
		Group g = new Group(-1L, "GroupName");
		return g;
	}

	@Override
	public void change(Group obj) {
		obj.setName("New Groupname");
	}

}
