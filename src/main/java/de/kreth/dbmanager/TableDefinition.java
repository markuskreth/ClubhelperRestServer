package de.kreth.dbmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Immutable 
 * <br /> Der Liste von Spalten wird <i>immer</i> eine Spalte mit Namen {@link #COLUMN_ID_NAME} hinzugefügt, die als Primärschlüssel dient.
 * @author markus
 *
 */
public class TableDefinition {
	public static final String COLUMN_ID_NAME = "_id";
	private String tableName;
	private Collection<ColumnDefinition> columns;
	
	public TableDefinition(String tableName,Collection<ColumnDefinition> columns) {
		super();
		this.tableName = tableName;
		ColumnDefinition id = new ColumnDefinition(DataType.INTEGER, COLUMN_ID_NAME, "primary key AUTO_INCREMENT");
		List<ColumnDefinition> def = new ArrayList<ColumnDefinition>();
		def.add(id);
		def.addAll(columns);
		this.columns = Collections.unmodifiableCollection(def);
	}

	public String getTableName() {
		return tableName;
	}

	public Collection<ColumnDefinition> getColumns() {
		return columns;
	}

}
