package de.kreth.clubhelperbackend.spreadsheet;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.sheets.v4.model.Sheet;

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
	
	public JumpHeightSheet get(String title) throws IOException {
		Sheet result = getForName(title);
		try {
			return new JumpHeightSheet(result);
		} catch (Exception e) {
			return JumpHeightSheet.INVALID;
		}
	}

	private Sheet getForName(String title) throws IOException {
		List<Sheet> all = service.getSheets();
		Sheet result = null;
		for (Sheet s: all) {
			if(s.getProperties().getTitle().equals(title)) {
				result = s;
				break;
			}
		}
		return result;
	}

	public JumpHeightSheet create(String title) throws IOException {
		try {
			return new JumpHeightSheet(service.dublicateTo("Vorlage", title));
		} catch (Exception ex) {
			return JumpHeightSheet.INVALID;
		}
	}

	public void delete(JumpHeightSheet test) throws IOException {
		service.delete(test.sheet);
	}
}
