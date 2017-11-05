package de.kreth.clubhelperbackend.spreadsheetdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CellRange {

	private List<List<String>> values;
	
	private CellRange(Builder builder) {
		this.values = Collections.unmodifiableList(builder.values);
	}

	public List<List<String>> getValues() {
		return values;
	}
	
	public String getValue(int column, int row) {
		return values.get(row).get(column);
	}
	
	public static class Builder {

		private List<List<String>> values = new ArrayList<>();
		
		public Builder add(int columnIndex, int rowIndex, String value) {
			List<String> row;
			if(rowIndex >= values.size()) {
				row = new ArrayList<>();
				values.add(rowIndex, row);
			} else {
				row = values.get(rowIndex);
			}
			row.add(columnIndex, value);
			return this;
		}
		
		public CellRange build() {
			return new CellRange(this);
		}
	}
}
