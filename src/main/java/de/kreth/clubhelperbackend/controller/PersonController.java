package de.kreth.clubhelperbackend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.kreth.clubhelperbackend.dao.Dao;
import de.kreth.clubhelperbackend.pojo.Adress;
import de.kreth.clubhelperbackend.pojo.Contact;
import de.kreth.clubhelperbackend.pojo.Person;
import de.kreth.clubhelperbackend.pojo.Relative;

@Controller
@RequestMapping("/person")
public class PersonController extends AbstractController<Person>{

	private ContactController contactController;
	private RelativeController relativeController;
	private AdressController adressController;
	
	@Autowired
	public PersonController(Dao<Person> personDao) {
		super(personDao, Person.class);
	}

	@Autowired
	public void setContactController(ContactController contactController) {
		this.contactController = contactController;
	}

	@Autowired
	public void setRelativeController(RelativeController relativeController) {
		this.relativeController = relativeController;
	}

	@Autowired
	public void setAdressController(AdressController adressController) {
		this.adressController = adressController;
	}

	@Override
	@RequestMapping(value="/get/{id}", method=RequestMethod.GET)
	public String get(@PathVariable("id") long id, Model m) {
		List<Contact> contacts = contactController.getForId(id);
		m.addAttribute(Contact.class.getSimpleName() + "List", contacts);
		
		List<Adress> adresses = adressController.getForId(id);
		m.addAttribute(Adress.class.getSimpleName() + "List", adresses);
		
		List<Relative> relatives = relativeController.getForId(id);
		List<PersonRelative> rel = new ArrayList<PersonController.PersonRelative>();
		for(Relative r: relatives) {
			PersonRelative current = new PersonRelative(r);
			long otherId;
			if(r.getPerson1() == id) {
				current.relation = r.getToPerson2Relation();
				otherId = r.getPerson2();
			} else {
				current.relation = r.getToPerson1Relation();
				otherId = r.getPerson1();
			}
			current.toPerson = dao.getById(otherId);
			rel.add(current);
		}
		m.addAttribute(PersonRelative.class.getSimpleName() + "List", rel);
		
		return super.get(id, m);
	}

	/**
	 * Delivers list with one Person with id.
	 */
	@Override
	public List<Person> getForId(long id) {
		List<Person> all = new ArrayList<Person>();
		all.add(getObject(id));
		return all;
	}

	public class PersonRelative extends Relative {
		
		private Person toPerson;
		private String relation;
		
		public PersonRelative(Relative r) {
			super(r.getId(), r.getPerson1(), r.getPerson2(), r.getToPerson2Relation(), r.getToPerson1Relation(), r.getChanged(), r.getCreated());
		}

		public Person getToPerson() {
			return toPerson;
		}

		public String getRelation() {
			return relation;
		}
		
		@Override
		public String toString() {
			StringBuilder bld = new StringBuilder();
			bld.append(relation).append(" ").append(toPerson.getId()).append(": ").append(toPerson.getPrename()).append(" ").append(toPerson.getSurname());
			return bld.toString();
		}
	}
}
