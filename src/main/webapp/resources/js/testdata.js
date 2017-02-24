var personMarkusResponse = {
	"id" : 1,
	"prename" : "Markus",
	"surname" : "Kreth",
	"type" : "ACTIVE",
	"birth" : "21/08/1973 00:00:00.000 +0100",
	"changed" : "31/08/2015 22:26:03.000 +0200",
	"created" : "31/08/2015 22:26:03.000 +0200",
	"personType" : "ACTIVE"
};

var person51Response = {
	"id" : 51,
	"prename" : "RelPreName",
	"surname" : "RelSurName",
	"type" : "ACTIVE",
	"changed" : "31/08/2015 22:26:03.000 +0200",
	"created" : "31/08/2015 22:26:03.000 +0200",
	"personType" : "ACTIVE"
};

var person52Response = {
	"id" : 52,
	"prename" : "ParentPreName",
	"surname" : "ParentSurName",
	"type" : "RELATIVE",
	"changed" : "31/08/2015 22:26:03.000 +0200",
	"created" : "31/08/2015 22:26:03.000 +0200",
	"personType" : "RELATIVE"
};

var person54Response = {
	"id" : 54,
	"prename" : "SibPreName",
	"surname" : "SibSurName",
	"type" : "ACTIVE",
	"changed" : "31/08/2015 22:26:03.000 +0200",
	"created" : "31/08/2015 22:26:03.000 +0200",
	"personType" : "ACTIVE"
};

var contactsMarkusResponse = [ {
	"id" : 1,
	"type" : "Telefon",
	"value" : "+495112618291",
	"personId" : 1,
	"changed" : "16/05/2016 17:43:04.000 +0200",
	"created" : "31/08/2015 22:26:08.000 +0200"
}, {
	"id" : 2,
	"type" : "Email",
	"value" : "markus.kreth@web.de",
	"personId" : 1,
	"changed" : "31/08/2015 22:26:09.000 +0200",
	"created" : "31/08/2015 22:26:09.000 +0200"
}, {
	"id" : 3,
	"type" : "Mobile",
	"value" : "01742521286",
	"personId" : 1,
	"changed" : "31/08/2015 22:26:09.000 +0200",
	"created" : "31/08/2015 22:26:09.000 +0200"
} ];

var contacts51Response = [ {
	"id" : 4,
	"type" : "Telefon",
	"value" : "+49000000",
	"personId" : 51,
	"changed" : "16/05/2016 17:43:04.000 +0200",
	"created" : "31/08/2015 22:26:08.000 +0200"
}, {
	"id" : 5,
	"type" : "Email",
	"value" : "an@email.de",
	"personId" : 51,
	"changed" : "31/08/2015 22:26:09.000 +0200",
	"created" : "31/08/2015 22:26:09.000 +0200"
}, {
	"id" : 6,
	"type" : "Mobile",
	"value" : "0174555555",
	"personId" : 51,
	"changed" : "31/08/2015 22:26:09.000 +0200",
	"created" : "31/08/2015 22:26:09.000 +0200"
} ];

var relMarkus51Response = [ {
	"id" : 1,
	"person1" : 51,
	"person2" : 1,
	"toPerson2Relation" : "RELATIONSHIP",
	"toPerson1Relation" : "RELATIONSHIP",
	"changed" : "31/08/2015 22:26:16.000 +0200",
	"created" : "31/08/2015 22:26:16.000 +0200"
} ];

var relMarkus52Response = [ {
	"id" : 2,
	"person1" : 52,
	"person2" : 1,
	"toPerson2Relation" : "CHILD",
	"toPerson1Relation" : "PARENT",
	"changed" : "10/09/2015 22:26:16.000 +0200",
	"created" : "10/09/2015 22:26:16.000 +0200"
} ];

var relMarkus54Response = [ {
	"id" : 3,
	"person1" : 54,
	"person2" : 1,
	"toPerson2Relation" : "SIBLINGS",
	"toPerson1Relation" : "SIBLINGS",
	"changed" : "10/09/2015 22:26:16.000 +0200",
	"created" : "10/09/2015 22:26:16.000 +0200"
} ];

var repoReplacement = function(requestUrl, targetFunction) {
	if (requestUrl.includes("/contact/for/1")) {
		targetFunction(contactsMarkusResponse);
	} else if (requestUrl.includes("/contact/for/51")) {
		targetFunction(contacts51Response);
	} else if (requestUrl.includes("/relative/for/1")) {
		targetFunction(relMarkus51Response);
	} else if (requestUrl.includes("/person/1")) {
		targetFunction(personMarkusResponse);
	} else if (requestUrl.includes("/person/51")) {
		targetFunction(person51Response);
	} else if (requestUrl.includes("/person/52")) {
		targetFunction(person52Response);
	} else if (requestUrl.includes("/person/54")) {
		targetFunction(person54Response);
	}

}
var ajaxHistory = [];

var ajaxReplacement = function (requestUrl, object, type, resultFunction) {
	var obj = Object.create(null);
	obj.requestUrl = requestUrl;
	obj.object = object;
	obj.type = type;
	obj.resultFunction = resultFunction;
	
	ajaxHistory.push(obj);
	resultFunction(object);
}

