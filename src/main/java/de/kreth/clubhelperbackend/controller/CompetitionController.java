package de.kreth.clubhelperbackend.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.kreth.clubhelperbackend.dao.abstr.Dao;
import de.kreth.clubhelperbackend.pojo.PersonCompetition;

@Controller
@RequestMapping("/competitions")
//@PreAuthorize("hasAnyRole('STAFF')")
public class CompetitionController {
	
	private Dao<PersonCompetition> dao;

	@Autowired
	public CompetitionController(Dao<PersonCompetition> dao) {
		this.dao = dao;
	}

	@RequestMapping(value = "/{calendarId}/{eventId}", method = RequestMethod.GET)
	@ResponseBody
	public List<PersonCompetition> getCompetitorsForEvent(@PathVariable("calendarId")String calendarId,@PathVariable("eventId")String eventId){
		
		return dao.getByWhere("calendar_id='" + calendarId + "' AND event_id='" + eventId + "'");
	}
	
	@RequestMapping(value = "/{calendarId}/{eventId}", method = RequestMethod.PUT)
	@ResponseBody
	public PersonCompetition storeCompetitorsForEvent(@PathVariable("calendarId")String calendarId, @PathVariable("eventId")String eventId, @RequestBody long personId){
		PersonCompetition p = new PersonCompetition();
		p.setEventId(eventId);
		p.setCalenderId(calendarId);
		p.setPersonId(personId);
		p = dao.insert(p);
		return p;
	}
	
}
