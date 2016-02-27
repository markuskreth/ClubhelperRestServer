package de.kreth.clubhelperbackend.pojo;


/**
 * Entity mapped to table "ATTENDANCE".
 */
public class Attendance implements Data, java.io.Serializable {

    private Long id;
    private java.util.Date onDate;
    private long personId;
    /** Not-null value. */
    private java.util.Date changed;
    /** Not-null value. */
    private java.util.Date created;


    public Attendance() {
    }

    public Attendance(Long id) {
        this.id = id;
    }

    public Attendance(Long id, java.util.Date onDate, long personId, java.util.Date changed, java.util.Date created) {
        this.id = id;
        this.onDate = onDate;
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

    public java.util.Date getOnDate() {
        return onDate;
    }

    public void setOnDate(java.util.Date onDate) {
        this.onDate = onDate;
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
