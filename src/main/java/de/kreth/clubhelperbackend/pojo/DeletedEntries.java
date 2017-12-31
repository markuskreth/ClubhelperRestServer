package de.kreth.clubhelperbackend.pojo;

/**
 * Entity mapped to table "DELETED_ENTRIES".
 */
public class DeletedEntries extends AbstractData {

	private static final long serialVersionUID = -8631714525707328490L;

	private String tablename;
	private Long entryId;

	public DeletedEntries() {
	}

	public DeletedEntries(Long id, String tablename, Long entryId,
			java.util.Date changed, java.util.Date created) {
		super(entryId, changed, created);
		this.tablename = tablename;
		this.entryId = entryId;
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

	@Override
	public String toString() {
		return "DeletedEntries [tablename=" + tablename + ", entryId=" + entryId
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((entryId == null) ? 0 : entryId.hashCode());
		result = prime * result
				+ ((tablename == null) ? 0 : tablename.hashCode());
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
		DeletedEntries other = (DeletedEntries) obj;
		if (entryId == null) {
			if (other.entryId != null)
				return false;
		} else if (!entryId.equals(other.entryId))
			return false;
		if (tablename == null) {
			if (other.tablename != null)
				return false;
		} else if (!tablename.equals(other.tablename))
			return false;
		return true;
	}

}
