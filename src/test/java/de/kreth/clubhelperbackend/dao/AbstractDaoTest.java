package de.kreth.clubhelperbackend.dao;

import static de.kreth.clubhelperbackend.string.String.join;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.internal.matchers.VarargMatcher;
import org.springframework.jdbc.core.JdbcTemplate;

import de.kreth.clubhelperbackend.config.SqlForDialect;
import de.kreth.clubhelperbackend.config.SqlForMysql;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao.DaoConfig;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao.RowMapper;
import de.kreth.clubhelperbackend.pojo.Contact;

public class AbstractDaoTest {

	private JdbcTemplate jdbcTemplate;
	private RowMapper<Contact> mapper;

	private String tableName = "tableName";
	private String[] columnNames = { "column1", "column2" };

	private AbstractDao<Contact> dao;
	private PlatformTransactionManager transMan;
	private DeletedEntriesDao deletedEnriesDao;

	@Before
	public void setUp() throws Exception {
		jdbcTemplate = mock(JdbcTemplate.class);
		mapper = new ContactDao.ContactRowMapper();
		transMan = mock(PlatformTransactionManager.class);
		deletedEnriesDao = mock(DeletedEntriesDao.class);

		dao = configureDao(columnNames);
	}

	private AbstractDao<Contact> configureDao(String[] columnNames2) {

		DaoConfig<Contact> config = new DaoConfig<Contact>(tableName, columnNames, mapper);
		AbstractDao<Contact> dao = new AbstractDao<Contact>(config) {
		};
		dao.setJdbcTemplate(jdbcTemplate);
		dao.setPlatformTransactionManager(transMan);
		return dao;
	}

	@Test
	public void testGetById() {

		long id = 1;
		String regex = "(?iu)select\\s+\\*\\s+from\\s+`tablename`\\s+where\\s+_id\\s*=\\s*\\?";

		dao.getById(id);

		verify(jdbcTemplate).queryForObject(Matchers.matches(regex), Matchers.<RowMapper<Contact>> any(),
				Matchers.eq(id));
	}

	@Test
	public void testGetAll() {

		String regex = "(?iu)select\\s+\\*\\s+from\\s+`tablename`\\s*";

		dao.getAll();
		verify(jdbcTemplate).query(Matchers.matches(regex), Matchers.<RowMapper<Contact>> any());
	}

	@Test
	public void testDeleteObject() {

		String regex = "(?iu)delete\\s+from\\s+`tablename`\\s+where\\s+_id\\s*=\\s*\\?";

		long id = 1;
		Contact c = new Contact(id);

		dao.delete(c);
		verify(jdbcTemplate).update(Matchers.matches(regex), Matchers.eq(id));

	}

	@Test
	public void testDeleteById() {

		String regex = "(?iu)delete\\s+from\\s+`tablename`\\s+where\\s+_id\\s*=\\s*\\?";
		long id = 1;

		dao.delete(id);
		verify(jdbcTemplate).update(Matchers.matches(regex), Matchers.eq(id));

	}

	@Test
	public void testGetByWhere() {

		String where = "person_id = 1";

		String regex = "(?iu)select\\s+\\*\\s+from\\s+`tablename`\\s+where\\s+" + where;

		dao.getByWhere(where);

		verify(jdbcTemplate).query(Matchers.matches(regex), Matchers.<RowMapper<Contact>> any());

	}

	@Test
	public void testInsertWithId() {

		Date now = new GregorianCalendar(2015, Calendar.AUGUST, 21, 8, 21, 0).getTime();

		String type = "contactType";
		String value = "contactValue";
		long personId = 1L;
		Contact obj = new Contact(512L, type, value, personId, now, now);

		String regex = "(?iu)insert\\s+into\\s+`tablename`\\s*\\(\\s*_id\\s*,\\s*" + join("\\s*,\\s*", columnNames)
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

		when(jdbcTemplate.update(Matchers.any(String.class), Matchers.argThat(new ObjectArrayMatcher(null))))
				.thenReturn(1);

		Contact insert = dao.insert(obj);

		verify(jdbcTemplate).update(Matchers.matches(regex), Matchers.argThat(new ObjectArrayMatcher(values)));
		assertEquals(512L, insert.getId().longValue());
	}

	private class ObjectArrayMatcher extends ArgumentMatcher<Object[]> implements VarargMatcher {

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

	@Test
	public void testInsertWithoutId() {
		SqlForDialect sqlDialect = mock(SqlForMysql.class);
		when(sqlDialect.queryForIdentity(Matchers.any(String.class))).thenReturn("SQLcode for queriing id");
		when(jdbcTemplate.queryForObject(Matchers.same("SQLcode for queriing id"), Matchers.any(),
				Matchers.same(Long.class))).thenReturn(512L);

		dao.setSqlDialect(sqlDialect);
		Date now = new GregorianCalendar(2015, Calendar.AUGUST, 21, 8, 21, 0).getTime();

		String type = "contactType";
		String value = "contactValue";
		long personId = 1L;
		Contact obj = new Contact(null, type, value, personId, now, now);

		String regex = "(?iu)insert\\s+into\\s+`tablename`\\s*\\(\\s*" + join("\\s*,\\s*", columnNames)
				+ "\\s*,\\s*changed\\s*,\\s*created\\s*\\)\\s*values\\s*\\(\\s*"
				+ countToQuestionmarkList(columnNames.length + 2) + "\\s*\\)";

		List<Object> list = new ArrayList<Object>();

		list.add(type);
		list.add(value);
		list.add(personId);

		list.add(now);
		list.add(now);
		Object[] values = list.toArray();

		when(jdbcTemplate.update(Matchers.anyString(), Matchers.argThat(new ObjectArrayMatcher(null)))).thenReturn(1);

		Contact insert = dao.insert(obj);

		verify(jdbcTemplate).update(Matchers.matches(regex), Matchers.argThat(new ObjectArrayMatcher(values)));
		verify(sqlDialect).queryForIdentity(tableName);
		verify(jdbcTemplate).queryForObject("SQLcode for queriing id", null, Long.class);
		assertEquals(512L, insert.getId().longValue());
	}

	@Test
	public void testUpdateWithId() {

		GregorianCalendar calendar = new GregorianCalendar(2015, Calendar.AUGUST, 21, 8, 21, 0);
		Date now = calendar.getTime();
		calendar.add(Calendar.DAY_OF_MONTH, -10);

		String type = "contactType";
		String value = "contactValue";
		long personId = 1L;
		Contact obj = new Contact(personId, type, value, personId, now, calendar.getTime());

		String regex = "(?iu)update\\s+`tablename`\\s+set\\s+" + join("\\s*=\\s*\\?\\s*,\\s*", columnNames)
				+ "\\s*=\\s*\\?\\s*,\\s*changed\\s*=\\s*\\?\\s+where\\s+_id\\s*=\\s*\\?\\s*";

		when(jdbcTemplate.update(Matchers.anyString(), Matchers.argThat(new ObjectArrayMatcher(null)))).thenReturn(1);

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
