package de.kreth.clubhelperbackend;

import java.util.Date;

import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import de.kreth.clubhelperbackend.pojo.Person;

@Component
public class PersonDao extends JdbcDaoSupport implements Dao<Person> {

//	private static final String personAllFields[] 	= {"_id", "prename", "surname", "type", "birth", "changed", "created"};
	private static final String personFields[] 		= {"prename", "surname", "type", "birth", "changed", "created"};
	private static final String personValues[] 		= {"prename", "surname", "type", "birth", "changed"};
	private static final String SQL_INSERT_PERSON = "insert into person (" + String.join(", ", personFields) + ") values (?,?,?,?,?,?)";
	private static final String SQL_UPDATE_PERSON = "update person set " + String.join("=?, ", personValues) + "=? WHERE _id=?";
	
	private SqlForDialect sqlDialect;
	
	public SqlForDialect getSqlDialect() {
		return sqlDialect;
	}

	public void setSqlDialect(SqlForDialect sqlDialect) {
		this.sqlDialect = sqlDialect;
	}

	/* (non-Javadoc)
	 * @see de.kreth.clubhelperbackend.IPersonDao#getById(long)
	 */
	@Override
	public Person getById(long Id) {
		return getJdbcTemplate().queryForObject("select " + personFields + " from person where id=?", Person.class, Id);
	}
	
	/* (non-Javadoc)
	 * @see de.kreth.clubhelperbackend.Dao#insert(de.kreth.clubhelperbackend.pojo.Person)
	 */
	@Override
	public Person insert(Person p) {

		Date now = new Date();
		p = new Person(null, p.getPrename(), p.getSurname(), p.getType(), p.getBirth(), now, now);
		getJdbcTemplate().update(SQL_INSERT_PERSON 
				, p.getPrename()
				, p.getSurname()
				, p.getType()
				, p.getBirth()
				, p.getChanged()
				, p.getCreated());
		p.setId(sqlDialect.queryForIdentity());
		
		return p;
	}

	/* (non-Javadoc)
	 * @see de.kreth.clubhelperbackend.Dao#update(de.kreth.clubhelperbackend.pojo.Person)
	 */
	@Override
	public void update(Person p) {
		getJdbcTemplate().update(SQL_UPDATE_PERSON
				, p.getPrename()
				, p.getSurname()
				, p.getType()
				, p.getBirth()
				, p.getChanged()
				, p.getId());
	}

}
