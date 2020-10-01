package de.kreth.clubhelperbackend.testutils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultSetStructure {

	private final List<String> header = new ArrayList<>();
	private final List<List<String>> values;
	private final ArrayList<Integer> columnSizes = new ArrayList<>();
	
	public ResultSetStructure(ResultSet rs) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		int colCount = metaData.getColumnCount();
		for (int i=1; i<=colCount; i++) {
			String columnLabel = metaData.getColumnLabel(i);
			header.add(columnLabel);
			columnSizes.add(columnLabel.length());
		}
		values = new ArrayList<>();
		while (rs.next()) {
			List<String> values = new ArrayList<>();
			for (int i=1; i<=colCount; i++) {
				String value = rs.getString(i);

				if (value != null) {
					values.add(value);
					if (columnSizes.get(i-1) < value.length()) {
						columnSizes.set(i-1, value.length());
					}
				} else {
					values.add("");
				}
			}
			this.values.add(values);
		}
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		printLine(header, str);
		values.forEach(row -> printLine(row, str));
		return str.toString();
	}

	private void printLine(List<String> values, StringBuilder str) {
		for (int i=0; i<columnSizes.size(); i++) {
			int length = columnSizes.get(i).intValue();
			String value = values.get(i);
			str.append(String.format("|%LENs".replace("LEN", String.valueOf(length)), value));
		}
		str.append('|').append('\n');
	}
}
