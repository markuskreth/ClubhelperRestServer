package de.kreth.clubhelperbackend.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import de.kreth.clubhelperbackend.pojo.Contact;

@Repository
public class ContactDao  extends AbstractDao<Contact> implements Dao<Contact> {

	private static final String columnNames[] 		= {"type", "value", "person_id"};

	private static AbstractDao.DaoConfig<Contact> daoConfig = new DaoConfig<Contact>("contact", columnNames, new ContactRowMapper());
	
	public ContactDao() {
		super(daoConfig);
	}

	static class ContactRowMapper implements RowMapper<Contact> {

		@Override
		public Contact mapRow(ResultSet rs, int rowNo) throws SQLException {
			Contact c = new Contact(rs.getLong("_id"), rs.getString("type"), rs.getString("value"), rs.getLong("person_id"), rs.getDate("changed"), rs.getDate("created"));
			return c;
		}

		@Override
		public Collection<Object> mapObject(Contact obj) {
			List<Object> values = new ArrayList<Object>();
			values.add(obj.getType());
			values.add(obj.getValue());
			values.add(obj.getPersonId());
			return values;
		}
	};
}
