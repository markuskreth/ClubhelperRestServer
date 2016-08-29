package de.kreth.clubhelperbackend.pojo;

/**
 * Entity mapped to table "GROUP".
 */
public class Group implements Data {

	private static final long serialVersionUID = 6274828594078300002L;

	private Long id;
	private String name;
	private java.util.Date changed;
	private java.util.Date created;

	public Group() {
	}

	public Group(Long id) {
		this.id = id;
	}

	public Group(Long id, String name, java.util.Date changed, java.util.Date created) {
		this.id = id;
		this.name = name;
		this.changed = changed;
		this.created = created;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
