package de.kreth.clubhelperbackend.pojo;

/**
 * Entity mapped to table "PERSON_GROUP".
 */
public class PersonGroup implements Data {

	private static final long serialVersionUID = 5404113637638434939L;

	private Long id;
	private long personId;
	private long groupId;
	private java.util.Date changed;
	private java.util.Date created;

	public PersonGroup() {
	}

	public PersonGroup(Long id) {
		this.id = id;
	}

	public PersonGroup(Long id, long personId, long groupId, java.util.Date changed, java.util.Date created) {
		this.id = id;
		this.personId = personId;
		this.groupId = groupId;
		this.changed = changed;
		this.created = created;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

}
