package de.kreth.clubhelperbackend.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.kreth.dbmanager.Database;
import de.kreth.dbmanager.DbValue;

public class TransactionalDatabase implements Database {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Connection con;
	
	public TransactionalDatabase(Connection con) {
		super();
		this.con = con;
	}

	@Override
	public void beginTransaction() throws SQLException {
		con.setAutoCommit(false);
	}

	@Override
	public void setTransactionSuccessful() throws SQLException {
		if (!con.getAutoCommit())
			con.commit();
		con.setAutoCommit(true);
	}

	@Override
	public void endTransaction() throws SQLException {
		if (!con.getAutoCommit())
			con.rollback();
		con.setAutoCommit(true);
	}

	@Override
	public void execSQL(String sql) throws SQLException {
		try (Statement stm = con.createStatement()) {
			stm.execute(sql);
		}
	}

	@Override
	public void execSQL(String sql, Object[] bindArgs) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getVersion() {
		int version = 0;
		try (Statement stm = con.createStatement(); ResultSet rs = stm.executeQuery("SELECT version FROM version")){
			if (rs.next())
				version = rs.getInt("version");

		} catch (SQLException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Error on Database fetch version, version 0?", e);
			}
		}

		return version;
	}

	@Override
	public long insert(String table, List<DbValue> values) throws SQLException {
		return 0;
	}

	@Override
	public boolean needUpgrade(int newVersion) {
		return false;
	}

	@Override
	public Iterator<Collection<DbValue>> query(String table, String[] columns)
			throws SQLException {
		return null;
	}

	@Override
	public void setVersion(int version) {
		Statement stm = null;
		boolean autoCommit = true;
		try {
			autoCommit = con.getAutoCommit();
			con.setAutoCommit(false);
			stm = con.createStatement();
			int rows = stm
					.executeUpdate("UPDATE version SET version = " + version);
			if (rows == 1)
				con.commit();
			else
				con.rollback();
		} catch (SQLException e) {

			try {
				con.rollback();
			} catch (SQLException e1) {
				logger.error("Error on database rollback after exception "
						+ e.getMessage(), e1);
			}
			logger.error("Error updating verions", e);
		} finally {
			try {
				con.setAutoCommit(autoCommit);
			} catch (SQLException e) {
				logger.info("Error restoring autocommit to " + autoCommit, e);
			}
		}

	}

	@Override
	public int delete(String table, String whereClause, String[] whereArgs)
			throws SQLException {
		return 0;
	}

}
