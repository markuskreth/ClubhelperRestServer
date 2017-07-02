package de.kreth.clubhelperbackend.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

public class SqlForMysql implements SqlForDialect {

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
		Statement stm = null;
		ResultSet rs = null;
		try {
			stm = dataSource.getConnection().createStatement();
			rs = stm.executeQuery("SHOW TABLES LIKE '" + tableName + "'");
			exists = rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					if (stm != null)
						stm.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return exists;
	}

}
