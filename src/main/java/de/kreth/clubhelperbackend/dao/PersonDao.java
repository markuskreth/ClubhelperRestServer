package de.kreth.clubhelperbackend.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import de.kreth.clubhelperbackend.pojo.Person;

@Repository
public class PersonDao extends AbstractDao<Person> implements Dao<Person> {

	private static final String personFields[] = { "prename", "surname",
			"type", "birth", "changed", "created" };
	private static final String personValues[] = { "prename", "surname",
			"type", "birth", "changed" };
	private static final String SQL_INSERT = "insert into person ("
			+ String.join(", ", personFields) + ") values (?,?,?,?,?,?)";
	private static final String SQL_UPDATE = "update person set "
			+ String.join("=?, ", personValues) + "=? WHERE _id=?";
	private static final String SQL_DELETE = "delete from person where _id=?";
	private static final String SQL_QUERY_ALL = "select * from person";
	private static final String SQL_QUERY_BY_ID = SQL_QUERY_ALL + " where _id=?";

	private final PersonRowMapper rowMapper = new PersonRowMapper();
	
	public PersonDao() {
		super(SQL_QUERY_BY_ID, SQL_INSERT, SQL_UPDATE, SQL_DELETE, SQL_QUERY_ALL);
	}

	@Override
	protected Object[] getInsertValues(Person p) {
		Object[] values = new Object[6];
		values[0] = p.getPrename();
		values[1] = p.getSurname();
		values[2] = p.getType();
		values[3] = p.getBirth();
		values[4] = p.getChanged();
		values[5] = p.getCreated();
		return values;
	}

	@Override
	protected Object[] getUpdateValues(long id, Person p) {
		Object[] values = new Object[6];
		values[0] = p.getPrename();
		values[1] = p.getSurname();
		values[2] = p.getType();
		values[3] = p.getBirth();
		values[4] = p.getChanged();
		values[5] = id;
		return values;
	}

	@Override
	protected RowMapper<Person> getRowMapper() {
		return rowMapper;
	}

	private class PersonRowMapper implements RowMapper<Person> {
		@Override
		public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
			Person p = new Person(rs.getLong("_id"), rs.getString("prename"), rs.getString("surname"), rs.getString("type"), rs.getDate("birth"), rs.getDate("changed"), rs.getDate("created"));
			return p;
		}
	}
	
}
