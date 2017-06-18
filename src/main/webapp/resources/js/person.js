
function Person(personId, targetFunction, relationId){
	
	var person = sessionStorage.getItem("personId" + personId);
	
	if(person != null) {
		targetFunction(new PersonInstance(personId, JSON.parse(person), relationId));
	} else {
		if(personId>=0) {

			repo(baseUrl + "/person/" + personId, function(response) {
				sessionStorage.setItem("personId" + personId, JSON.stringify(response));
				targetFunction(new PersonInstance(personId, response, relationId));
			});
		} else {
			targetFunction(new PersonInstance(personId, null, null));
		}
	}
} 

var repo = function (requestUrl, targetFunction) {

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
		repo(baseUrl + "/group/", function(response) {
			sessionStorage.setItem("allGroups", JSON.stringify(response));
			targetFunction(response);
		});
		
	}
}

class PersonInstance {
	constructor(personId, response, relation) {
		this.personId = personId;
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
			this.id = personId;
			this.birth=null;
			this._bday = moment(null, "DD/MM/YYYY HH:mm:ss.SSS ZZ");
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

	update(targetFunction) {
		this.changed = null;

		var call="put";
		if(this.id<0) {
			call="post";
		}
		
		ajax(baseUrl + "/person/" + this.id, this, call, function(response) {
			sessionStorage.setItem("personId" + response.id, JSON.stringify(response));
			if(targetFunction != null) {
				targetFunction(new PersonInstance(response.id, response, null));
				
			}
		})
		this._changed = false;
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
	
	deleteRelatives() {
		this._relatives = null;
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
	
	updateContact(contact, targetFunction) {
		var url = baseUrl + "/contact/" + contact.id;
		var me = this;
		ajax(url, contact, "put", function(con) {
			var text = JSON.stringify(me);
			sessionStorage.setItem("personId" + me.personId, text);
			targetFunction(contact);
		});
	}
	
	groups (targetFunction) {
		if(this.persGroups == null) {
			var me = this;
			repo(baseUrl + "/persongroup/for/" + me.personId, function(response) {
				me.persGroups = response;
				var text = JSON.stringify(me);
				sessionStorage.setItem("personId" + me.personId, text);
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
			
			for (var j = 0, allLen = allGroupResult.length; j < allLen; j++) {
				for (var i = 0, len = me.persGroups.length; i < len; i++) {
					if (me.persGroups[i].groupId==allGroupResult[j].id) {
						if(ids[allGroupResult[j].id]) continue;
						ids[allGroupResult[j].id] = true;
						personGroups.push(allGroupResult[j]);
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
				ajax(baseUrl + "/persongroup/" + me.persGroups[i].id, me.persGroups[i], "delete", function(response) {
					me.persGroups.splice(index, 1);
					sessionStorage.setItem("personId" + me.personId, JSON.stringify(me));
				});
				break;
			}
		}
	}
}

