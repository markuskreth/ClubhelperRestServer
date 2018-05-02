package de.kreth.clubhelperbackend.pojo;

import java.util.Date;

public class Startrecht extends AbstractData {

	private static final long serialVersionUID = -8288212579255646868L;

	private String verein_name;
	private String fachgebiet;
	private Date startrecht_beginn;
	private Date startrecht_ende;
	
	public Startrecht() {
	}
	
	public Startrecht(Long id, String verein_name, String fachgebiet, Date beginn, Date end) {
		this(id, verein_name, fachgebiet, beginn, end, null, null);
	}

	public Startrecht(Long id, String verein_name, String fachgebiet, Date beginn, Date end, Date changed, Date created) {
		super(id, changed, created);
		this.verein_name = verein_name;
		this.fachgebiet = fachgebiet;
		this.startrecht_beginn = beginn;
		this.startrecht_ende = end;
	}

	public String getVereinName() {
		return verein_name;
	}
	
	public void setVereinName(String verein_name) {
		this.verein_name = verein_name;
	}
	
	public String getFachgebiet() {
		return fachgebiet;
	}
	public void setFachgebiet(String fachgebiet) {
		this.fachgebiet = fachgebiet;
	}
	
	public Date getBeginn() {
		return startrecht_beginn;
	}
	
	public void setStartrechtBeginn(Date startrecht_beginn) {
		this.startrecht_beginn = startrecht_beginn;
	}
	
	public Date getEnd() {
		return startrecht_ende;
	}
	
	public void setStartrechtEnde(Date startrecht_ende) {
		this.startrecht_ende = startrecht_ende;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Startrecht [verein_name=").append(verein_name).append(", fachgebiet=").append(fachgebiet)
				.append(", beginn=").append(startrecht_beginn).append(", end=").append(startrecht_ende).append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((startrecht_beginn == null) ? 0 : startrecht_beginn.hashCode());
		result = prime * result + ((startrecht_ende == null) ? 0 : startrecht_ende.hashCode());
		result = prime * result + ((fachgebiet == null) ? 0 : fachgebiet.hashCode());
		result = prime * result + ((verein_name == null) ? 0 : verein_name.hashCode());
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
		Startrecht other = (Startrecht) obj;
		if (startrecht_beginn == null) {
			if (other.startrecht_beginn != null)
				return false;
		} else if (!startrecht_beginn.equals(other.startrecht_beginn))
			return false;
		if (startrecht_ende == null) {
			if (other.startrecht_ende != null)
				return false;
		} else if (!startrecht_ende.equals(other.startrecht_ende))
			return false;
		if (fachgebiet == null) {
			if (other.fachgebiet != null)
				return false;
		} else if (!fachgebiet.equals(other.fachgebiet))
			return false;
		if (verein_name == null) {
			if (other.verein_name != null)
				return false;
		} else if (!verein_name.equals(other.verein_name))
			return false;
		return true;
	}
	
}
