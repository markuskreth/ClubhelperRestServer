package de.kreth.clubhelperbackend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.kreth.clubhelperbackend.controller.abstr.AbstractController;
import de.kreth.clubhelperbackend.controller.abstr.ClubController;
import de.kreth.clubhelperbackend.dao.abstr.Dao;
import de.kreth.clubhelperbackend.pojo.Adress;
import de.kreth.clubhelperbackend.pojo.Contact;
import de.kreth.clubhelperbackend.pojo.Person;
import de.kreth.clubhelperbackend.pojo.Relative;

@Controller
@RequestMapping("/person")
public class PersonController extends AbstractController<Person> {

	private ClubController<Contact> contactController;
	private ClubController<Relative> relativeController;
	private ClubController<Adress> adressController;

	@Autowired
	public PersonController(Dao<Person> personDao) {
		super(personDao, Person.class);
	}

	@Autowired
	public void setContactController(ClubController<Contact> contactController) {
		this.contactController = contactController;
	}

	@Autowired
	public void setRelativeController(ClubController<Relative> relativeController) {
		this.relativeController = relativeController;
	}

	@Autowired
	public void setAdressController(ClubController<Adress> adressController) {
		this.adressController = adressController;
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@PreAuthorize("hasRole('Admin')")
	public String getAsView(@PathVariable("id") long id, @RequestParam(required = false) boolean ajax, Model m) {
		List<Contact> contacts = contactController.getByParentId(id);
		m.addAttribute(Contact.class.getSimpleName() + "List", contacts);

		List<Adress> adresses = adressController.getByParentId(id);
		m.addAttribute(Adress.class.getSimpleName() + "List", adresses);

		List<Relative> relatives = relativeController.getByParentId(id);
		List<PersonRelative> rel = new ArrayList<PersonController.PersonRelative>();
		for (Relative r : relatives) {
			PersonRelative current = new PersonRelative(r);
			long otherId;
			if (r.getPerson1() == id) {
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

		return super.getAsView(id, ajax, m);
	}

	/**
	 * Delivers list with one Person with id.
	 */
	@Override
	public List<Person> getByParentId(long id) {
		List<Person> all = new ArrayList<Person>();
		all.add(getById(id));
		return all;
	}

	/**
	 * 
	 * @author markus
	 *
	 */
	public class PersonRelative extends Relative {

		private static final long serialVersionUID = 4828690343464403867L;
		private Person toPerson;
		private String relation;

		public PersonRelative(Relative r) {
			super(r.getId(), r.getPerson1(), r.getPerson2(), r.getToPerson2Relation(), r.getToPerson1Relation(),
					r.getChanged(), r.getCreated());
			toPerson = getById(r.getPerson1());
			relation = r.getToPerson1Relation();
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
			bld.append(relation).append(" ").append(toPerson.getId()).append(": ").append(toPerson.getPrename())
					.append(" ").append(toPerson.getSurname());
			return bld.toString();
		}
	}
}
