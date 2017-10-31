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

import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.ValueRange;

public class JumpHeightSheet {

	DateFormat defaultDf = new SimpleDateFormat("dd.MM.yyyy");
	DateFormat invalidDf = new SimpleDateFormat("dd.MM.yy");
	
	public static final JumpHeightSheet INVALID = new InvalidSheet();
	
	private static final int rowIndexDate = 2;
	private static final int taskIndexIncrementor = 4;
	
	private Logger log = LoggerFactory.getLogger(getClass());
	Sheet sheet;

	private JumpHeightSheet() {
		sheet = null;
	}
	
	public JumpHeightSheet(Sheet sheet) throws SheetDataException {
		this.sheet = sheet;
	}
	
	public String getTitle() {
		return sheet.getProperties().getTitle();
	}

	public List<Date> getDates() throws IOException {
		List<Date> dates = new ArrayList<>();
		ValueRange values = SheetService.getRange(getTitle(), "3:3");
		for (List<Object> l: values.getValues()) {
			for (Object o : l) {
				String text = o.toString();
				if(text != null && "Datum".equals(text) == false) {
					try {
						dates.add(defaultDf.parse(text));
					} catch (ParseException e) {
						log.warn("Not a date: " + text, e);
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
	
	public List<String> getTasks() throws IOException {
		List<String> tasks = new ArrayList<>();

		ValueRange values = SheetService.getRange(getTitle(), "A:A");
		for (List<Object> l: values.getValues()) {
			for (Object o : l) {
				String task = o.toString();
				if("Datum".equals(task) == false && task.trim().isEmpty() == false) {
					tasks.add(task);
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
		
		@Override
		public int hashCode() {
			return getClass().getName().hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			return this == obj;
		}
	}


}
