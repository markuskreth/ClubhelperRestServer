package de.kreth.clubhelperbackend.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.dao.abstr.Dao;
import de.kreth.clubhelperbackend.pojo.Person;

@Repository
public class PersonDao extends AbstractDao<Person> implements Dao<Person> {

	private final static String COLUMN_NAMES[] = {"prename", "surname", "birth"};
	private final static String ORDER_BY[] = {"surname", "prename"};

	private final static DaoConfig<Person> config = new DaoConfig<Person>(
			"person", COLUMN_NAMES, new PersonRowMapper(), ORDER_BY);

	public PersonDao() {
		super(config);
	}

	static class PersonRowMapper extends RowMapper<Person> {

		@Override
		public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
			Person p = new PersonToString(-1L,
					rs.getString("prename"), rs.getString("surname"),
					rs.getTimestamp("birth"));
			return appendDefault(p, rs);
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
			bld.append(getId()).append(": ").append(getPrename()).append(" ")
					.append(getSurname());
			return bld.toString();
		}

		public PersonToString(Long id, String prename, String surname,
				Date birth) {
			super(id, prename, surname, birth);
		}

	}

	/**
	 * This implementation returns an empty list.
	 */
	@Override
	public List<Person> getByWhere(String where) {
		return Collections.emptyList();
	}
}
