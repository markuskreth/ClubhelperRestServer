package de.kreth.clubhelperbackend.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlForMysql implements SqlForDialect {

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	private DataSource dataSource;

	public SqlForMysql(DataSource dataSource) {
		super();
		this.dataSource = dataSource;
	}

	@Override
	public String queryForIdentity(String tableName) {
		return "SELECT last_insert_id() from `" + tableName + "` LIMIT 1";
	}

	@Override
	public boolean tableExists(String tableName) {

		boolean exists = false;

		try (Connection connection = dataSource.getConnection()) {

			PreparedStatement showTablesLike = connection.prepareStatement("SHOW TABLES LIKE ?");
			showTablesLike.setString(1, tableName);
			ResultSet rs = showTablesLike.executeQuery();
			exists = rs.next();
			
		} catch (SQLException e) {
			LOG.error("Error on table check for tableName={}", tableName, e);
		}
		
		return exists;
	}

}
