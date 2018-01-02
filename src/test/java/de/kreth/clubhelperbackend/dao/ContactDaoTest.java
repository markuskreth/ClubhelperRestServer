package de.kreth.clubhelperbackend.dao;

import static org.apache.commons.lang3.StringUtils.join;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.text.IsEqualIgnoringCase;
import org.junit.Test;
import org.mockito.Matchers;

import de.kreth.clubhelperbackend.config.SqlForDialect;
import de.kreth.clubhelperbackend.config.SqlForMysql;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao.DaoConfig;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao.RowMapper;
import de.kreth.clubhelperbackend.pojo.Contact;

public class ContactDaoTest extends AbstractDaoTest<Contact> {

	private String tableName = "tableName";
	private String[] columnNames = {"column1", "column2"};

	protected AbstractDao<Contact> configureDao() {

		mapper = new ContactDao.ContactRowMapper();
		DaoConfig<Contact> config = new DaoConfig<Contact>(tableName,
				columnNames, mapper, null);
		AbstractDao<Contact> dao = new AbstractDao<Contact>(config) {
		};
		return dao;
	}

	@Test
	public void testGetById() {

		Matcher<String> sqlMatcher = sqlMatcher(
				Arrays.asList("select", "*", "from", "tablename", "where",
						"deleted", "is", "null", "and", "id=?"));
		long id = 1;

		dao.getById(id);
		verify(jdbcTemplate).queryForObject(Matchers.argThat(sqlMatcher),
				Matchers.<RowMapper<Contact>>any(), Matchers.eq(id));
	}

	@Test
	public void testGetAll() {

		Matcher<String> sqlMatcher = sqlMatcher(Arrays.asList("select", "*",
				"from", "tablename", "where", "deleted", "is", "null"));
		dao.getAll();
		verify(jdbcTemplate).query(Matchers.argThat(sqlMatcher),
				Matchers.<RowMapper<Contact>>any());
	}

	@Test
	public void testDeleteObject() {

		Matcher<String> sqlMatcher = sqlMatcher(Arrays.asList("update",
				"tableName", "set", "deleted=?", "where", "id=?"));
		long id = 1;
		Contact c = new Contact();
		c.setId(id);

		dao.delete(c);
		verify(jdbcTemplate).update(Matchers.argThat(sqlMatcher),
				Matchers.any(Date.class), Matchers.eq(id));

	}

	private BaseMatcher<String> sqlMatcher(final List<String> expected) {
		return new BaseMatcher<String>() {

			List<String> words = new ArrayList<>(expected);

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
		};
	}

	@Test
	public void testDeleteById() {

		Matcher<String> sqlMatcher = sqlMatcher(Arrays.asList("update",
				"tableName", "set", "deleted=?", "where", "id=?"));
		long id = 1;

		dao.delete(id);
		verify(jdbcTemplate).update(Matchers.argThat(sqlMatcher),
				Matchers.any(Date.class), Matchers.eq(id));

	}

	@Test
	public void testGetByWhere() {

		String where = "personid=1";

		Matcher<String> sqlMatcher = sqlMatcher(
				Arrays.asList("select", "*", "from", "tablename", "where",
						"deleted", "is", "null", "and", "personid=1"));
		dao.getByWhere(where);

		verify(jdbcTemplate).query(Matchers.argThat(sqlMatcher),
				Matchers.<RowMapper<Contact>>any());

	}

	@Test
	public void testInsertWithId() {

		Date now = new GregorianCalendar(2015, Calendar.AUGUST, 21, 8, 21, 0)
				.getTime();

		String type = "contactType";
		String value = "contactValue";
		long personId = 1L;
		Contact obj = new Contact(512L, type, value, personId, now, now);

		String regex = "(?iu)insert\\s+into\\s+tablename\\s*\\(\\s*id\\s*,\\s*"
				+ join(columnNames, "\\s*,\\s*")
				+ "\\s*,\\s*changed\\s*,\\s*created\\s*\\)\\s*values\\s*\\(\\s*"
				+ countToQuestionmarkList(columnNames.length + 3) + "\\s*\\)";

		List<Object> list = new ArrayList<Object>();
		list.add(512L);
		list.add(type);
		list.add(value);
		list.add(personId);

		list.add(now);
		list.add(now);
		final Object[] values = list.toArray();

		when(jdbcTemplate.update(Matchers.any(String.class),
				Matchers.argThat(new ObjectArrayMatcher(null)))).thenReturn(1);

		Contact insert = dao.insert(obj);

		verify(jdbcTemplate).update(Matchers.matches(regex),
				Matchers.argThat(new ObjectArrayMatcher(values)));
		assertEquals(512L, insert.getId().longValue());
	}

