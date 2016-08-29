package de.kreth.clubhelperbackend.pojo;

/**
 * Entity mapped to table "DELETED_ENTRIES".
 */
public class DeletedEntries implements Data {

	private static final long serialVersionUID = -8631714525707328490L;

	private Long id;
	private String tablename;
	private Long entryId;
	private java.util.Date changed;
	private java.util.Date created;

	public DeletedEntries() {
	}

	public DeletedEntries(Long id) {
		this.id = id;
	}

	public DeletedEntries(Long id, String tablename, Long entryId, java.util.Date changed, java.util.Date created) {
		this.id = id;
		this.tablename = tablename;
		this.entryId = entryId;
		this.changed = changed;
		this.created = created;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public Long getEntryId() {
		return entryId;
	}

	public void setEntryId(Long entryId) {
		this.entryId = entryId;
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

}
