package de.kreth.clubhelperbackend.pojo;

/**
 * Entity mapped to table "ATTENDANCE".
 */
public class Attendance implements Data {

	private static final long serialVersionUID = 4629334052631457791L;

	private Long id;
	private java.util.Date onDate;
	private long personId;
	private java.util.Date changed;
	private java.util.Date created;

	public Attendance() {
	}

	public Attendance(Long id) {
		this.id = id;
	}

	public Attendance(Long id, java.util.Date onDate, long personId, java.util.Date changed, java.util.Date created) {
		this.id = id;
		this.onDate = onDate;
		this.personId = personId;
		this.changed = changed;
		this.created = created;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public java.util.Date getChanged() {
		return changed;
	}

	public void setChanged(java.util.Date changed) {
		this.changed = changed;
	}

	public java.util.Date getCreated() {
		return created;
	}

	public void setCreated(java.util.Date created) {
		this.created = created;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Attendance that = (Attendance) o;

		if (personId != that.personId)
			return false;
		if (id != null ? !id.equals(that.id) : that.id != null)
			return false;
		if (onDate != null ? !onDate.equals(that.onDate) : that.onDate != null)
			return false;
		if (changed != null ? !changed.equals(that.changed) : that.changed != null)
			return false;
		return !(created != null ? !created.equals(that.created) : that.created != null);

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (onDate != null ? onDate.hashCode() : 0);
		result = 31 * result + (int) (personId ^ (personId >>> 32));
		result = 31 * result + (changed != null ? changed.hashCode() : 0);
		result = 31 * result + (created != null ? created.hashCode() : 0);
		return result;
	}

}
