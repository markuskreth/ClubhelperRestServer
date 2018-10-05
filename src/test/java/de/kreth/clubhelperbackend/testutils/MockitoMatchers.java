package de.kreth.clubhelperbackend.testutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.argThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.hamcrest.text.IsEqualIgnoringCase;
import org.mockito.ArgumentMatcher;
import org.mockito.internal.matchers.VarargMatcher;

public class MockitoMatchers {
	
	private static final ErrorState NO_ERROR = new ErrorState(-10, null);
	
	private static class ErrorState {
		int index;
		Object errorObject;
		
		public ErrorState(int index, Object errorObject) {
			super();
			this.index = index;
			this.errorObject = errorObject;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((errorObject == null) ? 0 : errorObject.hashCode());
			result = prime * result + index;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ErrorState other = (ErrorState) obj;
			if (errorObject == null) {
				if (other.errorObject != null)
					return false;
			} else if (!errorObject.equals(other.errorObject))
				return false;
			if (index != other.index)
				return false;
			return true;
		}
		
	}
	
	protected static class ObjectArrayArgumentMatcher implements ArgumentMatcher<Object[]>, VarargMatcher {
		
		private static final long serialVersionUID = 3846783742617041128L;
		private final Object[] values;
		private ErrorState errorState = NO_ERROR;

		protected ObjectArrayArgumentMatcher(Object[] values) {
			this.values = values;
		}

		@Override
		public boolean matches(Object[] argument) {

			if (values == null) {
				return true;
			}
			
			if (argument == null) {
				return false;
			}

			StringBuilder errMsg = new StringBuilder();
			errMsg.append("Expected=");
			errMsg.append(objArrayToString(values));
			errMsg.append("; actual=");
			errMsg.append(objArrayToString(argument));

			errorState = new ErrorState(-1, argument.length);
			assertEquals(errMsg.toString(), values.length, argument.length);
			for (int i = 0; i < values.length; i++) {
				errorState = new ErrorState(i, argument[i]);
				assertEquals("At index " + i + " values differ.", values[i],
						argument[i]);
			}
			errorState = NO_ERROR;
			return true;
		}
		
		@Override
		public String toString() {
			
			if (false == NO_ERROR.equals(errorState)) {
				if (errorState.index == -1) {
					return "expected length=" + values.length + ", actualLength=" + errorState.errorObject;
				} else {
					return "Expected=" + Arrays.toString(values) + ", at Index=" + errorState.index + "=" + errorState.errorObject;
				}
			} else {
				return "expected: " + Arrays.toString(values) + ", no errors";
			}
		}
	}

	private static String objArrayToString(Object... v) {
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
	
	public static ArgumentMatcher<String> containsCaseInsensitive(final String containedText) {
		
	    if (containedText == null) throw new IllegalArgumentException("containedText is null");
	    
	    final Pattern pattern = Pattern.compile(".*" + containedText + ".*", Pattern.CASE_INSENSITIVE);
	    
	    return new ArgumentMatcher<String>() {

	        @Override
	        public boolean matches(String arg) {
	            return arg != null && pattern.matcher(arg).find();
	        }

	        @Override 
	        public String toString() {
	            return String.format("[should have contained, ignoring case, \"%s\"]", containedText);
	        }

	    };
	}

	public static ArgumentMatcher<String> eqCaseInsensitive(final String s) {
	    if (s == null) throw new IllegalArgumentException("s is null");

	    return new ArgumentMatcher<String>() {

	        @Override
	        public boolean matches(String arg) {
	            return arg != null && s.equalsIgnoreCase(arg);
	        }

	        @Override 
	        public String toString() {
	            return String.format("[should equal, ignoring case, \"%s\"]", s);
	        }

	    };
	}

	public static ArgumentMatcher<Object[]> objectArray(final Object... values) { 
	    return new ObjectArrayArgumentMatcher(values);
	}

	public static ArgumentMatcher<String> sqlMatcher(final List<String> expected) {
	    if (expected == null) throw new IllegalArgumentException("expected is null");

	    return new ArgumentMatcher<String>() {

			List<String> words = new ArrayList<>(expected);
			int index = 0;
			
	        @Override
	        public synchronized boolean matches(String arg) {
	        	index = 0;
				StringTokenizer tok = new StringTokenizer(arg);
				while (index<words.size() && tok.hasMoreTokens()) {
					String nextToken = tok.nextToken();
					String expected = words.get(index);
					assertThat(nextToken,
							new IsEqualIgnoringCase(expected));

					index++;
				}
				return index == words.size() && tok.hasMoreTokens() == false;
	        }

	        @Override 
	        public String toString() {
	        	StringBuilder text = new StringBuilder("\"");
	        	for (String token:words) {
	        		text.append(token).append(" ");
	        	}
	        	text.deleteCharAt(text.length()-1);
	        	text.append("\"");
	        	return text.toString();
	        }

	    };
	}

	public static String tokens(List<String> expected) {
		return argThat(sqlMatcher(expected));
	}
}
