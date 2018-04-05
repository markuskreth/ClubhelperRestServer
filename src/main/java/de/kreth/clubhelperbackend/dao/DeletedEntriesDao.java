package de.kreth.clubhelperbackend.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.pojo.DeletedEntries;

@Repository
public class DeletedEntriesDao extends AbstractDao<DeletedEntries> {

	public static final String COLUMN_ENTRY_ID = "entryId";
	public static final String COLUMN_TABLENAME = "tablename";
	public static final String TABLE_NAME = "deleted_entries";

	private static final String[] columnNames = { COLUMN_TABLENAME, COLUMN_ENTRY_ID };

	private static final DaoConfig<DeletedEntries> config = new DaoConfig<DeletedEntries>(TABLE_NAME, columnNames,
			new DeletedEntriesRowMapper(), null);

	public DeletedEntriesDao() {
		super(config);
	}

	public static class DeletedEntriesRowMapper extends AbstractDao.RowMapper<DeletedEntries> {

		@Override
		public Collection<Object> mapObject(DeletedEntries obj) {
			List<Object> values = new ArrayList<Object>();
			values.add(obj.getTablename());
			values.add(obj.getEntryId());
			return values;
		}

		@Override
		public Class<? extends DeletedEntries> getItemClass() {
			return DeletedEntries.class;
		}

	}

	@Override
	public void setDeletedEntriesDao(DeletedEntriesDao deletedEntriesDao) {
		super.setDeletedEntriesDao(this);
	}

	@Override
	public boolean delete(DeletedEntries obj) {
		getJdbcTemplate().execute("DELETE FROM " + TABLE_NAME + " WHERE _id=" + obj.getId());
		return true;
	}

	@Override
	public boolean undelete(long id) {
		throw new UnsupportedOperationException("Delete Entry cannot be undeleted!");
	}
}
