package de.kreth.clubhelperbackend.pdf;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static java.time.temporal.ChronoUnit.DAYS;

public class AttendenceBeanCollector {

	
	public List<AttendenceBean> getList(ResultSet rs) throws SQLException {
		Set<Date> dates = new HashSet<>();
		
		List<AttendenceBean> tmp = new ArrayList<>();
		while (rs.next()) {
			Date date = new Date(rs.getDate("on_date").getTime());
			dates.add(date);
			AttendenceBean b = new AttendenceBean();
			b.name = new StringBuilder(rs.getString("surname")).append(", ").append(rs.getString("prename")).toString();
			b.date = date;
			b.set = true;
			tmp.add(b);
		}

		List<AttendenceBean> result = new ArrayList<>();
		for (AttendenceBean b: tmp) {
			result.add(b);
			for (Date d: dates) {
				if(DAYS.between(b.date.toInstant(), d.toInstant())!=0) {
					AttendenceBean n = new AttendenceBean();
					n.name = b.name;
					n.date = d;
					n.set = false;
					result.add(n);
				}
			}
		}
		return result;
	}
}
