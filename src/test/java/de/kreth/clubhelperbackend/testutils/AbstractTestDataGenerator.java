package de.kreth.clubhelperbackend.testutils;

import de.kreth.clubhelperbackend.pojo.Data;
import de.kreth.clubhelperbackend.pojo.Person;
import de.kreth.clubhelperbackend.testutils.TestData.TestDataGenerator;

public abstract class AbstractTestDataGenerator<T extends Data> implements TestDataGenerator<T>{

	private Class<T> pojoClass;

	public AbstractTestDataGenerator(Class<T> pojoClass) {
		super();
		this.pojoClass = pojoClass;
	}

	@Override
	public boolean isResponsible(Class<?> objClass) {
		return objClass.isAssignableFrom(pojoClass);
	}

	public Person testPerson() {
		return TestDataPerson.getPerson();
	}
}