package de.kreth.clubhelperbackend.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;

import de.kreth.clubhelperbackend.aspects.DbCheckAspect;
import de.kreth.clubhelperbackend.dao.DeletedEntriesDao;
import de.kreth.clubhelperbackend.dao.PersonDao;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.dao.abstr.AbstractDao.ClubhelperRowMapper;
import de.kreth.clubhelperbackend.pojo.Data;
import de.kreth.clubhelperbackend.pojo.DeletedEntries;
import de.kreth.clubhelperbackend.pojo.Group;
import de.kreth.clubhelperbackend.pojo.PersonGroup;
import de.kreth.clubhelperbackend.pojo.Relative;
import de.kreth.clubhelperbackend.testutils.MockedLogger;
import de.kreth.clubhelperbackend.testutils.ResultSetStructure;
import de.kreth.clubhelperbackend.testutils.TestData;
import de.kreth.clubhelperbackend.testutils.TestDataPerson;
import de.kreth.clubhelperbackend.utils.TimeProvider;
import de.kreth.dbmanager.DatabaseType;
import de.kreth.dbmanager.TableDefinition;

@RunWith(Parameterized.class)
public class DatabaseConfigurationTest<T extends Data> {

	private static DatabaseConfiguration config = new DatabaseConfiguration(0, DatabaseType.HSQLDB);
	private static Logger logger;
	private static JDBCDataSource dataSource;
	private static DbCheckAspect dbCheck;
	private static SqlForHsqlDb sqlDialect;

	@Mock
	protected PlatformTransactionManager transMan;
	@Mock
	private DeletedEntriesDao deletedEntriesDao;
	@Mock
	protected TimeProvider timeProvider;

	protected JdbcTemplate jdbcTemplate;
	
	@BeforeClass
	public static void initDatabase() {
		logger = MockedLogger.mock();
		dataSource = new JDBCDataSource();
		dataSource.setUrl("jdbc:hsqldb:mem:testdb");
		dataSource.setUser("sa");
		dbCheck = new DbCheckAspect(dataSource, DatabaseType.HSQLDB, logger);
		dbCheck.checkDb();
		sqlDialect = new SqlForHsqlDb(dataSource);
	}

	@Parameter
	public TestObjectMapping<T> mapping;
	private AbstractDao<T> dao;
	private TableDefinition tableDef;
	private T data;
	private Connection connection;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		jdbcTemplate = new JdbcTemplate(dataSource);
		dao = mapping.dao;
		dao.setDataSource(dataSource);
		dao.setJdbcTemplate(jdbcTemplate);
		dao.setPlatformTransactionManager(transMan);
		dao.setSqlDialect(sqlDialect );
		dao.setDeletedEntriesDao(deletedEntriesDao);
		dao.setTimeProvider(timeProvider);
		
