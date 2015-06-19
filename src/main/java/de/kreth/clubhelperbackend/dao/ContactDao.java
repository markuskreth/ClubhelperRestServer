package de.kreth.clubhelperbackend.dao;

import java.util.List;

import de.kreth.clubhelperbackend.pojo.Contact;

public class ContactDao  extends AbstractDao<Contact> implements Dao<Contact> {

	private static final String contactFields[] 		= {"type", "value", "person_id", "changed", "created"};
	private static final String contactValues[] 		= {"type", "value", "person_id", "changed"};
	private static final String SQL_INSERT = "insert into person (" + String.join(", ", contactFields) + ") values (?,?,?,?,?)";
	private static final String SQL_UPDATE = "update person set " + String.join("=?, ", contactValues) + "=? WHERE _id=?";
	private static final String SQL_DELETE = "delete from person where _id=?";
	private static final String SQL_QUERY_BY_ID = "select " + contactFields + " from person where id=?";
	private static final String SQL_QUERY_All = "select * from person";
	
	public ContactDao() {
		super(Contact.class);
	}

	@Override
	public Contact getById(long id) {
		return super.getById(SQL_QUERY_BY_ID, id);
	}

	@Override
	public Contact insert(Contact obj) {
		return super.insert(obj, SQL_INSERT);
	}

	@Override
	public boolean update(Contact obj) {
		return super.update(obj, SQL_UPDATE);
	}

	@Override
	public boolean delete(Contact obj) {
		return super.delete(obj, SQL_DELETE);
	}

	@Override
	public List<Contact> getAll() {
		return super.getAll(SQL_QUERY_All);
	}

	@Override
	protected Object[] getInsertValues(Contact obj) {
		Object[] values = new Object[5];
		values[0] = obj.getType();
		values[1] = obj.getValue();
		values[2] = obj.getPersonId();
		values[3] = obj.getChanged();
		values[4] = obj.getCreated();
		return values;
	}

	@Override
	protected Object[] getUpdateValues(Contact obj) {
		Object[] values = new Object[4];
		values[0] = obj.getType();
		values[1] = obj.getValue();
		values[2] = obj.getPersonId();
		values[3] = obj.getChanged();
		return values;
	}

}
