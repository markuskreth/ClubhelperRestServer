package de.kreth.clubhelperbackend.testutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.regex.Pattern;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

public class MockitoMatchersTest {

	@Test
	public void testPattern() {
		String regex = "text";

		assertTrue(Pattern.compile(regex).matcher("a text between").find());

		assertTrue(Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher("a teXt between").find());
		assertTrue(Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher("a teXt between").find());
	}
	
	@Test
	public void testTokensEmpty() {
		StringArg obj = mock(StringArg.class);
		obj.exec("");
		ArgumentMatcher<String> matcher = MockitoMatchers.sqlMatcher(Collections.emptyList());
		verify(obj).exec(ArgumentMatchers.argThat(matcher));
	}

	@Test
	public void testTokenOneArg() {

		StringArg obj = mock(StringArg.class);
		ArgumentMatcher<String> matcher;

		obj.exec("arg1");
		matcher = MockitoMatchers.sqlMatcher(Arrays.asList("arg1"));
		verify(obj, atLeastOnce()).exec(ArgumentMatchers.argThat(matcher));
	}

	@Test
	public void testInsertSql() {

		StringArg obj = mock(StringArg.class);
		ArgumentMatcher<String> matcher;

		obj.exec("insert into tableName (type, value, person_id, changed, created) values (?,?,?,?,?)");
		matcher = MockitoMatchers.sqlMatcher(Arrays.asList("insert", "into", "tableName", "(type,", "value,", "person_id,", "changed,", "created)", "values", "(?,?,?,?,?)"));
		verify(obj, atLeastOnce()).exec(ArgumentMatchers.argThat(matcher));
	}

	@Test
	public void testTokenTwoArgs() {

		StringArg obj = mock(StringArg.class);
		ArgumentMatcher<String> matcher;

		obj.exec("arg1 arg2");
		matcher = MockitoMatchers.sqlMatcher(Arrays.asList("arg1", "arg2"));
		verify(obj, atLeastOnce()).exec(ArgumentMatchers.argThat(matcher));
	}

	@Test
	public void testTokenMissingArg() {

		StringArg obj = mock(StringArg.class);
		ArgumentMatcher<String> matcher;

		obj.exec("arg1");
		matcher = MockitoMatchers.sqlMatcher(Arrays.asList("arg1", "arg2"));

		try {
			verify(obj, atLeastOnce()).exec(ArgumentMatchers.argThat(matcher));
			fail("sqlMatcher should fail on missing arg2");
		} catch (AssertionError e) {
			String msg = e.getMessage();
			assertTrue("Message was: " + msg, msg.contains("\"arg1\""));
			assertTrue("Message was: " + msg, msg.contains("\"arg1 arg2\""));
		}
	}

	@Test
	public void testTokenAdditionalArg() {

		StringArg obj = mock(StringArg.class);
		ArgumentMatcher<String> matcher;

		obj.exec("arg1 arg2 arg3");
		matcher = MockitoMatchers.sqlMatcher(Arrays.asList("arg1", "arg2"));
		try {
			verify(obj, atLeastOnce()).exec(ArgumentMatchers.argThat(matcher));
			fail("sqlMatcher should fail on additional arg3");
		} catch (AssertionError e) {
			String msg = e.getMessage();
			assertTrue("Message was: " + msg, msg.contains("\"arg1 arg2 arg3\""));
			assertTrue("Message was: " + msg, msg.contains("\"arg1 arg2\""));
		}
	}

	@Test
	public void testTokenWrongOrder() {

		StringArg obj = mock(StringArg.class);
		ArgumentMatcher<String> matcher;

		obj.exec("arg3 arg4");
		matcher = MockitoMatchers.sqlMatcher(Arrays.asList("arg1", "arg2"));
		try {
			verify(obj, atLeastOnce()).exec(ArgumentMatchers.argThat(matcher));
		} catch (AssertionError e) {
			String msg = e.getMessage();
			assertTrue(msg.contains("arg1"));
			assertTrue(msg.contains("arg3"));
		}
	}
	
