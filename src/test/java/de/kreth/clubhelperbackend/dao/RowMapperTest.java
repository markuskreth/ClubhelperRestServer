package de.kreth.clubhelperbackend.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import de.kreth.clubhelperbackend.dao.AdressDao.AdressWrapper;
import de.kreth.clubhelperbackend.dao.PersonDao.PersonToString;
import de.kreth.clubhelperbackend.dao.RelativeDao.ToStringRelative;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao.ClubhelperRowMapper;
import de.kreth.clubhelperbackend.pojo.AbstractData;
import de.kreth.clubhelperbackend.pojo.Adress;
import de.kreth.clubhelperbackend.pojo.Attendance;
import de.kreth.clubhelperbackend.pojo.DeletedEntries;
import de.kreth.clubhelperbackend.pojo.Person;
import de.kreth.clubhelperbackend.pojo.PersonGroup;
import de.kreth.clubhelperbackend.pojo.Relative;
import de.kreth.clubhelperbackend.testutils.MockedLogger;
import de.kreth.clubhelperbackend.testutils.TestDataPerson;

public class RowMapperTest {

	public static final int FIRST = 1;
	private Date changed;
	private Date created;
	private Long expectedID;
	private Logger log;

	@Before
	public void setupDefaultVariables() {
		expectedID = 7L;
		changed = new Date(100000L);
		created = new Date(200000L);
		log = MockedLogger.mock();
	}

	@Test
	public void testPersonMapperMapRow() throws SQLException {

		int row_count_Person = 7;
		Iterator<Integer> shuffeldedIndicee = shuffeldedIndicee(row_count_Person).iterator();

		ResultSetMetaData meta = mockMetaData(row_count_Person, shuffeldedIndicee);

		setColumnNameType(meta, shuffeldedIndicee.next(), "prename", Types.VARCHAR, JDBCType.VARCHAR.getName());
		setColumnNameType(meta, shuffeldedIndicee.next(), "surname", Types.VARCHAR, JDBCType.VARCHAR.getName());
		setColumnNameType(meta, shuffeldedIndicee.next(), "birth", Types.DATE, JDBCType.DATE.getName());

		ResultSet rs = mock(ResultSet.class);

		when(rs.getMetaData()).thenReturn(meta);

		String prename = "TestPreName";
		String surname = "TestSurname";

		setupWithDefaultColumns(rs);

		when(rs.getString("prename")).thenReturn(prename);
		when(rs.getString("surname")).thenReturn(surname);
		Date birth = new Date(1000000L);
		when(rs.getTimestamp("birth")).thenReturn(new Timestamp(birth.getTime()));

		ClubhelperRowMapper<Person> mapper = new ClubhelperRowMapper<Person>(PersonToString.class);

		mapper.setLog(log);
		Person mapped = mapper.mapRow(rs, FIRST);

		assertDefaults(mapped);

		assertEquals(prename, mapped.getPrename());
		assertEquals(surname, mapped.getSurname());
		assertEquals(birth, mapped.getBirth());
		verify(log, never()).warn(anyString());
	}

	@Test
	public void testPersonMapperMapObject() {
		Person p = TestDataPerson.getPerson();

		ClubhelperRowMapper<Person> mapper = new ClubhelperRowMapper<Person>(PersonToString.class);

		mapper.setLog(log);
		List<Object> values = new ArrayList<>(mapper.mapObject(p, PersonDao.COLUMN_NAMES));
		assertEquals(3, values.size());
		assertEquals(p.getPrename(), values.get(0));
		assertEquals(p.getSurname(), values.get(1));
		assertEquals(p.getBirth(), values.get(2));
	}

	private void assertDefaults(AbstractData mapped) {
		assertNotNull(mapped);
		assertEquals(expectedID, mapped.getId());
		assertEquals(changed, mapped.getChanged());
		assertEquals(created, mapped.getCreated());
	}

	private void setupWithDefaultColumns(ResultSet rs) throws SQLException {
		when(rs.getLong(ClubhelperRowMapper.ID_COLUMN)).thenReturn(expectedID);

		when(rs.getDate(ClubhelperRowMapper.CHANGED_COLUMN)).thenReturn(changed);
		when(rs.getTimestamp(ClubhelperRowMapper.CHANGED_COLUMN)).thenReturn(new Timestamp(changed.getTime()));

		when(rs.getDate(ClubhelperRowMapper.CREATED_COLUMN)).thenReturn(created);
		when(rs.getTimestamp(ClubhelperRowMapper.CREATED_COLUMN)).thenReturn(new Timestamp(created.getTime()));
		when(rs.getBoolean(ClubhelperRowMapper.DELETE_COLUMN)).thenReturn(false);
	}

