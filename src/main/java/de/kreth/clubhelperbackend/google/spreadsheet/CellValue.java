package de.kreth.clubhelperbackend.google.spreadsheet;

import java.text.DateFormat;
import java.util.Date;

public class CellValue<T> {
	
	private T innerObject;
	private int column;
	private int row;
	
	public CellValue(T object, int columnIndex, int rowIndex) {
		super();
		assert object != null:"Value Object must not be null!";

		this.object = object;
		this.column = column;
		this.row = row;
	}

	public final T getObject() {
		return innerObject;
	}

	public final int getColumn() {
		return column;
	}

	public final int getRow() {
		return row;
	}

	@Override
	public final String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("CellValue ");
		stringBuilder.append(GoogleSpreadsheetsAdapter.intToColumn(column));
		stringBuilder.append(row);
		stringBuilder.append("=");
		stringBuilder.append((object instanceof Date?DateFormat.getDateTimeInstance().format(object):object.toString()));

		return stringBuilder.toString();
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + column;
		result = prime * result + object.hashCode();
		result = prime * result + row;
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("unchecked")
		CellValue<T> other = (CellValue<T>) obj;
		if (column != other.column) {
			return false;
		if (row != other.row)
			return false;
		if (object == null) {
			if (other.object != null)
				return false;
		} 
		return object.equals(other.object);
	}
	
}
