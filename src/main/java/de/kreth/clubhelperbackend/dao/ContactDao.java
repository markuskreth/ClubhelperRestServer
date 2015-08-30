package de.kreth.clubhelperbackend.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
			Contact c = new ContactWrapper(rs.getLong("_id"), rs.getString("type"), rs.getString("value"), rs.getLong("person_id"), rs.getTimestamp("changed"), rs.getTimestamp("created"));
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
	
	public static class ContactWrapper extends Contact {
		
		@Override
		public String toString() {
			StringBuilder bld = new StringBuilder();
			bld.append(getType()).append("=").append(getValue());
			return bld.toString();
		}

		public ContactWrapper(Long id, String type, String value,
				long personId, Date changed, Date created) {
			super(id, type, value, personId, changed, created);
		}
	}
}
