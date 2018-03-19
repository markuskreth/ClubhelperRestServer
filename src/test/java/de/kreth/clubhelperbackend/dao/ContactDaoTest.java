package de.kreth.clubhelperbackend.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;

import de.kreth.clubhelperbackend.dao.ContactDao.ContactRowMapper;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao.DaoConfig;
import de.kreth.clubhelperbackend.pojo.Contact;
import de.kreth.clubhelperbackend.test.matchers.SqlMatcher;

public class ContactDaoTest extends AbstractDaoTest<Contact> {

	private String tableName = "tableName";
	private String[] columnNames = {"column1", "column2"};
	private ArgumentCaptor<ContactRowMapper> mapperCaptor;
	private ArgumentCaptor<Date> dateCaptor;

	@Override
	protected AbstractDao<Contact> configureDao() {

		mapper = new ContactDao.ContactRowMapper();
		DaoConfig<Contact> config = new DaoConfig<Contact>(tableName,
				columnNames, mapper, null);
		AbstractDao<Contact> dao = new AbstractDao<Contact>(config) {
		};
		return dao;
	}

	@Before
	public void init() {
		mapperCaptor = ArgumentCaptor.forClass(ContactDao.ContactRowMapper.class);
		dateCaptor = ArgumentCaptor.forClass(Date.class);
	}
	
	@Test
	public void testGetById() {

		SqlMatcher sqlMatcher = sqlMatcher(Arrays.asList("select", "*",
				"from", "tablename", "where", "id=?"));
		long id = 1L;

		dao.getById(id);
		verify(jdbcTemplate).queryForObject(
				sqlCaptor.capture(),
				mapperCaptor.capture(), 
				idCaptor.capture());
		sqlMatcher.matches(sqlCaptor.getValue());
		assertEquals(1L, idCaptor.getValue().longValue());
	}

	@Test
	public void testGetAll() {

		SqlMatcher sqlMatcher = new SqlMatcher(Arrays.asList("select", "*",
				"from", "tablename", "where", "deleted", "is", "null"));

		dao.getAll();
		verify(jdbcTemplate).query(sqlCaptor.capture(),
				mapperCaptor.capture());

		assertTrue(sqlMatcher.matches(sqlCaptor.getValue()));
	}

	@Test
	public void testDeleteObject() {

		Matcher<String> sqlMatcher = sqlMatcher(Arrays.asList("update",
				"tableName", "set", "deleted=?", "where", "id=?"));
		long id = 1;
		Contact c = new Contact();
		c.setId(id);
		dao.delete(c);
		verify(jdbcTemplate).update(sqlCaptor.capture(),
				dateCaptor.capture(), idCaptor.capture());
		sqlMatcher.matches(sqlCaptor.getValue());
		assertEquals(id, idCaptor.getValue().longValue());

	}

	private SqlMatcher sqlMatcher(final List<String> expected) {
		return new SqlMatcher(expected);
	}

	@Test
	public void testDeleteById() {

		SqlMatcher sqlMatcher = sqlMatcher(Arrays.asList("update",
				"tableName", "set", "deleted=?", "where", "id=?"));
		long id = 1l;

		dao.delete(id);
		verify(jdbcTemplate).update(sqlCaptor.capture(),
				dateCaptor.capture(), idCaptor.capture());

		assertTrue(sqlMatcher.matches(sqlCaptor.getValue()));
		assertEquals(id, idCaptor.getValue().longValue());
	}

