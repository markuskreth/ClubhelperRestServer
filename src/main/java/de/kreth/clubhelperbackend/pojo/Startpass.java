package de.kreth.clubhelperbackend.pojo;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Startpass extends AbstractData {

	private static final long serialVersionUID = -6071881343354800916L;

	private long personId;
	private String startpassNr;
	private List<Startrecht> startrechte;
	
	public Startpass() {
		super();
	}

	public Startpass(Long id) {
		super(id);
	}
	
	public Startpass(Long id, long personId, String startpassNr, List<Startrecht> startrechte, Date changed, Date created) {
		super(id, changed, created);
		this.personId = personId;
		this.startrechte = startrechte;
		this.startpassNr = startpassNr;
	}

	public long getPersonId() {
		return personId;
	}

	public void setPersonId(long personId) {
		this.personId = personId;
	}

	public String getStartpassNr() {
		return startpassNr;
	}

	public void setStartpassNr(String startpassNr) {
		this.startpassNr = startpassNr;
	}

	public List<Startrecht> getStartrechte() {
		return startrechte;
	}

	public void setStartrechte(List<Startrecht> startrechte) {
		this.startrechte = Collections.unmodifiableList(startrechte);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Startpass [personId=").append(personId).append(", startpassNr=").append(startpassNr).append(", startrechte=").append(startrechte)
				.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (personId ^ (personId >>> 32));
		result = prime * result + ((startrechte == null) ? 0 : startrechte.hashCode());
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
		Startpass other = (Startpass) obj;
		if (personId != other.personId)
			return false;
		if (startrechte == null) {
			if (other.startrechte != null)
				return false;
		} else if (!startrechte.equals(other.startrechte))
			return false;
		return true;
	}

}
