package de.kreth.clubhelperbackend.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class EmailUtilsTest {

	@Test
	public void validEmailadressReturnsTrue() {
		List<String> validEmails = new ArrayList<String>();
		validEmails.add("email@example.com");
		validEmails.add("firstname.lastname@example.com");
		validEmails.add("email@subdomain.example.com");
		validEmails.add("firstname+lastname@example.com");
		validEmails.add("1234567890@example.com");
		validEmails.add("email@example-one.com");
		validEmails.add("email@example.name");
		validEmails.add("email@example.museum");
		validEmails.add("email@example.web");
		validEmails.add("email@example.co.jp");
		validEmails.add("firstname-lastname@example.com");

		// Should be invalid, but aren't
		validEmails.add("email@-example.com");

		for (String email : validEmails) {
			boolean isValid = EmailUtils.isValidEmailadress(email);
			assertTrue("Should be valid: " + email, isValid);
		}
	}

	@Test
	public void validEmailadressReturnsFalse() {

		List<String> invalidEmails = new ArrayList<String>();
		invalidEmails.add("");
		invalidEmails.add("plainaddress");
		invalidEmails.add("#@%^%#$@#$@#.com");
		invalidEmails.add("@example.com");
		invalidEmails.add("Joe Smith <email@example.com>");
		invalidEmails.add("email.example.com");
		invalidEmails.add("email@example@example.com");
		invalidEmails.add("email@example.com@example.com");
		invalidEmails.add(".email@example.com");
		invalidEmails.add("email.@example.com");
		invalidEmails.add("email@.example.com");
		invalidEmails.add("email@exa mple.com");
		invalidEmails.add("ema il.@example.com");
		invalidEmails.add("email..email@example.com");
		invalidEmails.add("あいうえお@example.com");
		invalidEmails.add("email@example.com (Joe Smith)");
		invalidEmails.add("email@example");
		invalidEmails.add("email@111.222.333.44444");
		invalidEmails.add("email@example..com");
		invalidEmails.add("Abc..123@example.com");

		// Should be valid, but aren't
		invalidEmails.add("“email”@example.com");

		for (String email : invalidEmails) {
			boolean isValid = EmailUtils.isValidEmailadress(email);
			assertFalse("Should be invalid: " + email, isValid);
		}
	}
}
