package de.kreth.clubhelperbackend.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.mockito.ArgumentMatcher;
import org.mockito.internal.matchers.VarargMatcher;

public class ObjectArrayMatcher extends ArgumentMatcher<Object[]>
		implements
			VarargMatcher {

	private static final long serialVersionUID = -4022372867173708517L;
	private final Object[] values;

	public ObjectArrayMatcher(Object[] values) {
		super();
		this.values = values;
	}

	@Override
	public boolean matches(Object argument) {
		if (values == null) {
			if (argument instanceof List<?> || argument instanceof Object[]) {
				return true;
			} else {
				return false;
			}
		}
		if (argument == null) {
			return false;
		}

		Object[] argValues = (Object[]) argument;
		StringBuilder errMsg = new StringBuilder();
		errMsg.append("Expected=");
		errMsg.append(objArrayToString(values));
		errMsg.append("; actual=");
		errMsg.append(objArrayToString(argValues));

		assertEquals(errMsg.toString(), values.length, argValues.length);
		for (int i = 0; i < values.length; i++) {
			assertEquals("At index " + i + " values differ.", values[i],
					argValues[i]);
		}
		return true;
	}

	private String objArrayToString(Object[] v) {
		if (v == null) {
			return "NULL";
		}
		StringBuilder bld = new StringBuilder("[");
		if (v.length > 0)
			bld.append(v[0]);
		for (int i = 1; i < v.length; i++) {
			bld.append(",").append(v[i]);
		}
		bld.append("]");
		return bld.toString();
	}

}