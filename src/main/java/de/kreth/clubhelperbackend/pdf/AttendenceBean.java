package de.kreth.clubhelperbackend.pdf;

import java.text.DateFormat;
import java.util.Date;

public class AttendenceBean {
	String name;
	Date date;
	boolean set;
	
	public static String name() {
		return "";
	}
	public static String head() {
		return "";
	}
	public static boolean set() {
		return true;
	}
	
	@Override
	public String toString() {		
		return String.format("%30s;%s;%s%n", name, DateFormat.getDateInstance().format(date), (set?"X":" "));
	}
}
