package de.kreth.clubhelperbackend.testutils;

import de.kreth.clubhelperbackend.pojo.Relative;

public class TestDataGeneratorRelative extends AbstractTestDataGenerator<Relative> {

	public static final long attendanceDate = 1538938500000L;

	public TestDataGeneratorRelative() {
		super(Relative.class);
	}

	@Override
	public Relative instance(Class<Relative> pojoClass) {
		long pId = testPerson().getId().longValue();
		Relative rel = new Relative(null, pId, pId, "IDENTITY", "IDENTITY");
		return rel;
	}

	@Override
	public void change(Relative obj) {
		obj.setToPerson1Relation("SAME");
		obj.setToPerson2Relation("SAME");
	}

}
