package de.kreth.clubhelperbackend.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.pojo.PersonGroup;

@Repository
public class PersonGroupDao extends AbstractDao<PersonGroup> {

	public static final String COLUMN_GROUP_ID = "group_id";
	public static final String COLUMN_PERSON_ID = "person_id";

	private final static String columnNames[] = {COLUMN_PERSON_ID, COLUMN_GROUP_ID};

	private final static DaoConfig<PersonGroup> config = new DaoConfig<PersonGroup>(
			"persongroup", columnNames, new PersonGroupRowMapper(), null);

	public PersonGroupDao() {
		super(config);
	}

	public static class PersonGroupRowMapper extends RowMapper<PersonGroup> {

		@Override
		public Collection<Object> mapObject(PersonGroup obj) {
			List<Object> values = new ArrayList<Object>();
			values.add(obj.getPersonId());
			values.add(obj.getGroupId());
			return values;
		}

		@Override
		public Class<? extends PersonGroup> getItemClass() {
			return PersonGroup.class;
		}

	}
}
