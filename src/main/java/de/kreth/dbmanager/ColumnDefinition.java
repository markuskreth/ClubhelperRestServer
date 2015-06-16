package de.kreth.dbmanager;

public class ColumnDefinition {

	private DataType type;
	private String columnName;
	private String columnParameters;

	public ColumnDefinition(DataType type, String columnName) {
		super();
		this.type = type;
		this.columnName = columnName;
	}

	public ColumnDefinition(DataType type, String columnName, String columnParameters) {
		super();
		this.type = type;
		this.columnName = columnName;
		this.columnParameters = columnParameters;
	}
	
	public DataType getType() {
		return type;
	}
	
	public String getColumnName() {
		return columnName;
	}
	
	public String getColumnParameters() {
		return columnParameters;
	}
}
