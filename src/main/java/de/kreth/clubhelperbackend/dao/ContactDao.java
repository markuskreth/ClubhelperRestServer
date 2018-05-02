package de.kreth.clubhelperbackend.dao;

import org.springframework.stereotype.Repository;

import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.dao.abstr.Dao;
import de.kreth.clubhelperbackend.pojo.Contact;

@Repository
public class ContactDao extends AbstractDao<Contact> implements Dao<Contact> {

	public static final String tableName = "contact";

	static final String[] columnNames = {"type", "value", "person_id"};

	private static AbstractDao.DaoConfig<Contact> daoConfig = new DaoConfig<Contact>(
			tableName, columnNames, new RowMapper<Contact>(ContactWrapper.class), null);

	public ContactDao() {
		super(daoConfig);
	}

	public static class ContactWrapper extends Contact {

		private static final long serialVersionUID = -8809447394542591592L;

		@Override
		public String toString() {
			StringBuilder bld = new StringBuilder();
			bld.append(getType()).append("=").append(getValue());
			return bld.toString();
		}

	}
}
