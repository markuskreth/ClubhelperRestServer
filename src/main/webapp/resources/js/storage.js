/**
 * Storage Class for local Browser storage.
 */
function Storage(classname) {
	this.classname = classname;
}

Storage.prototype.get = function (id) {

	console.log("from storage " + this.classname + ", id=" + id);
	
	if(id == null) {
		return JSON.parse(sessionStorage.getItem(this.classname));
	} else {
		return JSON.parse(sessionStorage.getItem(this.classname + id));
	}
	
}

Storage.prototype.clearAll = function () {
	sessionStorage.clear();
}

Storage.prototype.length = function () {
	return sessionStorage.length;
}

Storage.prototype.key = function (i) {
	return sessionStorage.key(i);
}

Storage.prototype.set = function (obj, id) {
	if(!id) {
		id = obj.id;
	}
	if(!id) {
		sessionStorage.setItem(this.classname, JSON.stringify(obj));
	} else {
		sessionStorage.setItem(this.classname + id, JSON.stringify(obj));
	}
}

Storage.prototype.remove = function (obj) {
	sessionStorage.removeItem(this.classname + obj.id);
}

Storage.prototype[Symbol.iterator] = function() {

	var index = 0;
	var theClassName = this.classname;
    const iterator = {
            next() {
                while (index < sessionStorage.length) {
                	var key = sessionStorage.key(index++);
                	if(key.startsWith(theClassName)) {
                		var obj = JSON.parse(sessionStorage.getItem(key));
                		console.log("from storage " + theClassName + ", key=" + key + ", id=" + obj.id + ", json=" + sessionStorage.getItem(key));
                		return { value: obj, done: false  };
                	}
                } 

                return { done: true };
            }
        };
    return iterator;
}
