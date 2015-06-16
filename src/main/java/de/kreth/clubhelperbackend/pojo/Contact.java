package de.kreth.clubhelperbackend.pojo;


/**
 * Entity mapped to table CONTACT.
 */
public class Contact {

    private Long id;
    private String type;
    private String value;
    private long personId;
    /** Not-null value. */
    private java.util.Date changed;
    /** Not-null value. */
    private java.util.Date created;


    public Contact() {
    }

    public Contact(Long id) {
        this.id = id;
    }

    public Contact(Long id, String type, String value, long personId, java.util.Date changed, java.util.Date created) {
        this.id = id;
        this.type = type;
        this.value = value;
        this.personId = personId;
        this.changed = changed;
        this.created = created;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    /** Not-null value. */
    public java.util.Date getChanged() {
        return changed;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setChanged(java.util.Date changed) {
        this.changed = changed;
    }

    /** Not-null value. */
    public java.util.Date getCreated() {
        return created;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCreated(java.util.Date created) {
        this.created = created;
    }


}
