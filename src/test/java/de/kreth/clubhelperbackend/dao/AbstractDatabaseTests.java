package de.kreth.clubhelperbackend.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import de.kreth.clubhelperbackend.aspects.DbCheckAspect;
import de.kreth.clubhelperbackend.config.SqlForHsqlDb;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.pojo.Data;
import de.kreth.dbmanager.DatabaseType;

public abstract class AbstractDatabaseTests<T extends Data> {

	static final String db_file_name_prefix = "TestDatabase";

	protected DbCheckAspect dbCheck;
	protected static DataSource dataSource;
	protected AbstractDao<T> dao;

	public AbstractDatabaseTests() {
		super();
	}

	@BeforeClass
	public static void initDbConnection() throws Exception {
		JDBCDataSource ds = new JDBCDataSource();
		ds.setUrl("jdbc:hsqldb:mem:testdb");
		ds.setUser("sa");

		dataSource = ds;
	}

	@Before
	public void setUp() throws Exception {
		deleteTables(dataSource.getConnection());
		dbCheck = new DbCheckAspect(dataSource, DatabaseType.HSQLDB);
		dao = initDao();

		dbCheck.checkDb();
		DataSourceTransactionManager transMan = new DataSourceTransactionManager(
				dataSource);

		DeletedEntriesDao deletedEntriesDao = new DeletedEntriesDao();
		deletedEntriesDao.setDataSource(dataSource);
		deletedEntriesDao.setPlatformTransactionManager(transMan);
		deletedEntriesDao.setSqlDialect(new SqlForHsqlDb(dataSource));

		dao.setDataSource(dataSource);
		dao.setPlatformTransactionManager(transMan);
		dao.setDeletedEntriesDao(deletedEntriesDao);

		dao.setSqlDialect(new SqlForHsqlDb(dataSource));

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

		String[] types = {"TABLE", "VIEW"};
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
		allSql.sort((sql1, sql2) -> {

			if (sql1.toLowerCase().endsWith(" person")) {
				return 1;
			}
			if (sql2.toLowerCase().endsWith(" person")) {
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