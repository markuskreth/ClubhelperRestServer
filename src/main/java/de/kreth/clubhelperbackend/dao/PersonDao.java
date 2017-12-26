package de.kreth.clubhelperbackend.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.dao.abstr.Dao;
import de.kreth.clubhelperbackend.pojo.Person;

@Repository
public class PersonDao extends AbstractDao<Person> implements Dao<Person> {

	private final static String columnNames[] = { "prename", "surname", "birth" };

	private final static DaoConfig<Person> config = new DaoConfig<Person>("person", columnNames, new PersonRowMapper(),
			new String[] { "surname", "prename" });

	public PersonDao() {
		super(config);
	}

	private static class PersonRowMapper implements RowMapper<Person> {

		@Override
		public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
			Person p = new PersonToString(rs.getLong("_id"), rs.getString("prename"), rs.getString("surname"),
					rs.getTimestamp("birth"), rs.getTimestamp("changed"),
					rs.getTimestamp("created"));
			return p;
		}

		@Override
		public Collection<Object> mapObject(Person p) {
			List<Object> values = new ArrayList<Object>();
			values.add(p.getPrename());
			values.add(p.getSurname());
			values.add(p.getBirth());
			return values;
		}
	}

	private static class PersonToString extends Person {

		private static final long serialVersionUID = -2909522514132832331L;

		@Override
		public String toString() {
			StringBuilder bld = new StringBuilder();
			bld.append(getId()).append(": ").append(getPrename()).append(" ").append(getSurname());
			return bld.toString();
		}

		public PersonToString(Long id, String prename, String surname, Date birth, Date changed,
				Date created) {
			super(id, prename, surname, birth, changed, created);
		}

	}

	/**
	 * This implementation returns an empty list.
	 */
	@Override
	public List<Person> getByWhere(String where) {
		return new ArrayList<Person>();
	}
}
