package de.kreth.clubhelperbackend.dao;

import java.util.Date;
import java.util.List;

import de.kreth.clubhelperbackend.pojo.Contact;

public class ContactDao  extends AbstractDao implements Dao<Contact> {

	private static final String contactFields[] 		= {"type", "value", "person_id", "changed", "created"};
	private static final String contactValues[] 		= {"type", "value", "person_id", "changed"};
	private static final String SQL_INSERT_CONTACT = "insert into person (" + String.join(", ", contactFields) + ") values (?,?,?,?,?)";
	private static final String SQL_UPDATE_CONTACT = "update person set " + String.join("=?, ", contactValues) + "=? WHERE _id=?";
	private static final String SQL_DELETE_CONTACT = "delete from person where _id=?";
	private static final String SQL_QUERY_CONTACT_BY_ID = "select " + contactFields + " from person where id=?";
	private static final String SQL_QUERY_All_CONTACT = "select * from person";
	
	@Override
	public Contact getById(long id) {
		
		return getJdbcTemplate().queryForObject(SQL_QUERY_CONTACT_BY_ID, Contact.class, id);
	}

	@Override
	public Contact insert(Contact obj) {
		Date now = new Date();
		obj = new Contact(null, obj.getType(), obj.getValue(), obj.getPersonId(), now, now);
		int inserted = getJdbcTemplate().update(SQL_INSERT_CONTACT, 
				obj.getType()
				, obj.getValue()
				, obj.getPersonId()
				, obj.getChanged()
				, obj.getCreated());
		if(inserted == 1) {
			obj.setId(sqlDialect.queryForIdentity());
		} else
			obj = null;
		return obj;
	}

	@Override
	public boolean update(Contact obj) {
		int update = getJdbcTemplate().update(SQL_UPDATE_CONTACT, 
				obj.getType()
				, obj.getValue()
				, obj.getPersonId()
				, obj.getChanged());

		return update==1;
	}

	@Override
	public boolean delete(Contact obj) {
		int update = getJdbcTemplate().update(SQL_DELETE_CONTACT, obj.getId());
		return update == 1;
	}

	@Override
	public List<Contact> getAll() {
		return getJdbcTemplate().queryForList(SQL_QUERY_All_CONTACT, Contact.class);
	}

}
