package de.kreth.clubhelperbackend.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.pojo.DeletedEntries;

@Repository
public class DeletedEntriesDao extends AbstractDao<DeletedEntries> {

	private static final String[] columnNames = { "tablename", "entryId" };

	private static final DeletedEntriesRowMapper mapper = new DeletedEntriesRowMapper();

	private static final DaoConfig<DeletedEntries> config = new DaoConfig<>("deleted_entries", columnNames, mapper);

	public DeletedEntriesDao() {
		super(config);
	}

	private static class DeletedEntriesRowMapper implements AbstractDao.RowMapper<DeletedEntries> {

		@Override
		public DeletedEntries mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new DeletedEntries(rs.getLong("_id"), rs.getString("tablename"), rs.getLong("entryId"),
					rs.getTimestamp("changed"), rs.getTimestamp("created"));
		}

		@Override
		public Collection<Object> mapObject(DeletedEntries obj) {
			List<Object> values = new ArrayList<Object>();
			values.add(obj.getTablename());
			values.add(obj.getEntryId());
			return values;
		}

	}
}
