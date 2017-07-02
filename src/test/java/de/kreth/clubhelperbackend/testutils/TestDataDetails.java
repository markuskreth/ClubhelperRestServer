package de.kreth.clubhelperbackend.testutils;

import de.kreth.clubhelperbackend.pojo.Contact;

public enum TestDataDetails {
	INSTANCE;
	public final Contact EMAIL;

	private TestDataDetails() {
		EMAIL = new Contact();
		EMAIL.setType("Email");
		EMAIL.setValue("test@test.de");

	}
}
