package de.kreth.clubhelperbackend.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import de.kreth.clubhelperbackend.dao.abstr.AbstractDao;
import de.kreth.clubhelperbackend.dao.abstr.Dao;
import de.kreth.clubhelperbackend.pojo.Relative;

@Repository
public class RelativeDao extends AbstractDao<Relative>
		implements
			Dao<Relative> {

	public static final String COLUMN_PERSON1 = "person1";
	public static final String COLUMN_PERSON2 = "person2";
	public static final String COLUMN_PERSON1_RELATION = "TO_PERSON1_RELATION";
	public static final String COLUMN_PERSON2_RELATION = "TO_PERSON2_RELATION";
	
	private static final String columnNames[] = {COLUMN_PERSON1, COLUMN_PERSON2,
			COLUMN_PERSON1_RELATION, COLUMN_PERSON2_RELATION};

	private static final DaoConfig<Relative> daoConfig = new DaoConfig<Relative>(
			"relative", columnNames, new RelativeRowMapper(), null);

	public RelativeDao() {
		super(daoConfig);
	}

	public static class RelativeRowMapper extends RowMapper<Relative> {

		@Override
		public Collection<Object> mapObject(Relative obj) {
			List<Object> values = new ArrayList<Object>();
			values.add(obj.getPerson1());
			values.add(obj.getPerson2());
			values.add(obj.getToPerson1Relation());
			values.add(obj.getToPerson2Relation());
			return values;
		}

		public static class ToStringRelative extends Relative {

			private static final long serialVersionUID = 4772529931953029461L;

			@Override
			public String toString() {
				return getId() + ": " + getPerson2() + " "
						+ getToPerson1Relation() + " --> " + getPerson1();
			}
		}

		@Override
		public Class<ToStringRelative> getItemClass() {
			return ToStringRelative.class;
		}

	};

}
