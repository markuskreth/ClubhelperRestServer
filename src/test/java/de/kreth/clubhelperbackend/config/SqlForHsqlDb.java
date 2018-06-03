package de.kreth.clubhelperbackend.config;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlForHsqlDb implements SqlForDialect {

	private final Logger log;
	private final DataSource dataSource;

	public SqlForHsqlDb(DataSource dataSource) {
		super();
		this.dataSource = dataSource;
		log = LoggerFactory.getLogger(getClass());
	}

	@Override
	public String queryForIdentity(String tableName) {
		return "CALL IDENTITY()";
	}

	@Override
	public boolean tableExists(String tableName) {
		try (Connection connection = dataSource.getConnection()) {

			DatabaseMetaData meta = connection.getMetaData();
			ResultSet res = meta.getTables(null, null, tableName.toUpperCase(),
					new String[]{"TABLE"});
			return res.next();
		} catch (SQLException e) {
			log.error("Unable to query tables in Db", e);
		}

		return false;
	}

}
