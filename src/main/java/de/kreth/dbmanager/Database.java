package de.kreth.dbmanager;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public interface Database {

  public void beginTransaction() throws SQLException;

  public void setTransactionSuccessful() throws SQLException;

  public void endTransaction() throws SQLException;

  public void execSQL(String sql) throws SQLException;

  public void execSQL(String sql, Object bindArgs[]) throws SQLException;

  public int getVersion();

  public long insert(String table, List<DbValue> values) throws SQLException;

  public boolean needUpgrade(int newVersion);

  public Iterator<Collection<DbValue>> query(String table, String columns[]) throws SQLException;

  public void setVersion(int version);

  int delete(String table, String whereClause, String[] whereArgs) throws SQLException;

}