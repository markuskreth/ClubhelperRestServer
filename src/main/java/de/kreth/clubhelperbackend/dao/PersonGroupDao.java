package de.kreth.clubhelperbackend.dao;

import org.springframework.stereotype.Repository;

import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.pojo.PersonGroup;

@Repository
public class PersonGroupDao extends AbstractDao<PersonGroup> {

	public static final String COLUMN_GROUP_ID = "group_id";
	public static final String COLUMN_PERSON_ID = "person_id";

	final static String columnNames[] = {COLUMN_PERSON_ID, COLUMN_GROUP_ID};

	private final static DaoConfig<PersonGroup> config = new DaoConfig<PersonGroup>(
			"persongroup", columnNames, new RowMapper<PersonGroup>(PersonGroup.class), null);

	public PersonGroupDao() {
		super(config);
	}

}
