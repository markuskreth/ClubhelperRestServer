package de.kreth.dbmanager;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbManager {
	private Map<String, TableDefinition> tableDefinitions;
	private Database db;
	private int newVersion;
	private List<Version> versions;
	
	public DbManager(Collection<TableDefinition> tables, List<Version> versions, Database db, int newVersion) {
		tableDefinitions = new HashMap<String, TableDefinition>();
		this.db = db;
		this.newVersion = newVersion;
		this.versions = versions;
		
		for(TableDefinition def: tables){
			tableDefinitions.put(def.getTableName(), def);
		}

		checkParamters();
		sortVersionsAndMakeUnmodifiable();
	}
	
	private void checkParamters() {
		StringBuilder errorMsg = new StringBuilder();
		if(tableDefinitions.isEmpty())
			append(errorMsg, "Parameter tables darf nicht leer sein!");
		if(db == null)
			append(errorMsg, "Parameter db darf nicht null sein!");
		if(newVersion<1)
			append(errorMsg, "Parameter newVersion muss >= 1 sein!");
		if(versions == null)
			append(errorMsg, "Liste mit Versionen darf nicht null sein");
		if(versions != null && versions.get(0).getVersionNr() != 1)
			append(errorMsg, "Der erste Eintrag muss die Versionsnummer 1 haben! Die Create Table anweisungen für erste Version müssen enthalten sein.");
		
		if(errorMsg.length()>0)
			throw new IllegalArgumentException(errorMsg.toString());
	}

	private void sortVersionsAndMakeUnmodifiable() {

		Collections.sort(versions, new Comparator<Version>() {

			@Override
			public int compare(Version o1, Version o2) {
				if(o1.getVersionNr() == o2.getVersionNr())
					throw new IllegalStateException("Es gibt zwei Einträge mit der selben Version! Es muss eine eindeutige Reihenfolge gegeben sein!");
				
				return o1.getVersionNr() - o2.getVersionNr();
			}
		});
		
		versions = Collections.unmodifiableList(versions);
		
	}

	private void append(StringBuilder bld, String msg){
		if(bld.length()>0)
			bld.append("\n");
		bld.append(msg);
	}
	
	public Map<String, TableDefinition> getTableDefinitions() {
		return Collections.unmodifiableMap(tableDefinitions);
	}

	public boolean needUpdate() {
		return db.getVersion()<newVersion;
	}

	/**
	 * Führt die nötigen Befehle aus um die aktuelle Version zu erreichen. <br />
	 * @throws SQLException
	 */
	public void execute() throws SQLException {
		int currentVersion = db.getVersion();
		int from = 0;
		
		for (int i = from; i < versions.size(); i++) {
			Version v = versions.get(i);
			
			if(v.getVersionNr()>currentVersion)
				executeStatements(v.getSqlStms());
		}
	}

	private void executeStatements(List<String> sqlStms) throws SQLException {
		for(String sql : sqlStms){
			db.execSQL(sql);
		}
	}


	public static String createSqlStatement(TableDefinition def) {
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE ").append(def.getTableName()).append(" (\n");
		
		boolean first = true;
		for(ColumnDefinition col : def.getColumns()) {
			if(!first) {
				sql.append(",\n");
			}
			
			first = false;
			
			sql.append("\t").append(col.getColumnName()).append(" ");
			switch (col.getType()) {
			case BLOB:
				throw new IllegalArgumentException("Column Type " + col.getType() + " not supported");
			case BOOLEAN:
				sql.append(col.getType().name());
				break;
			case DATETIME:
				sql.append("DATETIME");
				break;
			case INTEGER:
				sql.append(col.getType().name());
				break;
			case REAL:
				sql.append("DOUBLE");
				break;
			case VARCHAR100:
				sql.append("VARCHAR(100)");
				break;
			case VARCHAR25:
				sql.append("VARCHAR(25)");
				break;
			case TEXT:
			case VARCHAR255:
				sql.append("VARCHAR(255)");
				break;			
			}
			if(col.getColumnParameters() != null && !col.getColumnParameters().isEmpty()){
				sql.append(" ").append(col.getColumnParameters());
			}
			sql.append("\n");
		}
		sql.append(")");
		return sql.toString();
	}
}
