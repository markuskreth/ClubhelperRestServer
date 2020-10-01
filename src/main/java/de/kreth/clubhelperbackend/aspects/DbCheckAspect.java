package de.kreth.clubhelperbackend.aspects;

import java.sql.Connection;
import java.sql.SQLException;

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
import de.kreth.clubhelperbackend.config.TransactionalDatabase;
import de.kreth.dbmanager.DatabaseType;

@Component
@Aspect
public class DbCheckAspect {

	private final Logger logger;

	private final DatabaseType dbType;
	private boolean isChecked = false;

	private DataSource dataSource;

	@Autowired
	public DbCheckAspect(DataSource dataSource) {
		this(dataSource, DatabaseType.MYSQL, LoggerFactory.getLogger(DbCheckAspect.class));
	}

	public DbCheckAspect(DataSource dataSource, DatabaseType dbType, Logger log) {

		logger = log;
		this.dbType = dbType;
		this.dataSource = dataSource;

		if (logger.isInfoEnabled()) {
			try (Connection con = dataSource.getConnection()) {

				logger.info("finished db init, got con to " + con.getMetaData().getURL());
			} catch (SQLException e) {
				throw new InvalidDataAccessApiUsageException("Keine Connection aus DataSource erhalten", e);
			}

		}
		checkDb(false);
	}

	@Before("execution (* de.kreth.clubhelperbackend.dao.*.*(..))")
	public void checkDb() {
		checkDb(false);
	}

	public synchronized void checkDb(boolean force) {
		if (isChecked && force == false) {
			if (logger.isTraceEnabled()) {
				logger.trace("Database already checked.");
			}
			return;
		}
		
		isChecked = true;
		if (logger.isDebugEnabled()) {
			logger.debug("Initalizing Database");
		}

		try (Connection con = dataSource.getConnection()) {
			TransactionalDatabase db = new TransactionalDatabase(con);

			int currentDbVersion = db.getVersion();
			if (logger.isInfoEnabled()) {
				logger.info("Database Version " + currentDbVersion);
			}
			db.beginTransaction();
			try {

				DatabaseConfiguration manager = new DatabaseConfiguration(currentDbVersion, dbType);
				manager.executeOn(db);
				db.setTransactionSuccessful();
			} catch (SQLException e) {
				logger.error("Failed to update. Rolling back.", e);
				throw e;
			} finally {
				db.endTransaction();
			}
		} catch (SQLException e) {
			throw new DataAccessResourceFailureException("Konnte Datenbanktabellen nicht erstellen!", e);
		}
	}

}
