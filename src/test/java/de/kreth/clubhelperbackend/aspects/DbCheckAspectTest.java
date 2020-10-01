package de.kreth.clubhelperbackend.aspects;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import de.kreth.clubhelperbackend.config.TransactionalDatabase;
import de.kreth.clubhelperbackend.testutils.MockedLogger;
import de.kreth.clubhelperbackend.testutils.MockitoMatchers;
import de.kreth.dbmanager.DatabaseType;
import de.kreth.testutils.sql.TestResultset;

public class DbCheckAspectTest {

	private Logger log;
	@Mock
	private DataSource dataSource;
	@Mock
	private Connection connection;
	@Mock
	private Statement stm;
	@Mock
	private DatabaseMetaData metaData;
	private DbCheckAspect dbCheck;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		log = MockedLogger.mock();
		setupDataSource();
		dbCheck = new DbCheckAspect(dataSource, DatabaseType.MYSQL, log);
	}

	private void setupDataSource() throws SQLException {
		when(dataSource.getConnection()).thenReturn(connection);
		when(dataSource.getConnection(anyString(), anyString())).thenReturn(connection);
		when(connection.createStatement()).thenReturn(stm);
		when(connection.getMetaData()).thenReturn(metaData);
		TestResultset versionRs = new TestResultset();
		Map<String, Object> versionRow = new HashMap<>();
		versionRow.put("version", 1);
		versionRs.add(versionRow);
		
		when(stm.executeQuery(argThat(MockitoMatchers.eqCaseInsensitive("SELECT version FROM version")))).thenReturn(versionRs);
	}

	@Test
	public void testSqlExceptions() throws SQLException {
		dbCheck.checkDb(true);
		verify(connection, atLeastOnce()).setAutoCommit(false);
		verify(stm, atLeast(10)).execute(anyString());
		verify(connection, atLeastOnce()).commit();
	}

	@Test
	public void testSetVersion() throws SQLException {
		TransactionalDatabase db = new TransactionalDatabase(connection);
		db.setVersion(2);
		verify(stm).executeUpdate(anyString());
	}
	
}
