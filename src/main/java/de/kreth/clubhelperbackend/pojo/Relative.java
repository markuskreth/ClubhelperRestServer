package de.kreth.clubhelperbackend.pojo;


/**
 * Entity mapped to table RELATIVE.
 */
public class Relative implements Data, java.io.Serializable {

	private static final long serialVersionUID = -8260891911558054631L;
	private Long id;
    private long person1;
    private long person2;
    private String toPerson2Relation;
    private String toPerson1Relation;
    /** Not-null value. */
    private java.util.Date changed;
    /** Not-null value. */
    private java.util.Date created;


    public Relative() {
    }

    public Relative(Long id) {
        this.id = id;
    }

    public Relative(Long id, long person1, long person2, String toPerson2Relation, String toPerson1Relation, java.util.Date changed, java.util.Date created) {
        this.id = id;
        this.person1 = person1;
        this.person2 = person2;
        this.toPerson2Relation = toPerson2Relation;
        this.toPerson1Relation = toPerson1Relation;
        this.changed = changed;
        this.created = created;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getPerson1() {
        return person1;
    }

    public void setPerson1(long person1) {
        this.person1 = person1;
    }

    public long getPerson2() {
        return person2;
    }

    public void setPerson2(long person2) {
        this.person2 = person2;
    }

    public String getToPerson2Relation() {
        return toPerson2Relation;
    }

    public void setToPerson2Relation(String toPerson2Relation) {
        this.toPerson2Relation = toPerson2Relation;
    }

    public String getToPerson1Relation() {
        return toPerson1Relation;
    }

    public void setToPerson1Relation(String toPerson1Relation) {
        this.toPerson1Relation = toPerson1Relation;
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
