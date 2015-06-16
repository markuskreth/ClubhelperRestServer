package de.kreth.clubhelperbackend.aspects;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import de.kreth.clubhelperbackend.DatabaseConfiguration;
import de.kreth.dbmanager.Database;
import de.kreth.dbmanager.DbValue;

public class MysqlDbCheckAspect implements Database {

	private Connection con;
	private boolean isChecked = false;

	public MysqlDbCheckAspect(DataSource source) {
		try {
			con = source.getConnection();
		} catch (SQLException e) {
			throw new InvalidDataAccessApiUsageException("Keine Connection aus DataSource erhalten", e);
		}
	}
	
	public synchronized void checkDb() {
		if(isChecked)
			return;
		isChecked = true;
		try {
			if(getVersion() == 0) {
				beginTransaction();
				DatabaseConfiguration manager = new DatabaseConfiguration();
				manager.executeOn(this);
				setTransactionSuccessful();
			}
		} catch (SQLException e) {
			try {
				endTransaction();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new DataAccessResourceFailureException("Konnte Datenbanktabellen nicht erstellen1", e);
		}
	}
	
	@Override
	public void beginTransaction() throws SQLException {
		con.setAutoCommit(false);
	}

	@Override
	public void setTransactionSuccessful() throws SQLException {
		if(!con.getAutoCommit())
			con.commit();
		con.setAutoCommit(true);
	}

	@Override
	public void endTransaction() throws SQLException {
		if(!con.getAutoCommit())
			con.rollback();
		con.setAutoCommit(true);
	}

	@Override
	public void execSQL(String sql) throws SQLException {
		con.createStatement().execute(sql);
	}

	@Override
	public void execSQL(String sql, Object[] bindArgs) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getVersion() {
		int version = 0;
		Statement stm = null;
		ResultSet rs = null;
		try {
	    	stm = con.createStatement();
	    	rs = stm.executeQuery("SELECT version FROM version");
	    	if(rs.next()) 
	    		version = rs.getInt("version");
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(stm != null)
				try {
					stm.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
		return version;
	}

	@Override
	public long insert(String table, List<DbValue> values)
			throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean needUpgrade(int newVersion) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<Collection<DbValue>> query(String table,
			String[] columns) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setVersion(int version) {
		Statement stm = null;
		boolean autoCommit = true;
    	try {
    		autoCommit = con.getAutoCommit();
    		con.setAutoCommit(false);
    		stm = con.createStatement();
			int rows = stm.executeUpdate("UPDATE version SET version = " + version);
			if(rows==1)
				con.commit();
			else
				con.rollback();
		} catch (SQLException e) {

			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				con.setAutoCommit(autoCommit);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public int delete(String table, String whereClause, String[] whereArgs)
			throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
