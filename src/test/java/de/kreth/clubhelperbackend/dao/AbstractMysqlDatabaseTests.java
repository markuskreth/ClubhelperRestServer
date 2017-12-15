package de.kreth.clubhelperbackend.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import de.kreth.clubhelperbackend.aspects.MysqlDbCheckAspect;

public class AbstractMysqlDatabaseTests {

	static final String db_file_name_prefix = "TestDatabase";

	protected MysqlDbCheckAspect dbCheck;
	protected MysqlConnectionPoolDataSource dataSource;

	public AbstractMysqlDatabaseTests() {
		super();
	}

	@Before
	public void setUp() throws Exception {
	
		dataSource = new MysqlConnectionPoolDataSource();
		dataSource.setUser("markus");
		dataSource.setPassword("0773");
		dataSource.setServerName("localhost");
		dataSource.setPort(3306);
		dataSource.setDatabaseName("testdb");
	
		deleteTables(dataSource.getConnection());
	
		dbCheck = new MysqlDbCheckAspect(dataSource);
	
	}

	@After
	public void tearDown() throws SQLException {
	
		Connection conn = dataSource.getConnection();
		deleteTables(conn);
	
		if (dataSource != null) {
			conn.close();
		}
		File dbFile = new File(db_file_name_prefix);
		if (dbFile.exists()) {
			dbFile.delete();
		}
	}

	private void deleteTables(Connection conn) throws SQLException {
	
		String[] types = { "TABLE", "VIEW" };
		DatabaseMetaData metaData = conn.getMetaData();
		String catalog = conn.getCatalog();
	
		ResultSet rs = metaData.getTables(catalog, null, "%", types);
		List<String> allSql = new ArrayList<String>();
	
		while (rs.next()) {
			String type = rs.getString("TABLE_TYPE");
	
			if (type != null && type.startsWith("SYSTEM") == false) {
				String tableName = rs.getString("TABLE_NAME");
				String sql = String.format("DROP %s %s", type, tableName);
				allSql.add(sql);
			}
		}
		rs.close();
		Statement stm = conn.createStatement();
		for (String sql : allSql) {
			try {
				stm.execute(sql);
			} catch (SQLException e) {
				throw new SQLException("SQL: " + sql, e);
			}
		}
	}

}