	private ResultSetMetaData mockMetaData(int row_count_Person, Iterator<Integer> shuffeldedIndicee)
			throws SQLException {
		ResultSetMetaData meta = mock(ResultSetMetaData.class);
		when(meta.getColumnCount()).thenReturn(row_count_Person);

		initDefaultColumns(meta, shuffeldedIndicee);
		return meta;
	}

	private void initDefaultColumns(ResultSetMetaData meta, Iterator<Integer> shuffeldedIndicee) throws SQLException {
		setColumnNameType(meta, shuffeldedIndicee.next(), ClubhelperRowMapper.ID_COLUMN, Types.INTEGER,
				JDBCType.INTEGER.getName());

		when(meta.getColumnName(shuffeldedIndicee.next())).thenReturn(ClubhelperRowMapper.CHANGED_COLUMN);
		when(meta.getColumnName(shuffeldedIndicee.next())).thenReturn(ClubhelperRowMapper.CREATED_COLUMN);
		when(meta.getColumnName(shuffeldedIndicee.next())).thenReturn(ClubhelperRowMapper.DELETE_COLUMN);
	}

	@Test
	public void testRelativeMapperMapRow() throws SQLException {

		int row_count_Relative = 8;
		Iterator<Integer> shuffeldedIndicee = shuffeldedIndicee(row_count_Relative).iterator();

		ResultSetMetaData meta = mockMetaData(row_count_Relative, shuffeldedIndicee);

		setColumnNameType(meta, shuffeldedIndicee.next(), RelativeDao.COLUMN_PERSON1, Types.INTEGER,
				JDBCType.INTEGER.getName());
		setColumnNameType(meta, shuffeldedIndicee.next(), RelativeDao.COLUMN_PERSON2, Types.INTEGER,
				JDBCType.INTEGER.getName());
		setColumnNameType(meta, shuffeldedIndicee.next(), RelativeDao.COLUMN_PERSON1_RELATION, Types.VARCHAR,
				JDBCType.VARCHAR.getName());
		setColumnNameType(meta, shuffeldedIndicee.next(), RelativeDao.COLUMN_PERSON2_RELATION, Types.VARCHAR,
				JDBCType.VARCHAR.getName());

		ResultSet rs = mock(ResultSet.class);
		when(rs.getMetaData()).thenReturn(meta);

		setupWithDefaultColumns(rs);

		Long person1 = 17L;
		Long person2 = 19L;
		when(rs.getLong(RelativeDao.COLUMN_PERSON1)).thenReturn(person1);
		when(rs.getLong(RelativeDao.COLUMN_PERSON2)).thenReturn(person2);
		String relation1 = "relation1";
		String relation2 = "relation2";
		when(rs.getString(RelativeDao.COLUMN_PERSON1_RELATION)).thenReturn(relation1);
		when(rs.getString(RelativeDao.COLUMN_PERSON2_RELATION)).thenReturn(relation2);

		ClubhelperRowMapper<Relative> mapper = new ClubhelperRowMapper<Relative>(ToStringRelative.class);
		mapper.setLog(log);

		Relative mapped = mapper.mapRow(rs, FIRST);
		assertDefaults(mapped);
		assertEquals(person1.longValue(), mapped.getPerson1());
		assertEquals(person2.longValue(), mapped.getPerson2());
		assertEquals(relation1, mapped.getToPerson1Relation());
		assertEquals(relation2, mapped.getToPerson2Relation());
		verify(log, never()).warn(anyString());
	}

	@Test
	public void testRelativeMapperMapObject() {
		Relative r = new Relative(expectedID, 2L, 3L, "toPerson2Relation", "toPerson1Relation");
		ClubhelperRowMapper<Relative> mapper = new ClubhelperRowMapper<Relative>(ToStringRelative.class);
		List<Object> values = new ArrayList<>(mapper.mapObject(r, RelativeDao.columnNames));
		assertEquals(4, values.size());
		assertEquals(r.getPerson1(), values.get(0));
		assertEquals(r.getPerson2(), values.get(1));
		assertEquals(r.getToPerson1Relation(), values.get(2));
		assertEquals(r.getToPerson2Relation(), values.get(3));

	}

