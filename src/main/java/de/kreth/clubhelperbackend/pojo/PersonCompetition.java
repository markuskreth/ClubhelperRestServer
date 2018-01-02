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
	
	
}
