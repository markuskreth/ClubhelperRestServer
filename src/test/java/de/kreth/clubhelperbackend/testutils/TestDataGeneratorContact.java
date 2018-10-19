package de.kreth.clubhelperbackend.testutils;

import de.kreth.clubhelperbackend.pojo.Contact;

public class TestDataGeneratorContact extends AbstractTestDataGenerator<Contact> {

	public TestDataGeneratorContact() {
		super(Contact.class);
	}

	@Override
	public Contact instance(Class<Contact> pojoClass) {
		Contact obj = new Contact();
		obj.setPersonId(testPerson().getId());
		obj.setType("Mobile");
		obj.setValue("00000000000");
		return obj;
	}

	@Override
	public void change(Contact obj) {
		obj.setType("Telefon");
	}

}
