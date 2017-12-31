package de.kreth.clubhelperbackend.pojo;

import java.util.Date;

public class PersonCompetition extends AbstractData {

	private static final long serialVersionUID = 7197621720102586079L;

	private Long personId;
	private String calenderId;
	private String participation;
	private String routine;
	private String comment;
	
	
	public PersonCompetition() {
		super();
	}

	public PersonCompetition(Long id, Long personId, String calenderId, String participation
			, String routine, String comment, Date changed, Date created) {
		super(id, changed, created);
		this.personId = personId;
		this.calenderId = calenderId;
		this.participation = participation;
		this.routine = routine;
		this.comment = comment;
	}

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public String getCalenderId() {
		return calenderId;
	}

	public void setCalenderId(String calenderId) {
		this.calenderId = calenderId;
	}

	public String getParticipation() {
		return participation;
	}

	public void setParticipation(String participation) {
		this.participation = participation;
	}

	public String getRoutine() {
		return routine;
	}

	public void setRoutine(String routine) {
		this.routine = routine;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((calenderId == null) ? 0 : calenderId.hashCode());
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((participation == null) ? 0 : participation.hashCode());
		result = prime * result + ((personId == null) ? 0 : personId.hashCode());
		result = prime * result + ((routine == null) ? 0 : routine.hashCode());
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
		PersonCompetition other = (PersonCompetition) obj;
		if (calenderId == null) {
			if (other.calenderId != null)
				return false;
		} else if (!calenderId.equals(other.calenderId))
			return false;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (participation == null) {
			if (other.participation != null)
				return false;
		} else if (!participation.equals(other.participation))
			return false;
		if (personId == null) {
			if (other.personId != null)
				return false;
		} else if (!personId.equals(other.personId))
			return false;
		if (routine == null) {
			if (other.routine != null)
				return false;
		} else if (!routine.equals(other.routine))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PersonCompetition [personId=" + personId + ", calenderId=" + calenderId + "]";
	}
	
}
