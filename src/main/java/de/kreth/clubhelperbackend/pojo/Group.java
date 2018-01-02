package de.kreth.clubhelperbackend.pojo;

/**
 * Entity mapped to table "GROUP".
 */
public class Group extends AbstractData {

	private static final long serialVersionUID = 6274828594078300002L;

	private String name;

	public Group() {
	}

	public Group(Long id, String name, java.util.Date changed,
			java.util.Date created) {
		super(id, changed, created);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Group other = (Group) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
