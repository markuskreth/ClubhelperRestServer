package de.kreth.clubhelperbackend.testutils;

import de.kreth.clubhelperbackend.pojo.Adress;

public class TestDataGeneratorAdress extends AbstractTestDataGenerator<Adress> {

	public TestDataGeneratorAdress() {
		super(Adress.class);
	}

	@Override
	public Adress instance(Class<Adress> pojoClass) {
		Adress adr = new Adress(null, "adress1", "adress2", "plz", "city", testPerson().getId());
		return adr;
	}

	@Override
	public void change(Adress obj) {
		obj.setPlz("D-30000");
		obj.setCity("Berlin");
		obj.setAdress1("Teststraße 1");
	}

}
