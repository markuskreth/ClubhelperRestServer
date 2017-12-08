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
	private GoogleSpreadsheetsAdapter service;
	
	private SheetService() {
		createService();
	}

	private void createService() {
		if(service != null) {
			return;
		}
		if(log.isInfoEnabled()) {
			log.info(GoogleSpreadsheetsAdapter.class.getName() + " not initiated, creating...");
		}
		try {
			service = new GoogleSpreadsheetsAdapter();
			sheets = new ArrayList<>();
		} catch (IOException | GeneralSecurityException e) {
			log.error("unable to init " + getClass().getName() + ", Service won't work.", e);
		}
	}
	
	public static JumpHeightSheet get(String title) throws IOException {
		if(INSTANCE.log.isDebugEnabled()) {
			INSTANCE.log.debug("Getting " + Sheet.class.getName() + " for " + title);
		}
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
			if(INSTANCE.log.isTraceEnabled()) {
				INSTANCE.log.trace("found Sheet: " + s.getProperties().getTitle());
			}
			if(s.getProperties().getTitle().equals(title)) {
				if(INSTANCE.log.isTraceEnabled()) {
					INSTANCE.log.trace("returning Sheet: " + s);
				}
				return s;
			}
		}
		throw new IOException("Sheet with title \"" + title + "\" not found.");
	}

	private static List<Sheet> getAllSheets() throws IOException {
		INSTANCE.createService();
		if(sheets != null && sheets.isEmpty() == false){
			return sheets;
		}
		sheets = INSTANCE.service.getSheets();
		return sheets;
	}

	public static JumpHeightSheet create(String title) throws IOException {
		INSTANCE.createService();
		try {
			Sheet dublicateTo = INSTANCE.service.dublicateTo("Vorlage", title);
			sheets.add(dublicateTo);
			return new JumpHeightSheet(dublicateTo);
		} catch (Exception ex) {
			throw new IOException(ex);
		}
	}

	public static void delete(JumpHeightSheet test) throws IOException {
		INSTANCE.createService();
		sheets.remove(test.sheet);
		INSTANCE.service.delete(test.sheet);
	}

	public static ExtendedValue set(String sheetTitle, int column, int row, double value) throws IOException {
		INSTANCE.createService();
		ValueRange content = new ValueRange();
		content = content.setValues(Arrays.asList(Arrays.asList(value)));
		INSTANCE.service.setValue(sheetTitle, column, row, content );
		ExtendedValue res = new ExtendedValue();
		res.setNumberValue(value);
		return res;
	}
	
	public static ExtendedValue set(String sheetTitle, int column, int row, String value) throws IOException {
		INSTANCE.createService();
		ValueRange content = new ValueRange();
		content = content.setValues(Arrays.asList(Arrays.asList(value)));
		INSTANCE.service.setValue(sheetTitle, column, row, content );
		ExtendedValue res = new ExtendedValue();
		res.setStringValue(value);
		return res;
	}

	public static JumpHeightSheet changeTitle(Sheet sheet, String name) throws IOException {
		INSTANCE.createService();
		INSTANCE.service.setSheetTitle(sheet, name);
		sheets = null;
		return get(name);
	}

	public static ValueRange getRange(String sheetTitle, String range) throws IOException {
		INSTANCE.createService();
		return INSTANCE.service.getValues(sheetTitle, range);
	}

}
