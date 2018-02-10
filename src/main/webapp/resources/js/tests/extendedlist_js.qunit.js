/*jshint esversion: 6 */

/**
 * 
 */
QUnit.module("ExtendableItemList Tests", {
//
//	setup : function() {
//		this.list = new ExtendableItemList();
//	},
//	afterEach : function() {
//	}
});

QUnit.test("Add items to ExtendableItemList", function(assert) {
	var list = new ExtendableItemList();
	list.push(1);
	list.push(1);
	list.push(3);
	assert.equal(list.length(), 3);
});

QUnit.test("Inner List of ExtendableItemList invisible", function(assert) {
	var list = new ExtendableItemList();
	list.push(1);
	assert.notOk(list.__list);
	assert.notOk(list._list);
	assert.notOk(list.list);
});

QUnit.test("Iterate in for loop ExtendableItemList", function(assert) {
	var list = new ExtendableItemList();
	list.push(1);
	list.push(2);
	list.push(3);
	var sum = 0;
	for(const item of list) {
		sum += item;
	}

	assert.equal(sum, 6);
});

QUnit.test("competitionParticipants use competitionId, serialize", function(assert) {
	assert.ok(competitionParticipants);
	competitionParticipants.competition("comp12345Id");
	competitionParticipants.push(1);
	competitionParticipants.push(2);
//	var serialized = JSON.stringify(competitionParticipants);
//	assert.equal(serialized, "{\"competitionId\":\"comp12345Id\",\"list\":[1,2]}");
	
});

QUnit.test("competitionParticipants iterate all elements", function(assert) {
	assert.ok(competitionParticipants);
	competitionParticipants.competition("comp12345Id");
	competitionParticipants.push(10);
	competitionParticipants.push(20);
	var items = [];
	for(var item of competitionParticipants) {
		items.push(item);
	}

	assert.equal(items.length, 2);
	assert.equal(items[0], {"competitionId":"comp12345Id", "personId":10});
	assert.equal(items[1], {"competitionId":"comp12345Id", "personId":20});
});