	@Test
	public void testAdressMapperMapRow() throws SQLException {

		int row_count_Relative = 9;
		Iterator<Integer> shuffeldedIndicee = shuffeldedIndicee(row_count_Relative).iterator();

		ResultSetMetaData meta = mockMetaData(row_count_Relative, shuffeldedIndicee);

		setColumnNameType(meta, shuffeldedIndicee.next(), AdressDao.PERSON_ID, Types.INTEGER,
				JDBCType.INTEGER.getName());
		setColumnNameType(meta, shuffeldedIndicee.next(), AdressDao.ADRESS1, Types.VARCHAR, JDBCType.VARCHAR.getName());
		setColumnNameType(meta, shuffeldedIndicee.next(), AdressDao.ADRESS2, Types.VARCHAR, JDBCType.VARCHAR.getName());
		setColumnNameType(meta, shuffeldedIndicee.next(), AdressDao.CITY, Types.VARCHAR, JDBCType.VARCHAR.getName());
		setColumnNameType(meta, shuffeldedIndicee.next(), AdressDao.PLZ, Types.VARCHAR, JDBCType.VARCHAR.getName());

		ResultSet rs = mock(ResultSet.class);
		when(rs.getMetaData()).thenReturn(meta);

		setupWithDefaultColumns(rs);

		long personId = 71L;
		String adress1 = "adress1";
		String adress2 = "adress2";
		String city = "city";
		String plz = "99999";

		when(rs.getLong(AdressDao.PERSON_ID)).thenReturn(personId);
		when(rs.getString(AdressDao.ADRESS1)).thenReturn(adress1);
		when(rs.getString(AdressDao.ADRESS2)).thenReturn(adress2);
		when(rs.getString(AdressDao.CITY)).thenReturn(city);
		when(rs.getString(AdressDao.PLZ)).thenReturn(plz);

		ClubhelperRowMapper<Adress> mapper = new ClubhelperRowMapper<Adress>(AdressWrapper.class);
		mapper.setLog(log);

		Adress mapped = mapper.mapRow(rs, FIRST);
		assertDefaults(mapped);

		assertEquals(personId, mapped.getPersonId());
		assertEquals(adress1, mapped.getAdress1());
		assertEquals(adress2, mapped.getAdress2());
		assertEquals(city, mapped.getCity());
		assertEquals(plz, mapped.getPlz());
	}

	@Test
	public void testAdressMapperMapObject() {
		Adress a = new Adress(7L, "adress1", "adress2", "plz", "city", 13L);
		ClubhelperRowMapper<Adress> mapper = new ClubhelperRowMapper<Adress>(AdressWrapper.class);

		List<Object> values = new ArrayList<>(mapper.mapObject(a, AdressDao.columnNames));
		assertEquals(5, values.size());
		assertEquals(a.getAdress1(), values.get(0));
		assertEquals(a.getAdress2(), values.get(1));
		assertEquals(a.getPlz(), values.get(2));
		assertEquals(a.getCity(), values.get(3));
		assertEquals(a.getPersonId(), values.get(4));
	}

	@Test
	public void testDeletedEntriesMapperMapRow() throws SQLException {

		int row_count_Relative = 6;
		Iterator<Integer> shuffeldedIndicee = shuffeldedIndicee(row_count_Relative).iterator();

		ResultSetMetaData meta = mockMetaData(row_count_Relative, shuffeldedIndicee);

		setColumnNameType(meta, shuffeldedIndicee.next(), DeletedEntriesDao.COLUMN_ENTRY_ID, Types.INTEGER,
				JDBCType.INTEGER.getName());
		setColumnNameType(meta, shuffeldedIndicee.next(), DeletedEntriesDao.COLUMN_TABLENAME, Types.VARCHAR,
				JDBCType.VARCHAR.getName());

		assertFalse(shuffeldedIndicee.hasNext());
		ResultSet rs = mock(ResultSet.class);
		when(rs.getMetaData()).thenReturn(meta);

		setupWithDefaultColumns(rs);

		String tableName = "entry-table";
		Long entryId = 13L;
		when(rs.getString(DeletedEntriesDao.COLUMN_TABLENAME)).thenReturn(tableName);
		when(rs.getLong(DeletedEntriesDao.COLUMN_ENTRY_ID)).thenReturn(entryId);

		AbstractDao.ClubhelperRowMapper<DeletedEntries> mapper = new AbstractDao.ClubhelperRowMapper<DeletedEntries>(DeletedEntries.class);
		DeletedEntries mapped = mapper.mapRow(rs, FIRST);

		assertDefaults(mapped);
		assertEquals(tableName, mapped.getTablename());
		assertEquals(entryId, mapped.getEntryId());
	}

