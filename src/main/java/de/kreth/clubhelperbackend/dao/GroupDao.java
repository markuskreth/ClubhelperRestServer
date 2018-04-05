package de.kreth.clubhelperbackend.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.dao.abstr.Dao;
import de.kreth.clubhelperbackend.pojo.Group;

@Repository
public class GroupDao extends AbstractDao<Group> implements Dao<Group> {

	private static final String COLUMN_NAME = "name";
	public static final String TABLE_NAME = "groupDef";
	private static final String[] columnNames = {COLUMN_NAME};
	private static final DaoConfig<Group> config = new DaoConfig<Group>(
			TABLE_NAME, columnNames, new GroupRowMapper(), null);

	public GroupDao() {
		super(config);
	}

	private static class GroupRowMapper extends RowMapper<Group> {

		@Override
		public Collection<Object> mapObject(Group obj) {
			List<Object> values = new ArrayList<Object>();
			values.add(obj.getName());
			return values;
		}

		@Override
		public Class<? extends Group> getItemClass() {
			return Group.class;
		}

	}
}
