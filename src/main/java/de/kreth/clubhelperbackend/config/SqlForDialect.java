package de.kreth.clubhelperbackend.config;

public interface SqlForDialect {

	String queryForIdentity(String tableName);

	boolean tableExists(String tableName);

	// String alterTableRenameColumn(String tableName, String columnOldName,
	// String columnNewName);
	//
	// String escapeSqlNames(String name);
}
