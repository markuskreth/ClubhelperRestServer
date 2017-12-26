package de.kreth.clubhelperbackend;

import javax.sql.DataSource;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.slf4j.LoggerFactory;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import de.kreth.clubhelperbackend.aspects.DbCheckAspect;

public class ManualDatabase {

	public static void main(String[] args) {
		initLogger();

		LoggerFactory.getLogger(ManualDatabase.class).debug("Manual");
		LoggerFactory.getLogger(DbCheckAspect.class).debug("MysqlDbCheckAspect");

		DataSource dataSource = getDatasource(args);
		DbCheckAspect updater = new DbCheckAspect(dataSource);
		updater.checkDb();
	}

	private static void initLogger() {

		BasicConfigurator.resetConfiguration();

		ConsoleAppender console = new ConsoleAppender(); // create appender
		// configure the appender
		String PATTERN = "%d [%p|%c|%C{1}] %m%n";
		console.setLayout(new PatternLayout(PATTERN));
		console.setThreshold(Level.ALL);
		console.activateOptions();

		BasicConfigurator.configure(console);

		FileAppender fa = new FileAppender();
		fa.setName("FileLogger");
		fa.setFile("ManualDatabase.log");
		fa.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
		fa.setThreshold(Level.DEBUG);
		fa.setAppend(true);
		fa.activateOptions();

		BasicConfigurator.configure(fa);
	}

	private static DataSource getDatasource(String[] args) {
		MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
		ds.setServerName("localhost");
		ds.setDatabaseName("kreinakeClone");
		ds.setUser("markus");
		ds.setPassword("0773");
		return ds;
	}

}
