package de.kreth.clubhelperbackend.test.matchers;

import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.text.IsEqualIgnoringCase;

public class SqlMatcher extends BaseMatcher<String> {

	private final List<String> words;
	
	public SqlMatcher(Collection<String> expected) {
		words = new ArrayList<>(expected);
	}

	@Override
	public boolean matches(Object arg0) {
		if (arg0 instanceof String) {
			StringTokenizer tok = new StringTokenizer(arg0.toString());
			while (words.size() > 0 && tok.hasMoreTokens()) {
				assertThat(tok.nextToken(),
						new IsEqualIgnoringCase(words.get(0)));
				words.remove(0);
			}
			return words.isEmpty();
		}
		return false;
	}

	@Override
	public void describeTo(Description arg0) {
		arg0.appendText("Statement missing ").appendValue(words.get(0));
	}
}
