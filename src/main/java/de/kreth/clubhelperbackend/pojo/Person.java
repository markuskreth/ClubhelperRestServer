package de.kreth.clubhelperbackend.pojo;

/**
 * Entity mapped to table "PERSON".
 */
public class Person implements Data {

	private static final long serialVersionUID = -7650782727510018817L;

	private Long id;
	private String prename;
	private String surname;
	private String type;
	private java.util.Date birth;
	private java.util.Date changed;
	private java.util.Date created;

	public Person() {
	}

	public Person(Long id) {
		this.id = id;
	}

	public Person(Long id, String prename, String surname, String type, java.util.Date birth, java.util.Date changed,
			java.util.Date created) {
		this.id = id;
		this.prename = prename;
		this.surname = surname;
		this.type = type;
		this.birth = birth;
		this.changed = changed;
		this.created = created;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public java.util.Date getBirth() {
		return birth;
	}

	public void setBirth(java.util.Date birth) {
		this.birth = birth;
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

	public PersonType getPersonType() {
		try {
			return PersonType.valueOf(this.type);
		} catch (Exception e) {
			if (this.type == null || this.type.startsWith("AC")) {
				this.setType(PersonType.ACTIVE.name());
				return PersonType.ACTIVE;
			} else
				throw e;
		}
	}

	public void setPersonType(PersonType type) {
		setType(type.name());
	}

	@Override
	public String toString() {
		return id + ": " + prename + " " + surname;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Person person = (Person) o;

		if (id != null ? !id.equals(person.id) : person.id != null)
			return false;
		if (prename != null ? !prename.equals(person.prename) : person.prename != null)
			return false;
		if (surname != null ? !surname.equals(person.surname) : person.surname != null)
			return false;
		if (type != null ? !type.equals(person.type) : person.type != null)
			return false;
		if (birth != null ? !birth.equals(person.birth) : person.birth != null)
			return false;
		if (changed != null ? !changed.equals(person.changed) : person.changed != null)
			return false;
		return !(created != null ? !created.equals(person.created) : person.created != null);

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (prename != null ? prename.hashCode() : 0);
		result = 31 * result + (surname != null ? surname.hashCode() : 0);
		result = 31 * result + (type != null ? type.hashCode() : 0);
		result = 31 * result + (birth != null ? birth.hashCode() : 0);
		result = 31 * result + (changed != null ? changed.hashCode() : 0);
		result = 31 * result + (created != null ? created.hashCode() : 0);
		return result;
	}

	public class RelativeType {
		private RelationType type;
		private Person rel;

		private RelativeType() {
		}

		public Person getRel() {
			return rel;
		}

		public RelationType getType() {
			return type;
		}
	}

}
