package de.kreth.clubhelperbackend.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import de.kreth.clubhelperbackend.pojo.Person;

@Repository
public class PersonDao extends AbstractDao<Person> implements Dao<Person> {

	private static final String columnNames[] = { "prename", "surname", "type", "birth"};

	private final static DaoConfig<Person> config = new DaoConfig<Person>("person", columnNames, new PersonRowMapper());
	
	public PersonDao() {
		super(config);
	}

	private static class PersonRowMapper implements RowMapper<Person> {
		
		@Override
		public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
			Person p = new Person(rs.getLong("_id"), rs.getString("prename"), rs.getString("surname"), rs.getString("type"), rs.getDate("birth"), rs.getDate("changed"), rs.getDate("created"));
			return p;
		}

		@Override
		public Collection<Object> mapObject(Person p) {
			List<Object> values = new ArrayList<Object>();
			values.add(p.getPrename());
			values.add(p.getSurname());
			values.add(p.getType());
			values.add(p.getBirth());
			return values;
		}
	}

	/**
	 * This implementation returns an empty list.
	 */
	@Override
	public List<Person> getByWhere(String where) {
		return new ArrayList<>();
	}
}
