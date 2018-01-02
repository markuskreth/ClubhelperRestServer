package de.kreth.clubhelperbackend.pojo;

/**
 * Entity mapped to table "ATTENDANCE".
 */
public class Attendance extends AbstractData {

	private static final long serialVersionUID = 4629334052631457791L;

	private java.util.Date onDate;
	private long personId;

	public Attendance() {
	}

	public Attendance(Long id, java.util.Date onDate, long personId,
			java.util.Date changed, java.util.Date created) {
		super(id, changed, created);
		this.onDate = onDate;
		this.personId = personId;
	}

	public java.util.Date getOnDate() {
		return onDate;
	}

	public void setOnDate(java.util.Date onDate) {
		this.onDate = onDate;
	}

	public long getPersonId() {
		return personId;
	}

	public void setPersonId(long personId) {
		this.personId = personId;
	}

	@Override
	public String toString() {
		return "Attendance [onDate=" + onDate + ", personId=" + personId + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((onDate == null) ? 0 : onDate.hashCode());
		result = prime * result + (int) (personId ^ (personId >>> 32));
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
		Attendance other = (Attendance) obj;
		if (onDate == null) {
			if (other.onDate != null)
				return false;
		} else if (!onDate.equals(other.onDate))
			return false;
		if (personId != other.personId)
			return false;
		return true;
	}

}
