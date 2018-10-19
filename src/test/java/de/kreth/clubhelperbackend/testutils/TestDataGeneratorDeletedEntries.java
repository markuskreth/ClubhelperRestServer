package de.kreth.clubhelperbackend.testutils;

import de.kreth.clubhelperbackend.pojo.DeletedEntries;

public class TestDataGeneratorDeletedEntries extends AbstractTestDataGenerator<DeletedEntries> {

	public TestDataGeneratorDeletedEntries() {
		super(DeletedEntries.class);
	}

	@Override
	public DeletedEntries instance(Class<DeletedEntries> pojoClass) {
		return new DeletedEntries(0L, "tablename", 10L);
	}

	@Override
	public void change(DeletedEntries obj) {
		obj.setTablename("changedTable");
		obj.setEntryId(15L);
	}

}
