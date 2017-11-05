package de.kreth.clubhelperbackend.spreadsheet;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.ValueRange;

public enum SheetService {

	INSTANCE;

	private static List<Sheet> sheets;
	Logger log = LoggerFactory.getLogger(getClass());
	private final GoogleSpreadsheetsAdapter service;
	
	private SheetService() {
		GoogleSpreadsheetsAdapter s = null;
		try {
			s = new GoogleSpreadsheetsAdapter();
		} catch (IOException | GeneralSecurityException e) {
			log.error("unable to init " + getClass().getName() + ", Service won't work.", e);
		}
		service = s;
	}
	
	public static JumpHeightSheet get(String title) throws IOException {
		Sheet result = getForName(title);
		try {
			return new JumpHeightSheet(result);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public static List<JumpHeightSheet> getSheets() throws IOException {
		List<JumpHeightSheet> result = new ArrayList<>();
		for (Sheet s: getAllSheets()) {
			try {
				result.add(new JumpHeightSheet(s));
			} catch (SheetDataException e) {
				INSTANCE.log.error("unable to add sheet: " + s, e);
			}
		}
		return result;
	}
	
	private static Sheet getForName(String title) throws IOException {
		List<Sheet> all = getAllSheets();

		for (Sheet s: all) {
			INSTANCE.log.trace("found Sheet: " + s.getProperties().getTitle());
			if(s.getProperties().getTitle().equals(title)) {
				INSTANCE.log.trace("returning Sheet: " + s);
				return s;
			}
		}
		throw new IOException("Sheet with title \"" + title + "\" not found.");
	}

	private static List<Sheet> getAllSheets() throws IOException {
		if(sheets != null && sheets.isEmpty() == false){
			return sheets;
		}
		sheets = INSTANCE.service.getSheets();
		return sheets;
	}

	public static JumpHeightSheet create(String title) throws IOException {
		try {
			Sheet dublicateTo = INSTANCE.service.dublicateTo("Vorlage", title);
			sheets.add(dublicateTo);
			return new JumpHeightSheet(dublicateTo);
		} catch (Exception ex) {
			throw new IOException(ex);
		}
	}

	public static void delete(JumpHeightSheet test) throws IOException {
		sheets.remove(test.sheet);
		INSTANCE.service.delete(test.sheet);
	}

	public static ExtendedValue set(String sheetTitle, int column, int row, double value) throws IOException {
		ValueRange content = new ValueRange();
		content = content.setValues(Arrays.asList(Arrays.asList(value)));
		INSTANCE.service.setValue(sheetTitle, column, row, content );
		ExtendedValue res = new ExtendedValue();
		res.setNumberValue(value);
		return res;
	}
	
	public static ExtendedValue set(String sheetTitle, int column, int row, String value) throws IOException {
		ValueRange content = new ValueRange();
		content = content.setValues(Arrays.asList(Arrays.asList(value)));
		INSTANCE.service.setValue(sheetTitle, column, row, content );
		ExtendedValue res = new ExtendedValue();
		res.setStringValue(value);
		return res;
	}

	public static JumpHeightSheet changeTitle(Sheet sheet, String name) throws IOException {
		INSTANCE.service.setSheetTitle(sheet, name);
		sheets = null;
		return get(name);
	}

	public static ValueRange getRange(String sheetTitle, String range) throws IOException {
		return INSTANCE.service.getValues(sheetTitle, range);
	}
}
