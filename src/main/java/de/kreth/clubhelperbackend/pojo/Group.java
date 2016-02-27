package de.kreth.clubhelperbackend.pojo;

import java.util.Date;

/**
 * Entity mapped to table "GROUP".
 */
public class Group implements Data, java.io.Serializable {

	private static final long serialVersionUID = 6274828594078300002L;
	private Long id;
	/** Not-null value. */
	private String name;
	/** Not-null value. */
	private Date changed;
	/** Not-null value. */
	private Date created;

	/** Used to resolve relations */

	/** Used for active entity operations. */

	public Group() {
	}

	public Group(Long id) {
		this.id = id;
	}

	public Group(Long id, String name, Date changed, Date created) {
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

	/** Not-null value. */
	public String getName() {
		return name;
	}

	/**
	 * Not-null value; ensure this value is available before it is saved to the
	 * database.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/** Not-null value. */
	public Date getChanged() {
		return changed;
	}

	/**
	 * Not-null value; ensure this value is available before it is saved to the
	 * database.
	 */
	public void setChanged(Date changed) {
		this.changed = changed;
	}

	/** Not-null value. */
	public Date getCreated() {
		return created;
	}

	@Override
	public void setCreated(Date created) {
		this.created = created;
	}

}