	@Test
	public void testContainsCaseInsensitive() {
		ArgumentMatcher<String> matcher = MockitoMatchers.containsCaseInsensitive("tst");
		assertFalse(matcher.matches(null));
		assertFalse(matcher.matches(""));
		assertFalse(matcher.matches("?"));
		assertFalse(matcher.matches("t"));
		assertFalse(matcher.matches("s"));

		assertTrue(matcher.matches("teststring"));
		assertTrue(matcher.matches("tst"));

		assertTrue(matcher.matches("teststring".toUpperCase()));
		assertTrue(matcher.matches("eTstRIng"));
		assertTrue(matcher.matches("TestStr"));

		matcher = MockitoMatchers.containsCaseInsensitive("TST");

		assertFalse(matcher.matches("t"));
		assertFalse(matcher.matches("s"));

		assertTrue(matcher.matches("teststring"));
		assertTrue(matcher.matches("tst"));

		assertTrue(matcher.matches("teststring".toUpperCase()));
		assertTrue(matcher.matches("eTstRIng"));
		assertTrue(matcher.matches("TestStr"));

	}

	@Test
	public void testEqCaseInsensitive() {
		assertFalse(MockitoMatchers.eqCaseInsensitive("teststring").matches("teststr"));
		assertFalse(MockitoMatchers.eqCaseInsensitive("teststring").matches("eststring"));
		assertFalse(MockitoMatchers.eqCaseInsensitive("teststring").matches(" teststring"));

		assertTrue(MockitoMatchers.eqCaseInsensitive("teststring").matches("teststring"));
		assertTrue(MockitoMatchers.eqCaseInsensitive("teststring").matches("teststring".toUpperCase()));
		assertTrue(MockitoMatchers.eqCaseInsensitive("teststring".toUpperCase()).matches("teststring"));
		assertTrue(MockitoMatchers.eqCaseInsensitive("teststring").matches("tesTString"));
		assertTrue(MockitoMatchers.eqCaseInsensitive("tesTString").matches("teststring"));
	}

	@Test
	public void testNullValueArrayWorking() {

		ObjectArrayMethod method = Mockito.mock(ObjectArrayMethod.class);

		Mockito.when(method.call(ArgumentMatchers.<Object>any())).thenReturn(15);
		Object[] arg = new Object[] {null, null};
		int actual = method.call(arg);
		assertEquals(15, actual);

	}

	@Test
	public void testNullValueArrayEq() {

		ObjectArrayMethod method = Mockito.mock(ObjectArrayMethod.class);

		Object[] arg = {"", 1};
		Mockito.when(method.call(ArgumentMatchers.<Object>any())).thenReturn(15);
		int actual = method.call(arg);
		assertEquals(15, actual);

	}

	@Test
	public void testVerifyNullValueArrayEq() {

		ObjectArrayMethod method = Mockito.mock(ObjectArrayMethod.class);

		Object[] arg = {"", ""};

		method.call(arg);

		verify(method).call(ArgumentMatchers.<String>any());
		verify(method).call(ArgumentMatchers.<Object>any());

	}

	@Test
	public void testObjectArrayMockitoMatchers() {

		ObjectArrayMethod method = Mockito.mock(ObjectArrayMethod.class);

		Object[] arg = new Object[] {"", ""};
		Mockito.when(method.call(ArgumentMatchers.<Object>any())).thenReturn(10);
		
		int actual = method.call(arg);
		verify(method).call(arg);
		
		assertEquals(10, actual);

		arg = new Object[] {"", 1};
		Mockito.when(method.call(ArgumentMatchers.<Object>any())).thenReturn(12);
		actual = method.call(arg);
		verify(method).call(arg);
		
		assertEquals(12, actual);
		Mockito.when(method.call(ArgumentMatchers.<Object>any())).thenReturn(15);
		arg = new Object[] {"", Long.valueOf(2), new Date()};
		actual = method.call(arg);
		verify(method).call(arg);
		
		assertEquals(15, actual);
	}
	
	@Test
	public void testObjectArray() {
		ArgumentMatcher<Object[]> allObjectArraysMatcher = MockitoMatchers.objectArray((Object[])null);
		assertTrue("null Array", allObjectArraysMatcher.matches(null));

		assertTrue("Empty Array", allObjectArraysMatcher.matches(new Object[0]));

		assertTrue("Two Objects Array null values", allObjectArraysMatcher.matches(new Object[2]));
		assertTrue("Two Objects Array String and int values", allObjectArraysMatcher.matches(new Object[] {"1", 1}));
		
	}

	static class ObjectArrayMethod {
		public int call(Object... objects) {
			return 0;
		}
	}
	
	private interface StringArg {
		String exec(String arg);
	}
}
