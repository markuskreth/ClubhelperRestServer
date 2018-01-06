
function Person(personId, targetFunction, relationId){
	
	var person = personStore.get(personId);
	
	if(person != null) {
		log.debug("found " + person.toString() + " in Storage.");
		targetFunction(new PersonInstance(personId, person, relationId));
	} else {
		if(personId>=0) {
			log.warn(personId + " not found in repo. Reloading...");
			repo(baseUrl + "person/" + personId, function(response) {
				personStore.set(personId, response);
				targetFunction(new PersonInstance(personId, response, relationId));
			});
		} else {
			log.debug("Creating new Person");
			targetFunction(new PersonInstance(personId, null, null));
		}
	}
}

var allGroups = function(targetFunction) {

	var groups = groupStore.get();
	if(groups != null) {
		targetFunction(groups);
	} else {
		repo(baseUrl + "group/", function(response) {
			groupStore.set(response);
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
	
	hasChanges() {
		return this._changed;
	}
	
	hasChanged() {
		this._changed = true;
	}

}

PersonInstance.prototype.removeGroup = function (groupId) {
	var me = this;
	for (var i = 0, len = me.persGroups.length; i < len; i++) {
		if (me.persGroups[i].groupId==groupId) {
			var index = i;
			ajax(baseUrl + "persongroup/" + me.persGroups[i].id, me.persGroups[i], "delete", function(response) {
				me.persGroups.splice(index, 1);
				personStore.setItem(me, me.id);
			});
			break;
		}
	}
}

PersonInstance.prototype.processGroups = function (targetFunction) {
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

PersonInstance.prototype.groups = function (targetFunction) {
	if(this.persGroups == null) {
		var me = this;
		repo(baseUrl + "persongroup/for/" + me.id, function(response) {
			me.persGroups = response;
			var text = JSON.stringify(me);
			personStore.set(me, me.id);
			me.processGroups(targetFunction);
		});
	} else {
		this.processGroups(targetFunction);
	}
}


PersonInstance.prototype.updateContact = function (contact, targetFunction) {
	var url = baseUrl + "contact/" + contact.id;
	var me = this;
	ajax(url, contact, "put", function(con) {
		var text = JSON.stringify(me);
		personStore.set(me, me.id);
		targetFunction(contact);
	});
}

PersonInstance.prototype.processRelatives = function (targetFunction) {

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

PersonInstance.prototype.relatives = function (targetFunction) {
	if(this._relatives == null) {
		var me = this;

		repo(baseUrl + "relative/for/" + me.id, function(response) {
			me._relatives = response;
			personStore.set(me, me.id);
			me.processRelatives(targetFunction);
		});
	} else {
		this.processRelatives(targetFunction);
	}
}

PersonInstance.prototype.deleteRelatives = function () {
	this._relatives = null;
}

PersonInstance.prototype.contacts = function (targetFunction) {
	if (this._contacts == null) {
		var me = this;
		repo(baseUrl + "contact/for/" + me.id, function(response) {
			me._contacts = response;
			personStore.set(me, me.id);
			targetFunction(me._contacts);	
		});
	} else {
		targetFunction(this._contacts);	
	}
}

PersonInstance.prototype.birthday = function () {
	return this._bday.format('L');
}

PersonInstance.prototype.birthdayAsDate = function () {
	return this._bday.format('YYYY-MM-DD');
}

PersonInstance.prototype.updateGroup = function (groupIndex) {
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

PersonInstance.prototype.setContacts = function (response) {
	this._contacts = response;
}

PersonInstance.prototype.update = function (targetFunction) {
	this.changed = null;

	var call="put";
	if(this.id<0) {
		call="post";

		personStore.remove(this);
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
		personStore.set(response, response.id);
		if(targetFunction != null) {
			targetFunction(new PersonInstance(response.id, response, null));
			
		}
	})
	this._changed = false;
}

PersonInstance.prototype.age = function () {
	return this._bday.fromNow(true);
}

PersonInstance.prototype.deletePerson = function (targetFunction) {
	var me = this;
	ajax(baseUrl + "person/" + this.id, this, "delete", function(response) {
		personStore.remove(me);
		if(targetFunction != null) {
			targetFunction();
		}
	})
}

PersonInstance.prototype.toString = function () {
	return JSON.stringify(this);
}
