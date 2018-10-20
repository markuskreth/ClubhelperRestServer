package de.kreth.clubhelperbackend.dao;

import org.springframework.stereotype.Repository;

import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.pojo.DeletedEntries;

@Repository
public class DeletedEntriesDao extends AbstractDao<DeletedEntries> {

	public static final String COLUMN_ENTRY_ID = "entryId";
	public static final String COLUMN_TABLENAME = "tablename";
	public static final String TABLE_NAME = "deleted_entries";

	static final String[] columnNames = { COLUMN_TABLENAME, COLUMN_ENTRY_ID };

	private static final DaoConfig<DeletedEntries> config = new DaoConfig<DeletedEntries>(TABLE_NAME, columnNames,
			new AbstractDao.ClubhelperRowMapper<DeletedEntries>(DeletedEntries.class), null);

	public DeletedEntriesDao() {
		super(config);
	}

	@Override
	public void setDeletedEntriesDao(DeletedEntriesDao deletedEntriesDao) {
		super.setDeletedEntriesDao(this);
	}

	@Override
	public boolean delete(DeletedEntries obj) {
		getJdbcTemplate().execute("DELETE FROM " + TABLE_NAME + " WHERE " + AbstractDao.ID_COLUMN + "=" + obj.getId());
		return true;
	}

	@Override
	public boolean undelete(long id) {
		throw new UnsupportedOperationException("Delete Entry cannot be undeleted!");
	}
}
