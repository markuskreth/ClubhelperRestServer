package de.kreth.clubhelperbackend.config;

public interface SqlForDialect {
	String queryForIdentity(String tableName);

	boolean tableExists(String tableName);
}
