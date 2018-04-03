package de.kreth.clubhelperbackend.pojo;

import java.util.Date;

public abstract class AbstractData implements Data {

	private static final long serialVersionUID = -6879650233333766171L;

	private Long id;
	private java.util.Date changed;
	private java.util.Date created;
	private boolean deleted;

	public AbstractData() {
		deleted = false;
	}

	public AbstractData(Long id) {
		super();
		this.id = id;
	}

	public AbstractData(Long id, Date changed, Date created) {
		super();
		this.id = id;
		this.changed = changed;
		this.created = created;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
	public boolean isDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((changed == null) ? 0 : changed.hashCode());
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + (deleted ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractData other = (AbstractData) obj;
		if (changed == null) {
			if (other.changed != null)
				return false;
		} else if (!changed.equals(other.changed))
			return false;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (deleted != other.deleted)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
