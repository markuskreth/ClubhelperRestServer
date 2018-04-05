package de.kreth.clubhelperbackend.dao;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Repository;

import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.dao.abstr.Dao;
import de.kreth.clubhelperbackend.pojo.Person;

@Repository
public class PersonDao extends AbstractDao<Person> implements Dao<Person> {

	final static String COLUMN_NAMES[] = {"prename", "surname", "birth"};
	private final static String ORDER_BY[] = {"surname", "prename"};

	private final static DaoConfig<Person> config = new DaoConfig<Person>(
			"person", COLUMN_NAMES, new RowMapper<Person>(PersonToString.class), ORDER_BY);

	public PersonDao() {
		super(config);
	}

	public static class PersonToString extends Person {

		private static final long serialVersionUID = -2909522514132832331L;

		@Override
		public String toString() {
			StringBuilder bld = new StringBuilder();
			bld.append(getId()).append(": ").append(getPrename()).append(" ")
					.append(getSurname());
			return bld.toString();
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
