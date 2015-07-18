package de.kreth.clubhelperbackend.config;

public interface SqlForDialect {
	long queryForIdentity();
	boolean tableExists(String tableName);
}
