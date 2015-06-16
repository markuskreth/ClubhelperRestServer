package de.kreth.clubhelperbackend;

public interface SqlForDialect {
	long queryForIdentity();
	boolean tableExists(String tableName);
}
