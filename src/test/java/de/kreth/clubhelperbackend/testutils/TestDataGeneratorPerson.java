package de.kreth.clubhelperbackend.testutils;

import java.util.Date;

import de.kreth.clubhelperbackend.pojo.Person;

public class TestDataGeneratorPerson extends AbstractTestDataGenerator<Person> {

	public TestDataGeneratorPerson() {
		super(Person.class);
	}

	@Override
	public Person instance(Class<Person> pojoClass) {
		return TestDataPerson.getPerson();
	}

	@Override
	public void change(Person obj) {
		obj.setBirth(new Date(obj.getBirth().getTime() - 200000L));
		obj.setPrename("Friedrich");
		obj.setSurname("Schmitd");
	}

}
