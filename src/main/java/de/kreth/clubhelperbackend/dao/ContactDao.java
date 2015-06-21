package de.kreth.clubhelperbackend.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import de.kreth.clubhelperbackend.pojo.Contact;

public class ContactDao  extends AbstractDao<Contact> implements Dao<Contact> {

	private static final String contactFields[] 		= {"type", "value", "person_id", "changed", "created"};
	private static final String contactValues[] 		= {"type", "value", "person_id", "changed"};
	private static final String SQL_INSERT = "insert into person (" + String.join(", ", contactFields) + ") values (?,?,?,?,?)";
	private static final String SQL_UPDATE = "update person set " + String.join("=?, ", contactValues) + "=? WHERE _id=?";
	private static final String SQL_DELETE = "delete from person where _id=?";
	private static final String SQL_QUERY_BY_ID = "select " + contactFields + " from person where id=?";
	private static final String SQL_QUERY_ALL = "select * from person";
	
	public ContactDao() {
		super(Contact.class, SQL_QUERY_BY_ID, SQL_INSERT, SQL_UPDATE, SQL_DELETE, SQL_QUERY_ALL);
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
	protected Object[] getUpdateValues(long id, Contact obj) {
		Object[] values = new Object[5];
		values[0] = obj.getType();
		values[1] = obj.getValue();
		values[2] = obj.getPersonId();
		values[3] = obj.getChanged();
		values[4] = id;
		return values;
	}

	@Override
	protected RowMapper<Contact> getRowMapper() {
		return rowMapper;
	}

	private final RowMapper<Contact> rowMapper = new RowMapper<Contact>() {
//		private static final String contactFields[] 		= {"type", "value", "person_id", "changed", "created"};
		@Override
		public Contact mapRow(ResultSet rs, int rowNo) throws SQLException {
			Contact c = new Contact(rs.getLong("_id"), rs.getString("type"), rs.getString("value"), rs.getLong("person_id"), rs.getDate("changed"), rs.getDate("created"));
			return c;
		}
	};
}
