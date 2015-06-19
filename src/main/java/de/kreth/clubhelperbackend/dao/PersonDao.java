package de.kreth.clubhelperbackend.dao;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import de.kreth.clubhelperbackend.pojo.Person;

@Component
public class PersonDao extends AbstractDao implements Dao<Person> {

//	private static final String personAllFields[] 	= {"_id", "prename", "surname", "type", "birth", "changed", "created"};
	private static final String personFields[] 		= {"prename", "surname", "type", "birth", "changed", "created"};
	private static final String personValues[] 		= {"prename", "surname", "type", "birth", "changed"};
	private static final String SQL_INSERT_PERSON = "insert into person (" + String.join(", ", personFields) + ") values (?,?,?,?,?,?)";
	private static final String SQL_UPDATE_PERSON = "update person set " + String.join("=?, ", personValues) + "=? WHERE _id=?";
	private static final String SQL_DELETE_PERSON = "delete from person where _id=?";
	private static final String SQL_QUERY_PERSON_BY_ID = "select " + personFields + " from person where id=?";
	private static final String SQL_QUERY_ALL_PERSON = "select * from person";
	
	/* (non-Javadoc)
	 * @see de.kreth.clubhelperbackend.IPersonDao#getById(long)
	 */
	@Override
	public Person getById(long id) {
		return getJdbcTemplate().queryForObject(SQL_QUERY_PERSON_BY_ID, Person.class, id);
	}
	
	/* (non-Javadoc)
	 * @see de.kreth.clubhelperbackend.Dao#insert(de.kreth.clubhelperbackend.pojo.Person)
	 */
	@Override
	public Person insert(Person p) {

		Date now = new Date();
		p = new Person(null, p.getPrename(), p.getSurname(), p.getType(), p.getBirth(), now, now);
		int inserted = getJdbcTemplate().update(SQL_INSERT_PERSON 
				, p.getPrename()
				, p.getSurname()
				, p.getType()
				, p.getBirth()
				, p.getChanged()
				, p.getCreated());
		if(inserted == 1) {
			p.setId(sqlDialect.queryForIdentity());
		} else
			p = null;
		
		return p;
	}

	/* (non-Javadoc)
	 * @see de.kreth.clubhelperbackend.Dao#update(de.kreth.clubhelperbackend.pojo.Person)
	 */
	@Override
	public boolean update(Person p) {
		int update = getJdbcTemplate().update(SQL_UPDATE_PERSON
				, p.getPrename()
				, p.getSurname()
				, p.getType()
				, p.getBirth()
				, p.getChanged()
				, p.getId());
		return update==1;
	}

	@Override
	public boolean delete(Person obj) {
		int update = getJdbcTemplate().update(SQL_DELETE_PERSON, obj.getId());
		return update==1;
	}

	@Override
	public List<Person> getAll() {
		return getJdbcTemplate().queryForList(SQL_QUERY_ALL_PERSON, Person.class);
	}

}
