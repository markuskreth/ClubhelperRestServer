package de.kreth.clubhelperbackend.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import de.kreth.clubhelperbackend.pojo.Person;

@Component
public class PersonDao extends AbstractDao<Person> implements Dao<Person> {

	public PersonDao() {
		super(Person.class);
	}

	// private static final String personAllFields[] = {"_id", "prename",
	// "surname", "type", "birth", "changed", "created"};
	private static final String personFields[] = { "prename", "surname",
			"type", "birth", "changed", "created" };
	private static final String personValues[] = { "prename", "surname",
			"type", "birth", "changed" };
	private static final String SQL_INSERT_PERSON = "insert into person ("
			+ String.join(", ", personFields) + ") values (?,?,?,?,?,?)";
	private static final String SQL_UPDATE_PERSON = "update person set "
			+ String.join("=?, ", personValues) + "=? WHERE _id=?";
	private static final String SQL_DELETE_PERSON = "delete from person where _id=?";
	private static final String SQL_QUERY_PERSON_BY_ID = "select "
			+ personFields + " from person where id=?";
	private static final String SQL_QUERY_ALL_PERSON = "select * from person";

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.kreth.clubhelperbackend.IPersonDao#getById(long)
	 */
	@Override
	public Person getById(long id) {
		return super.getById(SQL_QUERY_PERSON_BY_ID, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.kreth.clubhelperbackend.Dao#insert(de.kreth.clubhelperbackend.pojo
	 * .Person)
	 */
	@Override
	public Person insert(Person p) {
		return super.insert(p, SQL_INSERT_PERSON);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.kreth.clubhelperbackend.Dao#update(de.kreth.clubhelperbackend.pojo
	 * .Person)
	 */
	@Override
	public boolean update(Person p) {
		return super.update(p, SQL_UPDATE_PERSON);
	}

	@Override
	public boolean delete(Person obj) {
		return super.delete(obj, SQL_DELETE_PERSON);
	}

	@Override
	public List<Person> getAll() {
		return super.getAll(SQL_QUERY_ALL_PERSON);
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
	protected Object[] getUpdateValues(Person p) {
		Object[] values = new Object[5];
		values[0] = p.getPrename();
		values[1] = p.getSurname();
		values[2] = p.getType();
		values[3] = p.getBirth();
		values[4] = p.getChanged();
		return values;
	}

}
