package de.kreth.clubhelperbackend.dao;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

import de.kreth.clubhelperbackend.SqlForDialect;

public abstract class AbstractDao extends JdbcDaoSupport {

	protected SqlForDialect sqlDialect;

	public AbstractDao() {
		super();
	}

	public SqlForDialect getSqlDialect() {
		return sqlDialect;
	}

	public void setSqlDialect(SqlForDialect sqlDialect) {
		this.sqlDialect = sqlDialect;
	}

}