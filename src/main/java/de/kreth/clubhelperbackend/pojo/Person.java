package de.kreth.clubhelperbackend.pojo;

/**
 * Entity mapped to table "PERSON".
 */
public class Person implements Data, java.io.Serializable {

	private static final long serialVersionUID = -7650782727510018817L;
	private Long id;
	private String prename;
	private String surname;
	private String type;
	private java.util.Date birth;
	/** Not-null value. */
	private java.util.Date changed;
	/** Not-null value. */
	private java.util.Date created;

	/** Used to resolve relations */

	/** Used for active entity operations. */

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

	/** Not-null value. */
	public java.util.Date getChanged() {
		return changed;
	}

	/**
	 * Not-null value; ensure this value is available before it is saved to the
	 * database.
	 */
	public void setChanged(java.util.Date changed) {
		this.changed = changed;
	}

	/** Not-null value. */
	public java.util.Date getCreated() {
		return created;
	}

	/**
	 * Not-null value; ensure this value is available before it is saved to the
	 * database.
	 */
	public void setCreated(java.util.Date created) {
		this.created = created;
	}

}
