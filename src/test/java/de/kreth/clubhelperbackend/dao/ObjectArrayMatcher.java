package de.kreth.clubhelperbackend.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.mockito.ArgumentMatcher;
import org.mockito.internal.matchers.VarargMatcher;

public class ObjectArrayMatcher extends ArgumentMatcher<Object[]> implements VarargMatcher {

	private static final long serialVersionUID = -4022372867173708517L;
	private Object[] values = null;

	public ObjectArrayMatcher(Object[] values) {
		super();
		this.values = values;
	}

	@Override
	public boolean matches(Object argument) {
		if (values == null) {
			if (argument instanceof List<?> || argument instanceof Object[]) {
				return true;
			}
		}

		Object[] argValues = (Object[]) argument;
		assertEquals("Expected=" + objArrayToString(values) + "; actual=" + argValues, values.length,
				argValues.length);
		for (int i = 0; i < values.length; i++) {
			if (!values[i].equals(argValues[i]))
				return false;
		}
		return true;
	}

	private String objArrayToString(Object[] v) {
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