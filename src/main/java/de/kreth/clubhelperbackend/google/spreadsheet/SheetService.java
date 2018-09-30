package de.kreth.clubhelperbackend.google.spreadsheet;

public enum SheetService {

	INSTANCE;
	private final Sheets service = new SheetImpl();
	
	public Sheets getService() {
		return service;
	}

}