	@Test
	public void testGetByWhere() {

		String where = "personid=1";

		Matcher<String> sqlMatcher = sqlMatcher(Arrays.asList("select", "*",
				"from", "tablename", "where", "personid=1"));
		dao.getByWhere(where);

		verify(jdbcTemplate).query(sqlCaptor.capture(),
				mapperCaptor.capture());

		sqlMatcher.matches(sqlCaptor.getValue());
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
				+ String.join("\\s*,\\s*", columnNames)
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

		when(jdbcTemplate.update(anyString(), Matchers.<Object[]>any())).thenReturn(1);

		Contact insert = dao.insert(obj);

		verify(jdbcTemplate).update(matches(regex),
				Matchers.<Object[]>any());
		assertEquals(512L, insert.getId().longValue());
	}

//	@Test
//	public void testInsertWithoutId() {
//		SqlForDialect sqlDialect = mock(SqlForDialect.class);
//		when(sqlDialect.queryForIdentity(anyString()))
//				.thenReturn("SQLcode for queriing id");
//		when(jdbcTemplate.queryForObject(
//				eq("SQLcode for queriing id"), eq(Long.class))).thenReturn(512L);
//
//		dao.setSqlDialect(sqlDialect);
//		Date now = new GregorianCalendar(2015, Calendar.AUGUST, 21, 8, 21, 0)
//				.getTime();
//
//		String type = "contactType";
//		String value = "contactValue";
//		long personId = 1L;
//		Contact obj = new Contact(null, type, value, personId, now, now);
//
//		String regex = "(?iu)insert\\s+into\\s+tablename\\s*\\(\\s*"
//				+ String.join("\\s*,\\s*", columnNames)
//				+ "\\s*,\\s*changed\\s*,\\s*created\\s*\\)\\s*values\\s*\\(\\s*"
//				+ countToQuestionmarkList(columnNames.length + 2) + "\\s*\\)";
//
//		List<Object> list = new ArrayList<Object>();
//
//		list.add(type);
//		list.add(value);
//		list.add(personId);
//
//		list.add(now);
//		list.add(now);
//		Object[] values = list.toArray();
//
//		when(jdbcTemplate.update(anyString(),
//				ArgumentMatchers.<Object[]>any())).thenReturn(1);
//
//		Contact insert = dao.insert(obj);
//
//		verify(jdbcTemplate).update(matches(regex),
//				AdditionalMatchers.aryEq(values));
//		verify(sqlDialect).queryForIdentity(tableName);
//		verify(jdbcTemplate).queryForObject("SQLcode for queriing id", null,
//				Long.class);
//		assertEquals(512L, insert.getId().longValue());
//	}
//
//	@Test
//	public void testUpdateWithId() {
//
//		GregorianCalendar calendar = new GregorianCalendar(2015,
//				Calendar.AUGUST, 21, 8, 21, 0);
//		Date now = calendar.getTime();
//		calendar.add(Calendar.DAY_OF_MONTH, -10);
//
//		String type = "contactType";
//		String value = "contactValue";
//		long personId = 1L;
//		Contact obj = new Contact(personId, type, value, personId, now,
//				calendar.getTime());
//
//		String regex = "(?iu)update\\s+tablename\\s+set\\s+"
//				+ join(columnNames, "\\s*=\\s*\\?\\s*,\\s*")
//				+ "\\s*=\\s*\\?\\s*,\\s*changed\\s*=\\s*\\?\\s+where\\s+id\\s*=\\s*\\?\\s*";
//
//		Pattern.compile(regex);
//
//		when(jdbcTemplate.update(Matchers.anyString(),
//				Matchers.argThat(new ObjectArrayMatcher(null)))).thenReturn(1);
//
//		List<Object> list = new ArrayList<Object>();
//
//		list.add(type);
//		list.add(value);
//		list.add(personId);
//
//		list.add(now);
//		list.add(2L);
//
//		Object[] values = list.toArray();
//
//		assertTrue(dao.update(2L, obj));
//		assertEquals(2L, obj.getId().longValue());
//		assertTrue(dao.update(obj)); // Now obj has Id 2, so values will match
//										// also. Update is executed also with no
//										// changes.
//		assertEquals(2L, obj.getId().longValue());
//		verify(jdbcTemplate, times(2)).update(Matchers.matches(regex),
//				Matchers.argThat(new ObjectArrayMatcher(values)));
//
//	}

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
