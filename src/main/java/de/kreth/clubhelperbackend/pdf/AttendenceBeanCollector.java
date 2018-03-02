package de.kreth.clubhelperbackend.pdf;

import static java.time.temporal.ChronoUnit.DAYS;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AttendenceBeanCollector {

	
	public List<AttendenceBean> getList(ResultSet rs) throws SQLException {
		Set<Date> dates = new HashSet<>();
		Map<String, Set<Date>> nameDateMap = new HashMap<>();
		
		List<AttendenceBean> tmp = new ArrayList<>();
		while (rs.next()) {
			Date date = new Date(rs.getDate("on_date").getTime());
			dates.add(date);
			AttendenceBean b = new AttendenceBean();
			b.name = new StringBuilder(rs.getString("surname")).append(", ").append(rs.getString("prename")).toString();
			b.date = date;
			b.set = true;
			tmp.add(b);

			Set<Date> nameDateSet;
			if(nameDateMap.keySet().contains(b.name)) {
				nameDateSet = nameDateMap.get(b.name);
			} else {
				nameDateSet = new HashSet<>();
				nameDateMap.put(b.name, nameDateSet);
			}
			nameDateSet.add(date);
		}

		tmp.sort(comparatorByNameAndDate);
		List<Date> sortedDate = new ArrayList<>(dates);
		Collections.sort(sortedDate);
		List<AttendenceBean> result = new ArrayList<>();
		for (AttendenceBean b: tmp) {
			result.add(b);
			Set<Date> personDateList = nameDateMap.get(b.name);
			for (Date d: sortedDate) {
				boolean isPersonDate = personDateList.contains(d);
				if(!isPersonDate && DAYS.between(b.date.toInstant(), d.toInstant())!=0) {
					AttendenceBean n = new AttendenceBean();
					n.name = b.name;
					n.date = d;
					n.set = false;
					result.add(n);
					personDateList.add(d);
				}
			}
		}
		result.sort(comparatorByNameAndDate);
		return result;
	}

	public String format(List<AttendenceBean> list) {
		DateFormat df = DateFormat.getDateInstance();

		Set<Date> dates = new HashSet<>();
		for (AttendenceBean b : list) {
			dates.add(b.date);
		}
		
		StringBuilder text = new StringBuilder();
		List<Date> orderedDates = new ArrayList<>(dates);
		orderedDates.sort((o1,o2)-> o1.compareTo(o2));
		for (Date d: orderedDates) {
			text.append(";").append(df.format(d));
		}
		String currentName;
		
		for (int index=0; index<list.size(); index++) {
			AttendenceBean b = list.get(index);
			text.append('\n').append(b.name);
			currentName = b.name;
			for (Date d: orderedDates) {
				text.append(';');
				if(d.equals(b.date) && b.set) {
					text.append("X");
				}
				if(index+1<list.size()) {
					AttendenceBean next = list.get(index+1);
					if(next.name.equals(currentName)) {
						index++;
						b = list.get(index);
					}
				}
			}
		}
		return text.toString();
	}

	public static final Comparator<AttendenceBean> comparatorByNameAndDate = new Comparator<AttendenceBean>() {

		@Override
		public int compare(AttendenceBean o1, AttendenceBean o2) {
			int compareTo = o1.name.compareTo(o2.name);
			if(compareTo == 0) {
				compareTo = o1.date.compareTo(o2.date);
			}
			return compareTo;
		}
	};
	
	public static final Comparator<AttendenceBean> comparatorByDate = new Comparator<AttendenceBean>() {

		@Override
		public int compare(AttendenceBean o1, AttendenceBean o2) {
			return o1.date.compareTo(o2.date);
		}
	};
}
