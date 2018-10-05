package de.kreth.clubhelperbackend.dao;

import static org.apache.commons.lang3.StringUtils.join;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.ArgumentMatchers.same;
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

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import de.kreth.clubhelperbackend.config.SqlForDialect;
import de.kreth.clubhelperbackend.config.SqlForMysql;
import de.kreth.clubhelperbackend.dao.ContactDao.ContactWrapper;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao.DaoConfig;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao.RowMapper;
import de.kreth.clubhelperbackend.pojo.Contact;
import de.kreth.clubhelperbackend.testutils.MockitoMatchers;

public class ContactDaoTest extends AbstractDaoTest<Contact> {

	private String tableName = "tableName";
	// private String[] columnNames = {"column1", "column2"};

	protected AbstractDao<Contact> configureDao() {

		mapper = new RowMapper<Contact>(ContactWrapper.class);
		DaoConfig<Contact> config = new DaoConfig<Contact>(tableName,
				ContactDao.columnNames, mapper, null);
		AbstractDao<Contact> dao = new AbstractDao<Contact>(config) {
		};
		return dao;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetById() {

		List<String> sqlItems = Arrays.asList("select", "*",
				"from", "tablename", "where", "id=?");

		long id = 1;

		dao.getById(id);

		verify(jdbcTemplate).queryForObject(MockitoMatchers.tokens(sqlItems),
				any(RowMapper.class), eq(id));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetAll() {

		List<String> expected = Arrays.asList("select", "*",
				"from", "tablename", "where", "deleted", "is", "null");

		dao.getAll();

		verify(jdbcTemplate).query(MockitoMatchers.tokens(expected), any(RowMapper.class));
	}

	@Test
	public void testDeleteObject() {

		long id = 1;
		Contact c = new Contact();
		c.setId(id);

		dao.delete(c);
		verify(jdbcTemplate).update(MockitoMatchers.tokens(Arrays.asList("update",
				"tableName", "set", "deleted=?", "where", "id=?")), any(Date.class),
				eq(id));

	}
	
	@Test
	public void testDeleteById() {

		long id = 1;

		dao.delete(id);
		verify(jdbcTemplate).update(MockitoMatchers.tokens(Arrays.asList("update",
				"tableName", "set", "deleted=?", "where", "id=?")), any(Date.class),
				eq(id));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetByWhere() {

		String where = "personid=1";

		dao.getByWhere(where);

		verify(jdbcTemplate).query(MockitoMatchers.tokens(Arrays.asList("select", "*",
				"from", "tablename", "where", "personid=1", "AND", "deleted", "is", "null")), any(RowMapper.class));

	}

	@Test
	public void testInsertWithId() {

		Date now = new GregorianCalendar(2015, Calendar.AUGUST, 21, 8, 21, 0)
				.getTime();

		String type = "contactType";
		String value = "contactValue";
		long personId = 1L;
		Contact obj = new Contact(512L, type, value, personId);
		obj.setChanged(now);
		obj.setCreated(now);

		String regex = "(?iu)insert\\s+into\\s+tablename\\s*\\(\\s*id\\s*,\\s*"
				+ join(ContactDao.columnNames, "\\s*,\\s*")
				+ "\\s*,\\s*changed\\s*,\\s*created\\s*\\)\\s*values\\s*\\(\\s*"
				+ countToQuestionmarkList(ContactDao.columnNames.length + 3)
				+ "\\s*\\)";

		List<Object> list = new ArrayList<Object>();
		list.add(512L);
		list.add(type);
		list.add(value);
		list.add(personId);

		list.add(now);
		list.add(now);

		when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object>any())).thenReturn(1);

		Contact insert = dao.insert(obj);

		ArgumentCaptor<Object> capt = ArgumentCaptor.forClass(Object.class); 
		
		verify(jdbcTemplate).update(matches(regex), capt.capture());
		
		assertEquals(512L, insert.getId().longValue());
		List<Object> actual = capt.getAllValues();
		assertArrayEquals(list.toArray(), actual.toArray());
	}

	@Test
	public void testInsertWithoutId() {
		SqlForDialect sqlDialect = mock(SqlForMysql.class);
		when(sqlDialect.queryForIdentity(any(String.class)))
				.thenReturn("SQLcode for queriing id");
		when(jdbcTemplate.queryForObject(same("SQLcode for queriing id"), any(),
				same(Long.class))).thenReturn(512L);

		dao.setSqlDialect(sqlDialect);
		Date now = new GregorianCalendar(2015, Calendar.AUGUST, 21, 8, 21, 0)
				.getTime();

		String type = "contactType";
		String value = "contactValue";
		long personId = 1L;
		Contact obj = new Contact(null, type, value, personId);
		obj.setChanged(now);
		obj.setCreated(now);

		List<Object> list = new ArrayList<Object>();

		list.add(type);
		list.add(value);
		list.add(personId);

		list.add(now);
		list.add(now);

		when(jdbcTemplate.update(anyString(),ArgumentMatchers.<Object>any())).thenReturn(1);

		Contact insert = dao.insert(obj);

		ArgumentCaptor<Object> capt = ArgumentCaptor.forClass(Object.class); 
		verify(jdbcTemplate).update(MockitoMatchers.tokens(Arrays.asList("insert", "into", "tableName", "(type,", "value,", "person_id,", "changed,", "created)", "values", "(?,?,?,?,?)")),
				capt.capture());
		List<Object> actual = capt.getAllValues();
		assertArrayEquals(list.toArray(), actual.toArray());
		
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
		Contact obj = new Contact(personId, type, value, personId);
		obj.setChanged(now);
		obj.setCreated(calendar.getTime());

		when(jdbcTemplate.update(anyString(), ArgumentMatchers.<Object>any())).thenReturn(1);

		List<Object> list = new ArrayList<Object>();

		list.add(type);
		list.add(value);
		list.add(personId);

		list.add(now);
		list.add(2L);

		assertTrue(dao.update(2L, obj));
		assertEquals(2L, obj.getId().longValue());
		assertTrue(dao.update(obj)); // Now obj has Id 2, so values will match
										// also. Update is executed also with no
										// changes.
		assertEquals(2L, obj.getId().longValue());

		ArgumentCaptor<Object> capt = ArgumentCaptor.forClass(Object.class); 
		
		verify(jdbcTemplate, times(2)).update(MockitoMatchers.tokens(Arrays.asList("update",
				"tableName", "set", "type=?,", "value=?,", "person_id=?,",
				"changed=?", "where", "id=?")),
				capt.capture());
		list.addAll(list);
		List<Object> actual = capt.getAllValues();
		assertArrayEquals(list.toArray(), actual.toArray());

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
