package de.kreth.clubhelperbackend.pojo;

/**
 * Entity mapped to table "ADRESS".
 */
public class Adress extends AbstractData {

	private static final long serialVersionUID = 2456211837852984124L;

	private String adress1;
	private String adress2;
	private String plz;
	private String city;
	private long personId;

	public Adress() {
	}

	public Adress(Long id, String adress1, String adress2, String plz,
			String city, long personId, java.util.Date changed,
			java.util.Date created) {
		super(id, changed, created);
		this.adress1 = adress1;
		this.adress2 = adress2;
		this.plz = plz;
		this.city = city;
		this.personId = personId;
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

		if (plz != null && plz.length() > 0 && city != null
				&& city.length() > 0) {

			if (bld.length() > 0)
				bld.append("\n");
			bld.append(plz).append(" ").append(city);
		}

		return bld.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((adress1 == null) ? 0 : adress1.hashCode());
		result = prime * result + ((adress2 == null) ? 0 : adress2.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + (int) (personId ^ (personId >>> 32));
		result = prime * result + ((plz == null) ? 0 : plz.hashCode());
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
		Adress other = (Adress) obj;
		if (adress1 == null) {
			if (other.adress1 != null)
				return false;
		} else if (!adress1.equals(other.adress1))
			return false;
		if (adress2 == null) {
			if (other.adress2 != null)
				return false;
		} else if (!adress2.equals(other.adress2))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (personId != other.personId)
			return false;
		if (plz == null) {
			if (other.plz != null)
				return false;
		} else if (!plz.equals(other.plz))
			return false;
		return true;
	}

}
