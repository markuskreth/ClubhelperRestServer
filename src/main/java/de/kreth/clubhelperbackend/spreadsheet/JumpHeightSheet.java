package de.kreth.clubhelperbackend.spreadsheet;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.GridData;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.Sheet;

public class JumpHeightSheet {

	DateFormat defaultDf = new SimpleDateFormat("dd.MM.yyyy");
	DateFormat invalidDf = new SimpleDateFormat("dd.MM.yy");
	
	public static final JumpHeightSheet INVALID = new InvalidSheet();
	
	final Sheet sheet;
	private final List<GridData> data;

	private JumpHeightSheet() {
		sheet = null;
		data = null;
	}
	
	public JumpHeightSheet(Sheet sheet) throws SheetDataException {

		this.data = sheet.getData();
		if(data == null) {
			throw new SheetDataException("Unable to get Data from Sheet.");
		} else {
			this.sheet = sheet;
		}
	}
	
	public String getTitle() {
		return sheet.getProperties().getTitle();
	}

	public List<Date> getDates() {
		List<Date> dates = new ArrayList<>();
		for (GridData data: data) {
			RowData row = data.getRowData().get(2);
			for (CellData cell : row.getValues()) {
				ExtendedValue value = cell.getEffectiveValue();
				if(value != null) {
					
					String text = value.getStringValue();
					if(text != null) {
						try {
							dates.add(defaultDf.parse(text));
						} catch (ParseException e) {
							try {
								Date d = invalidDf.parse(text);
								value.setStringValue(defaultDf.format(d));
								dates.add(d);
							} catch (ParseException e1) {
								
								e1.printStackTrace();
							}
						}
					}
				}
			}
		}
		return dates;
	}
	
	private static class InvalidSheet extends JumpHeightSheet {

		@Override
		public String getTitle() {
			return "INVALID";
		}
		
		@Override
		public List<Date> getDates() {
			return Collections.emptyList();
		}
		@Override
		public String toString() {
			return getTitle();
		}
	}
}
