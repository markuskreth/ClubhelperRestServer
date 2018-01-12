/*jshint esversion: 6 */

/**
 * 
 */
var competitionParticipants = (function () {

	// private
	var competitionId;
		
	function CompetitorList() {
		
		this.competition = function (compId) {
			competitionId = compId;
		};
		this.toJSON = function() {
			var sub = [];
			for(var el of this) {
				sub.push(el);
			}

			var item = {competitionId, list: sub};
			
			return item;
		};
	}
	
	CompetitorList.prototype = new ExtendableItemList();
	
	// public
	return new CompetitorList();
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
		toJSON : function () {
			return _list;
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
