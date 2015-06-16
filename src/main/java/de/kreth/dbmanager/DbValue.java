package de.kreth.dbmanager;

public class DbValue {
	private DataType type;
	private String columnName;
	private Object value;
	
	public DbValue(DataType type, String columnName) {
		super();
		this.type = type;
		this.columnName = columnName;
	}
	public DataType getType() {
		return type;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getColumnName() {
		return columnName;
	}
	
}
