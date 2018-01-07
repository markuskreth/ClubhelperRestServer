package de.kreth.clubhelperbackend.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.dao.abstr.Dao;
import de.kreth.clubhelperbackend.pojo.Contact;

@Repository
public class ContactDao extends AbstractDao<Contact> implements Dao<Contact> {

	public static final String tableName = "contact";

	private static final String columnNames[] = {"type", "value", "person_id"};

	private static AbstractDao.DaoConfig<Contact> daoConfig = new DaoConfig<Contact>(
			tableName, columnNames, new ContactRowMapper(), null);

	public ContactDao() {
		super(daoConfig);
	}

	public static class ContactRowMapper extends RowMapper<Contact> {

		@Override
		public Contact mapRow(ResultSet rs, int rowNo) throws SQLException {
			Contact c = new ContactWrapper(rs.getLong("id"),
					rs.getString("type"), rs.getString("value"),
					rs.getLong("person_id"), rs.getTimestamp("changed"),
					rs.getTimestamp("created"));
			return appendDefault(c, rs);
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

		private static final long serialVersionUID = -8809447394542591592L;

		@Override
		public String toString() {
			StringBuilder bld = new StringBuilder();
			bld.append(getType()).append("=").append(getValue());
			return bld.toString();
		}

		public ContactWrapper(Long id, String type, String value, long personId,
				Date changed, Date created) {
			super(id, type, value, personId, changed, created);
		}
	}
}
