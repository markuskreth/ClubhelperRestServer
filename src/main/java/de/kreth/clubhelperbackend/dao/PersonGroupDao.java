package de.kreth.clubhelperbackend.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.pojo.PersonGroup;

@Repository
public class PersonGroupDao extends AbstractDao<PersonGroup> {

	private final static String columnNames[] = {"person_id", "group_id"};

	private final static DaoConfig<PersonGroup> config = new DaoConfig<PersonGroup>(
			"persongroup", columnNames, new PersonGroupRowMapper(), null);

	public PersonGroupDao() {
		super(config);
	}

	private static class PersonGroupRowMapper extends RowMapper<PersonGroup> {

		@Override
		public PersonGroup mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			PersonGroup p = new PersonGroup(-1L,
					rs.getLong("person_id"), rs.getLong("group_id"));
			return appendDefault(p, rs);
		}

		@Override
		public Collection<Object> mapObject(PersonGroup obj) {
			List<Object> values = new ArrayList<Object>();
			values.add(obj.getPersonId());
			values.add(obj.getGroupId());
			return values;
		}

	}
}