	@Test
	public void testDeletedEntriesMapperMapObject() {
		DeletedEntries obj = new DeletedEntries(3L, "tablename", 7L);
		AbstractDao.ClubhelperRowMapper<DeletedEntries> mapper = new AbstractDao.ClubhelperRowMapper<DeletedEntries>(DeletedEntries.class);
		List<Object> values = new ArrayList<>(mapper.mapObject(obj, DeletedEntriesDao.columnNames));
		assertEquals(2, values.size());
		assertEquals(obj.getTablename(), values.get(0));
		assertEquals(obj.getEntryId(), values.get(1));
	}

	@Test
	public void testPersonGroupMapperMapObject() {
		PersonGroup pg = new PersonGroup(expectedID, 13L, 14L);
		ClubhelperRowMapper<PersonGroup> mapper = new ClubhelperRowMapper<PersonGroup>(PersonGroup.class);
		List<Object> values = new ArrayList<>(mapper.mapObject(pg, PersonGroupDao.columnNames));
		assertEquals(2, values.size());
		assertEquals(pg.getPersonId(), values.get(0));
		assertEquals(pg.getGroupId(), values.get(1));
	}

	@Test
	public void testAttendanceMapperMapObject() {
		java.util.Date onDate = new GregorianCalendar(2017, Calendar.MARCH, 2, 2, 2, 2).getTime();
		java.util.Date expected = new GregorianCalendar(2017, Calendar.MARCH, 2).getTime();
		Attendance obj = new Attendance(expectedID, onDate, 14L);

		AttendanceDao.RowMapper mapper = new AttendanceDao.RowMapper();

		List<Object> values = new ArrayList<>(mapper.mapObject(obj, AttendanceDao.columnNames));
		assertEquals(2, values.size());
		assertEquals(expected, values.get(0));
		assertEquals(obj.getPersonId(), values.get(1));
	}

	@Test
	public void testPersonGroupDaoMapRow() throws SQLException {

		int row_count_Relative = 6;
		Iterator<Integer> shuffeldedIndicee = shuffeldedIndicee(row_count_Relative).iterator();

		ResultSetMetaData meta = mockMetaData(row_count_Relative, shuffeldedIndicee);

		setColumnNameType(meta, shuffeldedIndicee.next(), PersonGroupDao.COLUMN_PERSON_ID, Types.INTEGER,
				JDBCType.INTEGER.getName());
		setColumnNameType(meta, shuffeldedIndicee.next(), PersonGroupDao.COLUMN_GROUP_ID, Types.INTEGER,
				JDBCType.INTEGER.getName());

		assertFalse(shuffeldedIndicee.hasNext());
		ResultSet rs = mock(ResultSet.class);
		when(rs.getMetaData()).thenReturn(meta);

		setupWithDefaultColumns(rs);

		Long personId = 17L;
		Long groupId = 19L;

		when(rs.getLong(PersonGroupDao.COLUMN_PERSON_ID)).thenReturn(personId);
		when(rs.getLong(PersonGroupDao.COLUMN_GROUP_ID)).thenReturn(groupId);

		ClubhelperRowMapper<PersonGroup> mapper = new ClubhelperRowMapper<PersonGroup>(PersonGroup.class);
		PersonGroup mapped = mapper.mapRow(rs, FIRST);

		assertDefaults(mapped);
		assertEquals(personId.longValue(), mapped.getPersonId());
		assertEquals(groupId.longValue(), mapped.getGroupId());
	}

	@Test
	public void testPrimitiveParameter() {
		Optional<Method> first = Arrays.asList(Relative.class.getMethods()).stream()
				.filter(m -> "setPerson1".equalsIgnoreCase(m.getName())).findFirst();
		assertTrue(first.isPresent());
		Method method = first.get();
		assertEquals(1, method.getParameterCount());
		Class<?> type = method.getParameterTypes()[0];
		assertNotNull(type);
		assertEquals(long.class, type);
	}

	private void setColumnNameType(ResultSetMetaData meta, Integer index, String colName, int type, String typeName)
			throws SQLException {
		when(meta.getColumnName(index)).thenReturn(colName);
		when(meta.getColumnType(index)).thenReturn(type);
		when(meta.getColumnTypeName(index)).thenReturn(typeName);
	}

	private List<Integer> shuffeldedIndicee(int length) {
		ArrayList<Integer> result = new ArrayList<>(length);
		for (int id = 1; id <= length; id++) {
			result.add(id);
		}
		Collections.shuffle(result);
		return result;
	}
}
