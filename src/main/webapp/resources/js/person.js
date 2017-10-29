
function Person(personId, targetFunction, relationId){
	
	var person = sessionStorage.getItem("personId" + personId);
	
	if(person != null) {
		log.debug("found " + person + " in Storage.")
		targetFunction(new PersonInstance(personId, JSON.parse(person), relationId));
	} else {
		if(personId>=0) {
			log.warn(personId + " not found in repo. Reloading...");
			repo(baseUrl + "person/" + personId, function(response) {
				sessionStorage.setItem("personId" + personId, JSON.stringify(response));
				targetFunction(new PersonInstance(personId, response, relationId));
			});
		} else {
			log.debug("Creating new Person");
			targetFunction(new PersonInstance(personId, null, null));
		}
	}
} 

var repo = function (requestUrl, targetFunction) {

	log.trace("requesting " + requestUrl);
	$.ajax({
		url : requestUrl,
		dataType : "json",
	    error: function( jqXHR, textStatus, errorThrown){
	    	alert(requestUrl + "\n" + textStatus + "\n" + errorThrown);
	    },
		success : targetFunction
	});
}

var ajax = function (requestUrl, object, type, resultFunction) {
	var withResultFunction = true;
	if(resultFunction == null) {
		withResultFunction = false;
	}

	log.trace("requesting " + requestUrl);
	
	$.ajax(requestUrl,{
	    'data': JSON.stringify(object), //{action:'x',params:['a','b','c']}
	    'type': type,
	    'processData': withResultFunction,
	    'success': resultFunction,
	    'error': function( jqXHR, textStatus, errorThrown){
	    	alert(requestUrl + "\n" + textStatus + "\n" + errorThrown);
	    },
	    'contentType': 'application/json' //typically 'application/x-www-form-urlencoded', but the service you are calling may expect 'text/json'... check with the service to see what they expect as content-type in the HTTP header.
	});
}

var allGroups = function(targetFunction) {

	var groups = sessionStorage.getItem("allGroups");
	if(groups != null) {
		targetFunction(JSON.parse(groups));
	} else {
		repo(baseUrl + "group/", function(response) {
			sessionStorage.setItem("allGroups", JSON.stringify(response));
			targetFunction(response);
		});
		
	}
}

class PersonInstance {
	constructor(personId, response, relation) {
		this.id = personId;
		if(response!=null) {
			this.id = response.id;
			this.prename = response.prename;
			this.surname = response.surname;
			this.created = response.created;
			this.changed = response.changed;
			this.type = response.type;
			this.relation = relation;

			this.birth=response.birth;
			this._bday = moment(response.birth, "DD/MM/YYYY HH:mm:ss.SSS ZZ");
			this._bday.locale('DE_de');
			this._changed = false;
		} else {
			this.birth=null;
			this.persGroups = [];
			this._bday = moment(null, "DD/MM/YYYY HH:mm:ss.SSS ZZ");
			this._bday.locale('DE_de');
			this._changed = false;
		}
	}
	
	age() {
		return this._bday.fromNow(true);
	}
	
	hasChanges() {
		return this._changed;
	}
	
	hasChanged() {
		this._changed = true;
	}

	deletePerson(targetFunction) {
		ajax(baseUrl + "person/" + this.id, this, "delete", function(response) {
			sessionStorage.removeItem("personId" + this.id);
			if(targetFunction != null) {
				targetFunction();
			}
		})
	}

	update(targetFunction) {
		this.changed = null;

		var call="put";
		if(this.id<0) {
			call="post";

			sessionStorage.removeItem("personId" + this.id);
		}
		var me = this;
		ajax(baseUrl + "person/" + this.id, this, call, function(response) {
			log.debug("Updated Person: " + response);
			if(me.id != response.id) {
				log.info("Ids don't match after update - new person inserted? Overriding original with all answers in: " +response);
				for(var key in response) {
				    var value = response[key];
				    me[key] = value;
				}

				response = me;
			}
			sessionStorage.setItem("personId" + response.id, JSON.stringify(response));
			if(targetFunction != null) {
				targetFunction(new PersonInstance(response.id, response, null));
				
			}
		})
		this._changed = false;
	}

	updateGroup(groupIndex) {
		this.changed = null;

		var group = this.persGroups[groupIndex];
		var call="put";
		if(group.id<0) {
			call="post";
		}
		log.debug("Updating persongroup: id=" + group.id + ", personId=" + group.personId  + ", groupId=" + group.groupId);
		ajax(baseUrl + "persongroup/" + group.id, group, call, null);
		this._changed = false;
	}
	
	birthday() {
		return this._bday.format('L');
	}
	
	birthdayAsDate() {
		return this._bday.toDate();
	}
	
	setContacts(response) {
		this._contacts = response;
	}
	
	contacts(targetFunction) {
		if (this._contacts == null) {
			var me = this;
			repo(baseUrl + "contact/for/" + me.id, function(response) {
				me._contacts = response;
				sessionStorage.setItem("personId" + me.id, JSON.stringify(me));
				targetFunction(me._contacts);	
			});
		} else {
			targetFunction(this._contacts);	
		}
	}
	
	deleteRelatives() {
		this._relatives = null;
	}
	
	relatives(targetFunction) {
		if(this._relatives == null) {
			var me = this;

			repo(baseUrl + "relative/for/" + me.id, function(response) {
				me._relatives = response;
				sessionStorage.setItem("personId" + me.id, JSON.stringify(me));
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
			
			if (rel.person1 == this.id) {
				relId = rel.person2;
				rel.name = rel.toPerson1Relation;
			} else {
				relId = rel.person1;
				rel.name = rel.toPerson2Relation;
			}
			
			Person(relId, targetFunction, rel);
		}
	}
	
	updateContact(contact, targetFunction) {
		var url = baseUrl + "contact/" + contact.id;
		var me = this;
		ajax(url, contact, "put", function(con) {
			var text = JSON.stringify(me);
			sessionStorage.setItem("personId" + me.id, text);
			targetFunction(contact);
		});
	}
	
	groups (targetFunction) {
		if(this.persGroups == null) {
			var me = this;
			repo(baseUrl + "persongroup/for/" + me.id, function(response) {
				me.persGroups = response;
				var text = JSON.stringify(me);
				sessionStorage.setItem("personId" + me.id, text);
				me.processGroups(targetFunction);
			});
		} else {
			this.processGroups(targetFunction);
		}
	}
	
	processGroups (targetFunction) {
		var me = this;
		allGroups(function(allGroupResult){
			var personGroups = [];
			var ids = [];

			if(me.persGroups) {
				for (var i = 0, len = me.persGroups.length; i < len; i++) {
					for (var j = 0, allLen = allGroupResult.length; j < allLen; j++) {
						if (me.persGroups[i].groupId==allGroupResult[j].id) {
							if(ids[allGroupResult[j].id]) break;
							ids[allGroupResult[j].id] = true;
							personGroups.push(allGroupResult[j]);
							break;
						}
					}
				}
			}
			targetFunction(personGroups, allGroupResult);
		});
	}
	
	removeGroup(groupId) {
		var me = this;
		for (var i = 0, len = me.persGroups.length; i < len; i++) {
			if (me.persGroups[i].groupId==groupId) {
				var index = i;
				ajax(baseUrl + "persongroup/" + me.persGroups[i].id, me.persGroups[i], "delete", function(response) {
					me.persGroups.splice(index, 1);
					sessionStorage.setItem("personId" + me.id, JSON.stringify(me));
				});
				break;
			}
		}
	}
}

