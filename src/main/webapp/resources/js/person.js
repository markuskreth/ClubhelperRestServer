
function Person(personId, targetFunction, relationId){
	
	var person = sessionStorage.getItem("personId" + personId);
	
	if(person != null) {
		targetFunction(new PersonInstance(personId, JSON.parse(person), relationId));
	} else {
		repo(baseUrl + "/person/" + personId, function(response) {
			sessionStorage.setItem("personId" + personId, JSON.stringify(response));
			targetFunction(new PersonInstance(personId, response, relationId));
		});
		
	}
} 

var repo = function (requestUrl, targetFunction) {

	$.ajax({
		url : requestUrl,
		dataType : "json",
		success : targetFunction
	});
}

class PersonInstance {
	constructor(personId, response, relation) {
		this.personId = personId;
		this.prename = response.prename;
		this.surname = response.surname;
		this.relation = relation;

		this.birth=response.birth;
		this._bday = moment(response.birth, "DD/MM/YYYY HH:mm:ss.SSS ZZ");	
		this._bday.locale('DE_de');
	}
	
	age() {
		return this._bday.fromNow(true);
	}
	
	birthday() {
		return this._bday.format('L');
	}
	
	setContacts(response) {
		this._contacts = response;
	}
	
	contacts(targetFunction) {
		if (this._contacts == null) {
			var me = this;
			repo(baseUrl + "/contact/for/" + me.personId, function(response) {
				me._contacts = response;
				sessionStorage.setItem("personId" + me.personId, JSON.stringify(me));
				targetFunction(me._contacts);	
			});
		} else {
			targetFunction(this._contacts);	
		}
	}
	
	relatives(targetFunction) {
		if(this._relatives == null) {
			var me = this;

			repo(baseUrl + "/relative/for/" + me.personId, function(response) {
				me._relatives = response;
				sessionStorage.setItem("personId" + me.personId, JSON.stringify(me));
				me.processRelatives(targetFunction);
			});
		} else {
			this.processRelatives(targetFunction);
		}
	}
	
	processRelatives (targetFunction) {

		for ( var index in this._relatives) {
			
			var rel = this._relatives[index];
			var relId = -1;
			
			if (rel.person1 == this.personId) {
				relId = rel.person2;
				rel.name = rel.toPerson1Relation;
			} else {
				relId = rel.person1;
				rel.name = rel.toPerson2Relation;
			}
			
			Person(relId, targetFunction, rel);
		}
	}
}


