/*jshint esversion: 6 */

/**
 * 
 */
var competitionParticipants = (function () {
	
	// private
	var compId;
		
	// public
	const instance = {
		competition: function (competitionId) {
			compId = competitionId;
		}
	};
	instance.prototype = Object.create(ExtendableItemList.prototype);
	return instance;
})();

function ExtendableItemList () {
	
	var _list = [];
	
	const instance = {
		push: function (item) {
			_list.push(item);
		},
		length: function() {
			return _list.length;
		},
		remove: function (item) {
			var index = _list.indexOf(pId);
			if (index > -1) {
				_list.splice(index, 1);
			}
		},
		[Symbol.iterator]: function() {

			var index = 0;

		    const iterator = {
		            next() {
		                while (index < _list.length) {
		                	return { value: _list[index++], done: false  };
		                } 

		                return { done: true };
		            }
		        };
		    return iterator;
		}
	};

//	instance.prototype;
	return instance;
}


ExtendableItemList.prototype[Symbol.iterator] = function() {

	var index = 0;

    const iterator = {
            next() {
                while (index < _list.length) {
                	return _list[index++];
                } 

                return { done: true };
            }
        };
    return iterator;
};
