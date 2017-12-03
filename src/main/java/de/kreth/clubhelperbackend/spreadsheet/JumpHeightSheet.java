package de.kreth.clubhelperbackend.spreadsheet;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.ValueRange;

public class JumpHeightSheet {

	public static final DateFormat defaultDf = new SimpleDateFormat("dd.MM.yyyy");
	DateFormat invalidDf = new SimpleDateFormat("dd.MM.yy");
	
	private static final int rowIndexDate = 2;
	private static final int taskIndexIncrementor = 4;
	
	private Logger log = LoggerFactory.getLogger(getClass());
	Sheet sheet;
	private ArrayList<String> tasks;
	private List<CellValue<Date>> dates;
	
	public JumpHeightSheet(Sheet sheet) throws SheetDataException {
		assert(sheet != null);
		this.sheet = sheet;
	}
	
	public String getTitle() {
		return sheet.getProperties().getTitle();
	}

	public List<CellValue<Date>> getDates() throws IOException {
		if(dates == null) {
			dates = new ArrayList<>();
			ValueRange values = SheetService.getRange(getTitle(), "3:3");
			int column = 0;
			for (List<Object> l: values.getValues()) {
				for (Object o : l) {
					String text = o.toString();
					if(text != null && "Datum".equals(text) == false) {
						column++;
						try {
							dates.add(new CellValue<Date>(defaultDf.parse(text), column, 3));
						} catch (ParseException e) {
							log.warn("Not a date: " + text, e);
						}
					}
				}
			}

		}
		return dates;
	}

	public CellRange getValues(String name) throws IOException {
		CellRange.Builder builder = new CellRange.Builder();
		List<CellValue<Date>> dates = getDates();

		int columnIndex = 0;
		for (CellValue<Date> date : dates) {
			builder.add(columnIndex++, 0, defaultDf.format(date.getObject()));
		}
		int row = getIndexOf(name);
		ValueRange values = SheetService.getRange(getTitle(), new StringBuilder().append(row).append(':').append(row).toString());
		MutableInt count = new MutableInt(0);

		values.getValues().get(0).stream().forEach(o -> {
			if(count.intValue()>0) {
				builder.add(count.intValue() - 1, 1, o.toString());
			}
			count.increment();
		});
		return builder.build();
	}
	
	public CellValue<Double> add(String taskName, Calendar date, double value) throws IOException {
		
		int column = getIndexOf(date);
		int row = getIndexOf(taskName);
		
		ExtendedValue res = SheetService.set(getTitle(), column , row, value);
		
		return new CellValue<Double>(res.getNumberValue().doubleValue(), column, row);
	}

	public int getIndexOf(String taskName) throws IOException {
		int row;
		List<String> tasks = getTasks();
		row = tasks.indexOf(taskName);
		if(row<0) {
			row = tasks.size() + taskIndexIncrementor;
			SheetService.set(getTitle(), 1, row, taskName);
			this.tasks.add(taskName);
		} else {
			row += taskIndexIncrementor;
		}
		return row;
	}

	private int getIndexOf(Calendar date) throws IOException {
		int column = -1;
		List<CellValue<Date>> dates = getDates();
		for(CellValue<Date> d: dates) {
			if(d.getObject().equals(date.getTime())) {
				column = d.getColumn();
				break;
			}
		}
		
		if(column<0) {
			column = dates.size() + 2;
			SheetService.set(getTitle(), column, rowIndexDate + 1, defaultDf.format(date.getTime()));
			this.dates.add(new CellValue<Date>(date.getTime(), column, rowIndexDate + 1));
		}
		return column;
	}
	
	public List<String> getTasks() throws IOException {
		if(tasks == null) {
			tasks = new ArrayList<>();
	
			ValueRange values = SheetService.getRange(getTitle(), "A:A");
			for (List<Object> l: values.getValues()) {
				for (Object o : l) {
					String task = o.toString();
					if("Datum".equals(task) == false && task.trim().isEmpty() == false) {
						tasks.add(task);
					}
				}
			}
		}
		return tasks;
	}

	public List<String> addTask(String taskName) throws IOException {
		
		int row = getTasks().size() + taskIndexIncrementor;
		SheetService.set(getTitle(), 1, row, taskName);
		tasks.add(taskName);
		return getTasks();
	}

	private void update(JumpHeightSheet next) {
		this.sheet = next.sheet;
		tasks = null;
		dates = null;
	}

	public void setTitle(String name) throws IOException {
		JumpHeightSheet result = SheetService.changeTitle(sheet, name);
		update(result);
	}

}
