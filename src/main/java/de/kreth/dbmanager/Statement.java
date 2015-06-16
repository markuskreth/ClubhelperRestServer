package de.kreth.dbmanager;
public interface Statement {

  public void close();

  public void execute();

  public long executeInsert();

  public void bindBlob(int index, byte value[]);

  public void bindDouble(int index, double value);

  public void bindLong(int index, long value);

  public void bindNull(int index);

  public void bindString(int index, String value);

}