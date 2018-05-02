package de.kreth.clubhelperbackend.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.After;
import org.junit.Before;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import de.kreth.clubhelperbackend.aspects.DbCheckAspect;
import de.kreth.clubhelperbackend.config.SqlForHsqlDb;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.pojo.Data;
import de.kreth.dbmanager.DatabaseType;

public abstract class AbstractDatabaseTests<T extends Data> {

	static final String db_file_name_prefix = "TestDatabase";

	protected DbCheckAspect dbCheck;
	protected DataSource dataSource;
	protected AbstractDao<T> dao;

	protected DataSourceTransactionManager transMan;

	protected DeletedEntriesDao deletedEntriesDao;

	@Before
	public void setUp() throws Exception {
		JDBCDataSource ds = new JDBCDataSource();
		ds.setUrl("jdbc:hsqldb:mem:testdb");
		ds.setUser("sa");

		dataSource = ds;

		dbCheck = new DbCheckAspect(dataSource, DatabaseType.HSQLDB);
		dao = initDao();

		dbCheck.checkDb();
		transMan = new DataSourceTransactionManager(dataSource);

		deletedEntriesDao = new DeletedEntriesDao();
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
		conn.close();

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
		List<String> tableOrder = Arrays.asList("version", "deleted_entries",
				"contact", "relative", "ATTENDANCE".toLowerCase(), "adress",
				"persongroup", "startpass_startrechte", "startpaesse",
				"groupdef", "person");

		allSql.sort((sql1, sql2) -> {

			String t1 = sql1.trim().substring(sql1.trim().lastIndexOf(' ') + 1)
					.toLowerCase();
			String t2 = sql2.trim().substring(sql2.trim().lastIndexOf(' ') + 1)
					.toLowerCase();

			if (tableOrder.contains(t1) || tableOrder.contains(t2)) {
				return Integer.compare(tableOrder.indexOf(t1),
						tableOrder.indexOf(t2));
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