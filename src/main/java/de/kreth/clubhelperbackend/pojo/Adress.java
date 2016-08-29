package de.kreth.clubhelperbackend.pojo;

/**
 * Entity mapped to table "ADRESS".
 */
public class Adress implements Data {

	private static final long serialVersionUID = 2456211837852984124L;

	private Long id;
	private String adress1;
	private String adress2;
	private String plz;
	private String city;
	private long personId;
	private java.util.Date changed;
	private java.util.Date created;

	public Adress() {
	}

	public Adress(Long id) {
		this.id = id;
	}

	public Adress(Long id, String adress1, String adress2, String plz, String city, long personId,
			java.util.Date changed, java.util.Date created) {
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

	@Override
	public String toString() {
		StringBuilder bld = new StringBuilder();
		if (adress1 != null)
			bld.append(adress1);
		if (adress2 != null && adress2.trim().length() > 0) {
			if (bld.length() > 0)
				bld.append("\n");
			bld.append(adress2);
		}

		if (plz != null && plz.length() > 0 && city != null && city.length() > 0) {

			if (bld.length() > 0)
				bld.append("\n");
			bld.append(plz).append(" ").append(city);
		}

		return bld.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Adress adress = (Adress) o;

		if (personId != adress.personId)
			return false;
		if (id != null ? !id.equals(adress.id) : adress.id != null)
			return false;
		if (adress1 != null ? !adress1.equals(adress.adress1) : adress.adress1 != null)
			return false;
		if (adress2 != null ? !adress2.equals(adress.adress2) : adress.adress2 != null)
			return false;
		if (plz != null ? !plz.equals(adress.plz) : adress.plz != null)
			return false;
		if (city != null ? !city.equals(adress.city) : adress.city != null)
			return false;
		if (changed != null ? !changed.equals(adress.changed) : adress.changed != null)
			return false;
		return !(created != null ? !created.equals(adress.created) : adress.created != null);

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (adress1 != null ? adress1.hashCode() : 0);
		result = 31 * result + (adress2 != null ? adress2.hashCode() : 0);
		result = 31 * result + (plz != null ? plz.hashCode() : 0);
		result = 31 * result + (city != null ? city.hashCode() : 0);
		result = 31 * result + (int) (personId ^ (personId >>> 32));
		result = 31 * result + (changed != null ? changed.hashCode() : 0);
		result = 31 * result + (created != null ? created.hashCode() : 0);
		return result;
	}

}
