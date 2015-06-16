package de.kreth.dbmanager;

import java.util.Collections;
import java.util.List;

public class Version {

	private int versionNr;
	private List<String> sqlStms;
	
	public Version(int versionNr, List<String> sqlStms) {
		super();
		this.versionNr = versionNr;
		this.sqlStms = Collections.unmodifiableList(sqlStms);
	}

	public int getVersionNr() {
		return versionNr;
	}
	
	public List<String> getSqlStms() {
		return sqlStms;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": nr=" + versionNr + ", Anz.Sql-Statements=" + sqlStms.size();
	}
}
