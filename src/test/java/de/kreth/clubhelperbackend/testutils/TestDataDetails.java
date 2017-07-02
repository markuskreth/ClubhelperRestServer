package de.kreth.clubhelperbackend.testutils;

import de.kreth.clubhelperbackend.pojo.Contact;

public enum TestDataDetails {
	INSTANCE;
	public final Contact EMAIL;
	public final Contact MOBILE;

	private TestDataDetails() {
		EMAIL = new Contact();
		EMAIL.setType("Email");
		EMAIL.setValue("test@test.de");

		MOBILE = new Contact();
		MOBILE.setType("Mobile");
		MOBILE.setValue("01115555555");

	}
}
