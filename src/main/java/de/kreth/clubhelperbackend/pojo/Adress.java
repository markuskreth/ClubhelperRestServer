package de.kreth.clubhelperbackend.pojo;


/**
 * Entity mapped to table ADRESS.
 */
public class Adress implements Data {

    private Long id;
    private String adress1;
    private String adress2;
    private String plz;
    private String city;
    private long personId;
    /** Not-null value. */
    private java.util.Date changed;
    /** Not-null value. */
    private java.util.Date created;


    public Adress() {
    }

    public Adress(Long id) {
        this.id = id;
    }

    public Adress(Long id, String adress1, String adress2, String plz, String city, long personId, java.util.Date changed, java.util.Date created) {
        this.id = id;
        this.adress1 = adress1;
        this.adress2 = adress2;
        this.plz = plz;
        this.city = city;
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

    public String getAdress1() {
        return adress1;
    }

    public void setAdress1(String adress1) {
        this.adress1 = adress1;
    }

    public String getAdress2() {
        return adress2;
    }

    public void setAdress2(String adress2) {
        this.adress2 = adress2;
    }

    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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
