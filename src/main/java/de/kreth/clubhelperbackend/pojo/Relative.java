package de.kreth.clubhelperbackend.pojo;

/**
 * Entity mapped to table "RELATIVE".
 */
public class Relative extends AbstractData {

	private static final long serialVersionUID = -8260891911558054631L;
	private long person1;
	private long person2;
	private String toPerson2Relation;
	private String toPerson1Relation;

	public Relative() {
	}

	public Relative(Long id, long person1, long person2,
			String toPerson2Relation, String toPerson1Relation,
			java.util.Date changed, java.util.Date created) {
		super(id, changed, created);
		this.person1 = person1;
		this.person2 = person2;
		this.toPerson2Relation = toPerson2Relation;
		this.toPerson1Relation = toPerson1Relation;
	}

	public long getPerson1() {
		return person1;
	}

	public void setPerson1(long person1) {
		this.person1 = person1;
	}

	public long getPerson2() {
		return person2;
	}

	public void setPerson2(long person2) {
		this.person2 = person2;
	}

	public String getToPerson2Relation() {
		return toPerson2Relation;
	}

	public void setToPerson2Relation(String toPerson2Relation) {
		this.toPerson2Relation = toPerson2Relation;
	}

	public String getToPerson1Relation() {
		return toPerson1Relation;
	}

	public void setToPerson1Relation(String toPerson1Relation) {
		this.toPerson1Relation = toPerson1Relation;
	}

	@Override
	public String toString() {
		return "Relative [person1=" + person1 + ", person2=" + person2
				+ ", toPerson2Relation=" + toPerson2Relation
				+ ", toPerson1Relation=" + toPerson1Relation + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (person1 ^ (person1 >>> 32));
		result = prime * result + (int) (person2 ^ (person2 >>> 32));
		result = prime * result + ((toPerson1Relation == null)
				? 0
				: toPerson1Relation.hashCode());
		result = prime * result + ((toPerson2Relation == null)
				? 0
				: toPerson2Relation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Relative other = (Relative) obj;
		if (person1 != other.person1)
			return false;
		if (person2 != other.person2)
			return false;
		if (toPerson1Relation == null) {
			if (other.toPerson1Relation != null)
				return false;
		} else if (!toPerson1Relation.equals(other.toPerson1Relation))
			return false;
		if (toPerson2Relation == null) {
			if (other.toPerson2Relation != null)
				return false;
		} else if (!toPerson2Relation.equals(other.toPerson2Relation))
			return false;
		return true;
	}

}
