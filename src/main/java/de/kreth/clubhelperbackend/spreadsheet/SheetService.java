package de.kreth.clubhelperbackend.spreadsheet;

import java.io.IOException;
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

	Logger log = LoggerFactory.getLogger(getClass());
	private final GoogleSpreadsheetsAdapter service;
	
	private SheetService() {
		GoogleSpreadsheetsAdapter s = null;
		try {
			s = new GoogleSpreadsheetsAdapter();
		} catch (IOException e) {
			log.error("unable to init " + getClass().getName() + ", Service won't work.");
		}
		service = s;
	}
	
	public static JumpHeightSheet get(String title) throws IOException {
		Sheet result = getForName(title);
		try {
			return new JumpHeightSheet(result);
		} catch (Exception e) {
			return JumpHeightSheet.INVALID;
		}
	}

	public static List<JumpHeightSheet> getSheets() throws IOException {
		List<JumpHeightSheet> result = new ArrayList<>();
		for (Sheet s: INSTANCE.service.getSheets()) {
			try {
				result.add(new JumpHeightSheet(s));
			} catch (SheetDataException e) {
				INSTANCE.log.error("unable to add sheet: " + s, e);
			}
		}
		return result;
	}
	
	private static Sheet getForName(String title) throws IOException {
		List<Sheet> all = INSTANCE.service.getSheets();
		Sheet result = null;
		for (Sheet s: all) {
			if(s.getProperties().getTitle().equals(title)) {
				result = s;
				break;
			}
		}
		return result;
	}

	public static JumpHeightSheet create(String title) throws IOException {
		try {
			return new JumpHeightSheet(INSTANCE.service.dublicateTo("Vorlage", title));
		} catch (Exception ex) {
			return JumpHeightSheet.INVALID;
		}
	}

	public static void delete(JumpHeightSheet test) throws IOException {
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
}
