package de.kreth.clubhelperbackend.spreadsheetdata;

import java.text.DateFormat;
import java.util.Date;

public class CellValue<T> {
	
	private T object;
	private int column;
	private int row;
	
	public CellValue(T object, int column, int row) {
		super();
		assert (object != null);
		this.object = object;
		this.column = column;
		this.row = row;
	}

	public T getObject() {
		return object;
	}

	public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}

	@Override
	public String toString() {
		return "CellValue [object=" + (object instanceof Date?DateFormat.getDateTimeInstance().format(object):object) + ", column=" + column + ", row=" + row + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + column;
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		result = prime * result + row;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		CellValue<T> other = (CellValue<T>) obj;
		if (column != other.column)
			return false;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		if (row != other.row)
			return false;
		return true;
	}
	
}