	@Test
	public void testInsertWithoutId() {
		SqlForDialect sqlDialect = mock(SqlForMysql.class);
		when(sqlDialect.queryForIdentity(Matchers.any(String.class)))
				.thenReturn("SQLcode for queriing id");
		when(jdbcTemplate.queryForObject(
				Matchers.same("SQLcode for queriing id"), Matchers.any(),
				Matchers.same(Long.class))).thenReturn(512L);

		dao.setSqlDialect(sqlDialect);
		Date now = new GregorianCalendar(2015, Calendar.AUGUST, 21, 8, 21, 0)
				.getTime();

		String type = "contactType";
		String value = "contactValue";
		long personId = 1L;
		Contact obj = new Contact(null, type, value, personId, now, now);

		String regex = "(?iu)insert\\s+into\\s+tablename\\s*\\(\\s*"
				+ join(columnNames, "\\s*,\\s*")
				+ "\\s*,\\s*changed\\s*,\\s*created\\s*\\)\\s*values\\s*\\(\\s*"
				+ countToQuestionmarkList(columnNames.length + 2) + "\\s*\\)";

		List<Object> list = new ArrayList<Object>();

		list.add(type);
		list.add(value);
		list.add(personId);

		list.add(now);
		list.add(now);
		Object[] values = list.toArray();

		when(jdbcTemplate.update(Matchers.anyString(),
				Matchers.argThat(new ObjectArrayMatcher(null)))).thenReturn(1);

		Contact insert = dao.insert(obj);

		Pattern.compile(regex);

		verify(jdbcTemplate).update(Matchers.matches(regex),
				Matchers.argThat(new ObjectArrayMatcher(values)));
		verify(sqlDialect).queryForIdentity(tableName);
		verify(jdbcTemplate).queryForObject("SQLcode for queriing id", null,
				Long.class);
		assertEquals(512L, insert.getId().longValue());
	}

	@Test
	public void testUpdateWithId() {

		GregorianCalendar calendar = new GregorianCalendar(2015,
				Calendar.AUGUST, 21, 8, 21, 0);
		Date now = calendar.getTime();
		calendar.add(Calendar.DAY_OF_MONTH, -10);

		String type = "contactType";
		String value = "contactValue";
		long personId = 1L;
		Contact obj = new Contact(personId, type, value, personId, now,
				calendar.getTime());

		String regex = "(?iu)update\\s+tablename\\s+set\\s+"
				+ join(columnNames, "\\s*=\\s*\\?\\s*,\\s*")
				+ "\\s*=\\s*\\?\\s*,\\s*changed\\s*=\\s*\\?\\s+where\\s+id\\s*=\\s*\\?\\s*";

		Pattern.compile(regex);

		when(jdbcTemplate.update(Matchers.anyString(),
				Matchers.argThat(new ObjectArrayMatcher(null)))).thenReturn(1);

		List<Object> list = new ArrayList<Object>();

		list.add(type);
		list.add(value);
		list.add(personId);

		list.add(now);
		list.add(2L);

		Object[] values = list.toArray();

		assertTrue(dao.update(2L, obj));
		assertEquals(2L, obj.getId().longValue());
		assertTrue(dao.update(obj)); // Now obj has Id 2, so values will match
										// also. Update is executed also with no
										// changes.
		assertEquals(2L, obj.getId().longValue());
		verify(jdbcTemplate, times(2)).update(Matchers.matches(regex),
				Matchers.argThat(new ObjectArrayMatcher(values)));

	}

	private String countToQuestionmarkList(int count) {
		StringBuilder retVal = new StringBuilder();
		for (int i = 0; i < count; i++) {
			if (i > 0)
				retVal.append(",\\s*");
			retVal.append("\\?\\s*");
		}
		return retVal.toString();
	}

}