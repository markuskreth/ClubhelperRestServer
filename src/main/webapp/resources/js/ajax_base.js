
var repo = function (requestUrl, targetFunction) {

	log.trace("requesting " + requestUrl);
	console.log("requesting " + requestUrl);
	$.ajax({
		url : encodeURI(requestUrl),
		dataType : "json",
	    error: function( jqXHR, textStatus, errorThrown){
	    	var msg = extractError(jqXHR, textStatus, errorThrown);
	    	console.log(requestUrl + "\n" + msg);
//	    	alert(requestUrl + "\n" + msg);
	    },
		success : targetFunction
	});
};

var ajax = function (requestUrl, object, type, resultFunction) {
	var withResultFunction = true;
	if(resultFunction === null) {
		withResultFunction = false;
	}

	log.trace("requesting " + type + " " + requestUrl + ", withResultFunction=" + withResultFunction);
	console.log("requesting " + type + " " + requestUrl + ", withResultFunction=" + withResultFunction);
	
	$.ajax(encodeURI(requestUrl),{
	    'data': JSON.stringify(object), //{action:'x',params:['a','b','c']}
	    'type': type,
	    'processData': withResultFunction,
	    'success': resultFunction,
	    'error': function( jqXHR, textStatus, errorThrown){
	    	var msg = extractError(jqXHR, textStatus, errorThrown);
	    	console.log(requestUrl + "\n" + msg);
//	    	alert(requestUrl + "\n" + msg);
	    },
	    'contentType': 'application/json; charset=utf-8' //typically 'application/x-www-form-urlencoded', but the service you are calling may expect 'text/json'... check with the service to see what they expect as content-type in the HTTP header.
	});
};

function extractError(jqXHR, textStatus, errorThrown) {
	var html = $(jqXHR.responseText);
	var text = [];
	for (var i=0; i<html.length; i++) {
		var inner = html[i].innerHTML;
		if(inner && inner.startsWith("<b>Message</b>")) {
			inner = inner.replace("<b>Message</b>", "");
			console.log(i + "->" + inner);
			text.push(inner);
			break;
		} else if(html[i].outerHTML.startsWith("<title>")) {
			console.log(i + "->" + html[i].innerHTML);
			text.push(html[i].innerHTML);
		}
	}

	if(text.length >0) {
		return "\n" + text.join("\n");
	} else {
		return textStatus + "\n" + errorThrown;
	}

}