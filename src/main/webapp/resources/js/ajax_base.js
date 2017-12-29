
var repo = function (requestUrl, targetFunction) {

	log.trace("requesting " + requestUrl);
	$.ajax({
		url : encodeURI(requestUrl),
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
	
	$.ajax(encodeURI(requestUrl),{
	    'data': JSON.stringify(object), //{action:'x',params:['a','b','c']}
	    'type': type,
	    'processData': withResultFunction,
	    'success': resultFunction,
	    'error': function( jqXHR, textStatus, errorThrown){
	    	alert(requestUrl + "\n" + textStatus + "\n" + errorThrown);
	    },
	    'contentType': 'application/json; charset=utf-8' //typically 'application/x-www-form-urlencoded', but the service you are calling may expect 'text/json'... check with the service to see what they expect as content-type in the HTTP header.
	});
}

