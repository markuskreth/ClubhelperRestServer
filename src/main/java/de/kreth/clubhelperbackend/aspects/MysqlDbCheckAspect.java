package de.kreth.clubhelperbackend.aspects;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Component;

import de.kreth.clubhelperbackend.config.DatabaseConfiguration;
import de.kreth.dbmanager.Database;
import de.kreth.dbmanager.DbValue;

@Component
@Aspect
public class MysqlDbCheckAspect implements Database {

	private final Logger logger;

	private Connection con;
	private boolean isChecked = false;

	@Autowired
	public MysqlDbCheckAspect(DataSource dataSource) {

		logger = LoggerFactory.getLogger(MysqlDbCheckAspect.class);

		logger.debug("init with " + dataSource);
		try {
			con = dataSource.getConnection();
			logger.debug("finished init, got con: " + con);
			checkDb();
		} catch (SQLException e) {
			throw new InvalidDataAccessApiUsageException("Keine Connection aus DataSource erhalten", e);
		}
	}

	@Before("execution (* de.kreth.clubhelperbackend.dao.*.*(..))")
	public synchronized void checkDb() {
		logger.trace("Checking Database...");
		if (isChecked)
			return;
		isChecked = true;
		logger.debug("Initalizing Database");

		try {
			int currentDbVersion = getVersion();
			logger.info("Database Version " + currentDbVersion);
			beginTransaction();
			DatabaseConfiguration manager = new DatabaseConfiguration(currentDbVersion);
			manager.executeOn(this);
			setTransactionSuccessful();
			logger.info("Installed Tables successfully.");
		} catch (SQLException e) {

			logger.error("Failed to update. Rolling back.", e);

			try {
				endTransaction();
			} catch (SQLException e1) {
				logger.warn("rollback failed", e1);
			}

			throw new DataAccessResourceFailureException("Konnte Datenbanktabellen nicht erstellen1", e);
		}
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
		con.createStatement().execute(sql);
	}

	@Override
	public void execSQL(String sql, Object[] bindArgs) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getVersion() {
		int version = 0;
		Statement stm = null;
		ResultSet rs = null;
		try {
			stm = con.createStatement();
			rs = stm.executeQuery("SELECT version FROM version");
			if (rs.next())
				version = rs.getInt("version");

		} catch (SQLException e) {
			e.printStackTrace(System.out);
		} finally {
			if (stm != null)
				try {
					stm.close();
				} catch (SQLException e) {
					e.printStackTrace();
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
	public Iterator<Collection<DbValue>> query(String table, String[] columns) throws SQLException {
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
			int rows = stm.executeUpdate("UPDATE version SET version = " + version);
			if (rows == 1)
				con.commit();
			else
				con.rollback();
		} catch (SQLException e) {

			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				con.setAutoCommit(autoCommit);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public int delete(String table, String whereClause, String[] whereArgs) throws SQLException {
		return 0;
	}

}
