package de.kreth.clubhelperbackend.spreadsheet;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.GridData;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.Sheet;

public class JumpHeightSheet {

	DateFormat defaultDf = new SimpleDateFormat("dd.MM.yyyy");
	DateFormat invalidDf = new SimpleDateFormat("dd.MM.yy");
	
	public static final JumpHeightSheet INVALID = new InvalidSheet();
	
	private static final int rowIndexDate = 2;
	private static final int taskIndexIncrementor = 4;
	
	private Logger log = LoggerFactory.getLogger(getClass());
	Sheet sheet;
	private List<GridData> data;

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
			RowData row = data.getRowData().get(rowIndexDate);
			List<CellData> values = row.getValues();
			values.remove(0);
			for (CellData cell : values) {
				ExtendedValue value = cell.getEffectiveValue();
				if(value != null) {
					
					final String text = value.getStringValue();
					if(text != null) {
						try {
							dates.add(defaultDf.parse(text));
						} catch (ParseException e) {
							try {
								Date d = invalidDf.parse(text);
								value.setStringValue(defaultDf.format(d));
								dates.add(d);
							} catch (ParseException e1) {
								log.warn("Not a date: " + text, e1);
							}
						}
					}
				}
			}
		}

		return dates;
	}

	public CellValue add(String taskName, Calendar date, double value) throws IOException {
		
		int column = getIndexOf(date);
		int row = getIndexOf(taskName);
		
		ExtendedValue res = SheetService.set(getTitle(), column , row, value);
		CellValue cellValue = new CellValue(res);
		return cellValue;
	}

	private int getIndexOf(String taskName) throws IOException {
		int row;
		List<String> tasks = getTasks();
		row = tasks.indexOf(taskName);
		if(row<0) {
			row = tasks.size() + taskIndexIncrementor;
			SheetService.set(getTitle(), 1, row, taskName);
		} else {
			row += taskIndexIncrementor;
		}
		return row;
	}

	private int getIndexOf(Calendar date) throws IOException {
		int column;
		List<Date> dates = getDates();
		column = dates.indexOf(date.getTime());
		
		if(column<0) {
			column = dates.size() + 2;
			SheetService.set(getTitle(), column, rowIndexDate + 1, defaultDf.format(date.getTime()));
		} else {
			column += 2;
		}
		return column;
	}
	
	public List<String> getTasks() {
		List<String> tasks = new ArrayList<>();

		for (GridData d: data) {
			for (RowData row : d.getRowData()) {
				if(row.getValues() != null && row.getValues().size()>0) {
					CellData cell = row.getValues().get(0);
					ExtendedValue effectiveValue = cell.getEffectiveValue();
					
					if(effectiveValue != null) {
						String task = effectiveValue.getStringValue();
						if("Datum".equals(task) == false && task.trim().isEmpty() == false) {
							tasks.add(task);
						}
					}
					
				}
			}
		}
		return tasks;
	}

	public List<String> addTask(String taskName) throws IOException {
		
		int row = getTasks().size() + taskIndexIncrementor;
		SheetService.set(getTitle(), 1, row, taskName);
		JumpHeightSheet next = SheetService.get(getTitle());
		update(next);
		return getTasks();
	}

	private void update(JumpHeightSheet next) {
		this.sheet = next.sheet;
		this.data = next.data;
	}

	public void setTitle(String name) throws IOException {
		JumpHeightSheet result = SheetService.changeTitle(sheet, name);
		update(result);
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
