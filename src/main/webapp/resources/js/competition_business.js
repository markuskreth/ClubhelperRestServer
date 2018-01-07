/*jshint esversion: 6 */

/**
 * 
 */
var competitionParticipants = (function () {
	
	// private
	var compId;
	var participants = [];
		
	// public
	return {
		competition: function (competitionId) {
			compId = competitionId;
		},
		push: function (pId) {
			participants.push(pId);
		},
		remove: function (pId) {
			var index = participants.indexOf(pId);
			if (index > -1) {
				participants.splice(index, 1);
			}
		}
	};
})();

class ExtendableItemList {
	constructor() {
		this._list = [];
		this.push = function (item) {
			this._list.push(item);
		};
		this.length = function() {
			return this._list.length;
		};
		this.remove = function (item) {
			var index = this._list.indexOf(pId);
			if (index > -1) {
				this._list.splice(index, 1);
			}
		};
		this[Symbol.iterator] = function() {

			var index = 0;

		    const iterator = {
		            next() {
		                while (index < this._list.length) {
		                	return this._list[index++];
		                } 

		                return { done: true };
		            }
		        };
		    return iterator;
		};
	}
}