		tableDef = mapping.tableDef;
		data = TestData.getTestObject(mapping.pojo);
		connection = dataSource.getConnection();
		if (mapping.needPerson) {
			PersonDao pDao = new PersonDao();
			pDao.setDataSource(dataSource);
			pDao.setJdbcTemplate(jdbcTemplate);
			pDao.setPlatformTransactionManager(transMan);
			pDao.setSqlDialect(sqlDialect);
			pDao.insert(TestDataPerson.getPerson());
		}
	}

	@After
	public void closeConnection() throws SQLException {
		if (connection.isClosed() == false) {
			connection.createStatement().execute("TRUNCATE TABLE " + tableDef.getTableName());
			if (mapping.needPerson) {
				connection.createStatement().execute("TRUNCATE TABLE " + config.getPerson().getTableName());
			}
			connection.close();
		}
	}
	
	@Test
	public void testInsert() throws SQLException {
		T inserted = dao.insert(data);
		assertNotNull(inserted);
		ResultSet rs = getTableContent();
		assertTrue(rs.next());
		assertFalse(rs.next());
	}
	
	static boolean executed = false;
	@Test
	public void testStatement() throws SQLException {
		if (executed) {
			return;
		}
		executed = true;
		Statement stm = connection.createStatement();
		stm.execute("INSERT INTO person (prename, surname, birth, changed, created) "
				+ "VALUES ('Tala', 'Brüggemann', '2007-06-20 00:00:00', '2018-06-25 17:04:38', '2018-06-25 17:04:38')");
		String sql = sqlDialect.queryForIdentity("person");
		ResultSet rs = stm.executeQuery(sql);
		System.out.println(new ResultSetStructure(rs).toString());
		System.out.println();
		System.out.println(new ResultSetStructure(stm.executeQuery("select * from person")).toString());
		
		stm.close();
	}
	
	@Test
	@Ignore
	public void testUpdate() {
		T inserted = dao.insert(data);
		TestData.change(inserted);
		assertTrue("Update failed for " + inserted, dao.update(inserted));
	}
	
	@Test
	public void testDelete() throws SQLException {
		Long id = 11L;
		data.setId(id );
		T inserted = dao.insert(data);
		when(deletedEntriesDao.insert(ArgumentMatchers.any(DeletedEntries.class)))
			.thenReturn(new DeletedEntries(15L, tableDef.getTableName(), data.getId()));
		
		assertTrue("Delete failed for " + inserted, dao.delete(inserted));

		if (DeletedEntries.class.equals(mapping.pojo)) {
			assertFalse(getTableContent().next());
		} else {
			ArgumentCaptor<DeletedEntries> delCap = ArgumentCaptor.forClass(DeletedEntries.class);
			verify(deletedEntriesDao).insert(delCap.capture());
			
			DeletedEntries del = delCap.getValue();
			assertEquals(id, del.getEntryId());
			assertEquals(tableDef.getTableName(), del.getTablename());
		}
	}
	
	@Test
	public void testLoadAll() {
		List<T> entities = dao.getAll();
		assertTrue(entities.isEmpty());
		T inserted = dao.insert(data);
		TestData.change(inserted);
		inserted.setId(null);
		dao.insert(inserted);
		entities = dao.getAll();
		assertEquals(2, entities.size());
	}
	
	public void printTableContent() throws SQLException {
		System.out.println(new ResultSetStructure(getTableContent()));
	}
	
	private ResultSet getTableContent() throws SQLException {
		String sql = "select * from " + tableDef.getTableName();
		return connection.createStatement().executeQuery(sql);
	}

	@SuppressWarnings("unchecked")
	@Parameters(name="{index}: {0}")
	public static List<TestObjectMapping<? extends Data>> getTestClasses() throws SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, ReflectiveOperationException {
		
		List<Field> fields = getAllPojoDefinitions();
		
		Reflections refl = new Reflections(new ConfigurationBuilder()
			     .setUrls(ClasspathHelper.forPackage("de.kreth.clubhelperbackend.dao"))
			     .setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner())
			     );
		Set<Class<?>> daos = refl.getTypesAnnotatedWith(Repository.class);
		
		List<TestObjectMapping<? extends Data>> testDaos = new ArrayList<>();
		
		for (Class<?> daoClass: daos) {
			if (Modifier.isAbstract(daoClass.getModifiers())) {
				System.err.println("ERROR: Skipped -> " + daoClass);
				continue;
			}

			AbstractDao<? extends Data> instance = (AbstractDao<? extends Data>) daoClass.getConstructor().newInstance();
			if (instance.forDataType().equals(PersonGroup.class)) {
				continue;
			}
			
			Field mapperField = daoClass.getSuperclass().getDeclaredField("mapper");
			mapperField.setAccessible(true);
			ClubhelperRowMapper<? extends Data> mapper =  (ClubhelperRowMapper<? extends Data>) mapperField.get(instance);
			Field itemClassField;
			try {

				itemClassField = mapper.getClass().getDeclaredField("itemClass");
			} catch (Exception e) {
				try {

					itemClassField = mapper.getClass().getSuperclass().getDeclaredField("itemClass");
				} catch (Exception ex) {
					System.out.println("Unable to get itemClass from " + mapper);
					continue;
				}
			}
			itemClassField.setAccessible(true);
			Class<?> classObj = (Class<?>) itemClassField.get(mapper);

			for (Field f: fields) {

				Pojo pojoType = f.getDeclaredAnnotation(Pojo.class);
				Class<? extends Data> pojoClass = pojoType.pojoClass();
				if (pojoClass.isAssignableFrom(classObj)) {
					@SuppressWarnings("rawtypes")
					TestObjectMapping testObj = new TestObjectMapping();
					testObj.pojo = pojoClass;
					testObj.dao = instance;
					f.setAccessible(true);
					testObj.tableDef = (TableDefinition) f.get(config);
					if (pojoClass.equals(Relative.class) ) {
						testObj.needPerson = true;
					} else  {
						for (Method m: pojoClass.getMethods()) {
							if (m.getName().equalsIgnoreCase("getPersonId") && m.getReturnType().equals(long.class)) {
								testObj.needPerson = true;
								break;
							}
						}
					}
					testDaos.add(testObj);
				}
			}
			
		}

		return testDaos;
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		initDatabase();
		List<TestObjectMapping<? extends Data>> tests = getTestClasses();
		TestObjectMapping<Group> map = (TestObjectMapping<Group>) tests.stream().filter(e -> e.pojo.equals(Group.class)).findFirst().get();
		DatabaseConfigurationTest<Group> t = new DatabaseConfigurationTest<Group>();
		t.mapping = map;
		t.setUp();
		t.testLoadAll();
		
		t.printTableContent();
	}
	
	static List<Field> getAllPojoDefinitions() {
		return Arrays.asList(DatabaseConfiguration.class.getDeclaredFields())
				.stream()
				.filter(f -> {
					return f.getDeclaredAnnotation(Pojo.class) != null;
				})
				.collect(Collectors.toList());
	}

	public static class TestObjectMapping<T extends Data> {
		Class<T> pojo;
		AbstractDao<T> dao;
		TableDefinition tableDef;
		boolean needPerson;
		
		@Override
		public String toString() {
			return pojo.getName();
		}
	}
}
