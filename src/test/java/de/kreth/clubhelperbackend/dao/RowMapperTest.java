package de.kreth.clubhelperbackend.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import de.kreth.clubhelperbackend.dao.PersonDao.PersonRowMapper;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao.RowMapper;
import de.kreth.clubhelperbackend.pojo.Person;

public class RowMapperTest {

	@Test
	public void testPersonMapperFromResultset() throws SQLException {
		Long expectedID = 7L;
		String prename = "TestPreName";
		String surname = "TestSurname";

		ResultSetMetaData meta = mock(ResultSetMetaData.class);
		int row_count_Person = 7;
		when(meta.getColumnCount()).thenReturn(row_count_Person);
		
		List<Integer> shuffeldedIndicee = shuffeldedIndicee(row_count_Person);
		when(meta.getColumnName(shuffeldedIndicee.get(0))).thenReturn(RowMapper.ID_COLUMN);
		when(meta.getColumnName(shuffeldedIndicee.get(1))).thenReturn(RowMapper.CHANGED_COLUMN);
		when(meta.getColumnName(shuffeldedIndicee.get(2))).thenReturn(RowMapper.CREATED_COLUMN);
		when(meta.getColumnName(shuffeldedIndicee.get(3))).thenReturn(RowMapper.DELETE_COLUMN);
		
		PersonRowMapper mapper = new PersonRowMapper();
		ResultSet rs = mock(ResultSet.class);
		
		when(rs.getMetaData()).thenReturn(meta);
		
		when(rs.getLong(RowMapper.ID_COLUMN)).thenReturn(expectedID);
		when(rs.getString("prename")).thenReturn(prename);
		when(rs.getString("surname")).thenReturn(surname);
		Date birth = new Date(1000000L);
		when(rs.getTimestamp("birth")).thenReturn(new Timestamp(birth.getTime()));
		Date changed = new Date(100000L);
		when(rs.getDate(RowMapper.CHANGED_COLUMN)).thenReturn(changed);
		when(rs.getTimestamp(RowMapper.CHANGED_COLUMN)).thenReturn(new Timestamp(changed.getTime()));
		Date created = new Date(200000L);
		when(rs.getDate(RowMapper.CREATED_COLUMN)).thenReturn(created);
		when(rs.getTimestamp(RowMapper.CREATED_COLUMN)).thenReturn(new Timestamp(created.getTime()));
		when(rs.getBoolean(RowMapper.DELETE_COLUMN)).thenReturn(false);
		
		Person mapped = mapper.mapRow(rs, 1);
		assertNotNull(mapped);

		assertEquals(expectedID, mapped.getId());
		assertEquals(prename, mapped.getPrename());
		assertEquals(surname, mapped.getSurname());
		assertEquals(birth, mapped.getBirth());
		assertEquals(changed, mapped.getChanged());
		assertEquals(created, mapped.getCreated());
		
	}

	private List<Integer> shuffeldedIndicee(int length) {
		ArrayList<Integer> result = new ArrayList<>(length);
		for (int id = 1; id<=length; id++) {
			result.add(id);
		}
		Collections.shuffle(result);
		return result;
	}
}
