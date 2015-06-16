package de.kreth.clubhelperbackend;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.jdbc.core.JdbcTemplate;

public class SqlForMysql implements SqlForDialect {

	private JdbcTemplate dataSource;
	
	public SqlForMysql(JdbcTemplate dataSource) {
		super();
		this.dataSource = dataSource;
	}

	@Override
	public long queryForIdentity() {
		return dataSource.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
	}

	@Override
	public boolean tableExists(String tableName) {
		boolean exists = false;
		Statement stm = null;
		ResultSet rs = null;
		try {
			stm = dataSource.getDataSource().getConnection().createStatement();
			rs = stm.executeQuery("SHOW TABLES LIKE '" + tableName + "'");
			exists = rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				if(stm != null)
					stm.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return exists;
	}

	
}
