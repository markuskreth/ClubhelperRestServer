package de.kreth.clubhelperbackend.spreadsheet;

import com.google.api.services.sheets.v4.model.ExtendedValue;

public class CellValue {
	
	private final ExtendedValue value;
	
	public CellValue(ExtendedValue value) {
		super();
		this.value = value;
	}

	public double getDouble() {
		return value.getNumberValue().doubleValue();
	}
}
