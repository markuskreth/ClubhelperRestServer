package de.kreth.clubhelperbackend.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import de.kreth.clubhelperbackend.pojo.Group;

@Repository
public class GroupDao extends AbstractDao<Group> implements Dao<Group> {

	private static final String tableName = "group";
	private static final String[] columnNames = { "name" };
	private static final DaoConfig<Group> config = new DaoConfig<>(tableName, columnNames, new GroupRowMapper());

	public GroupDao() {
		super(config);
	}

	private static class GroupRowMapper implements RowMapper<Group> {

		@Override
		public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
			Group g = new Group(rs.getLong("_id"), rs.getString("name"), rs.getTimestamp("changed"),
					rs.getTimestamp("created"));
			return g;
		}

		@Override
		public Collection<Object> mapObject(Group obj) {
			List<Object> values = new ArrayList<Object>();
			values.add(obj.getName());
			return values;
		}

	}
}
