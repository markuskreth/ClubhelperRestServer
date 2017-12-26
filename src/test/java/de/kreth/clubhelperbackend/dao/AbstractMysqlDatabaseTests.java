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
import org.junit.BeforeClass;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import de.kreth.clubhelperbackend.aspects.MysqlDbCheckAspect;
import de.kreth.clubhelperbackend.config.SqlForMysql;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.pojo.Data;

public abstract class AbstractMysqlDatabaseTests<T extends Data> {

	static final String db_file_name_prefix = "TestDatabase";

	protected MysqlDbCheckAspect dbCheck;
	protected static MysqlConnectionPoolDataSource dataSource;
	protected AbstractDao<T> dao;

	public AbstractMysqlDatabaseTests() {
		super();
	}

	@BeforeClass
	public static void initDbConnection() {

		dataSource = new MysqlConnectionPoolDataSource();
		dataSource.setUser("markus");
		dataSource.setPassword("0773");
		dataSource.setServerName("localhost");
		dataSource.setPort(3306);
		dataSource.setDatabaseName("testdb");
	
	}

	@Before
	public void setUp() throws Exception {
		deleteTables(dataSource.getConnection());
		dbCheck = new MysqlDbCheckAspect(dataSource);
		dao = initDao();

		dbCheck.checkDb();
		DataSourceTransactionManager transMan = new DataSourceTransactionManager(dataSource);

		DeletedEntriesDao deletedEntriesDao = new DeletedEntriesDao();
		deletedEntriesDao.setDataSource(dataSource);
		deletedEntriesDao.setPlatformTransactionManager(transMan);
		deletedEntriesDao.setSqlDialect(new SqlForMysql(dataSource));

		dao.setDataSource(dataSource);
		dao.setPlatformTransactionManager(transMan);
		dao.setDeletedEntriesDao(deletedEntriesDao);

		dao.setSqlDialect(new SqlForMysql(dataSource));
		
	}

	public abstract AbstractDao<T> initDao();

	@After
	public void tearDown() throws SQLException {
	
		Connection conn = dataSource.getConnection();
		deleteTables(conn);
	
		if (conn != null) {
			conn.close();
		}
		File dbFile = new File(db_file_name_prefix);
		if (dbFile.exists()) {
			dbFile.delete();
		}
	}

	protected void deleteTables(Connection conn) throws SQLException {
	
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
		allSql.sort((sql1, sql2)-> {

			if(sql1.toLowerCase().endsWith(" person")) {
				return 1;
			}
			if(sql2.toLowerCase().endsWith(" person")) {
				return -1;
			}
			return sql1.compareTo(sql2);
					
		});
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