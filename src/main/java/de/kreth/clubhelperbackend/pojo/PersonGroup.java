package de.kreth.clubhelperbackend.pojo;

/**
 * Entity mapped to table "PERSON_GROUP".
 */
public class PersonGroup extends AbstractData {

	private static final long serialVersionUID = 5404113637638434939L;

	private long personId;
	private long groupId;

	public PersonGroup() {
	}

	public PersonGroup(Long id, long personId, long groupId) {
		super(id);
		this.personId = personId;
		this.groupId = groupId;
	}

	public PersonGroup(Long id, long personId, long groupId,
			java.util.Date changed, java.util.Date created) {
		super(id, changed, created);
		this.personId = personId;
		this.groupId = groupId;
	}

	public long getPersonId() {
		return personId;
	}

	public void setPersonId(long personId) {
		this.personId = personId;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	@Override
	public String toString() {
		return "personId=" + personId + "; groupId=" + groupId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (groupId ^ (groupId >>> 32));
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
		PersonGroup other = (PersonGroup) obj;
		if (groupId != other.groupId)
			return false;
		if (personId != other.personId)
			return false;
		return true;
	}

}
