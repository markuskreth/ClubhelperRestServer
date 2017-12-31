package de.kreth.clubhelperbackend.pojo;

/**
 * Entity mapped to table "PERSON".
 */
public class Person extends AbstractData {

	private static final long serialVersionUID = -7650782727510018817L;

	private String prename;
	private String surname;
	private java.util.Date birth;

	public Person() {
	}

	public Person(Long id, String prename, String surname, java.util.Date birth,
			java.util.Date changed, java.util.Date created) {
		super(id, changed, created);
		this.prename = prename;
		this.surname = surname;
		this.birth = birth;
	}

	public String getPrename() {
		return prename;
	}

	public void setPrename(String prename) {
		this.prename = prename;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public java.util.Date getBirth() {
		return birth;
	}

	public void setBirth(java.util.Date birth) {
		this.birth = birth;
	}

	@Override
	public String toString() {
		return getId() + ": " + prename + " " + surname;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((birth == null) ? 0 : birth.hashCode());
		result = prime * result + ((prename == null) ? 0 : prename.hashCode());
		result = prime * result + ((surname == null) ? 0 : surname.hashCode());
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
		Person other = (Person) obj;
		if (birth == null) {
			if (other.birth != null)
				return false;
		} else if (!birth.equals(other.birth))
			return false;
		if (prename == null) {
			if (other.prename != null)
				return false;
		} else if (!prename.equals(other.prename))
			return false;
		if (surname == null) {
			if (other.surname != null)
				return false;
		} else if (!surname.equals(other.surname))
			return false;
		return true;
	}

}
