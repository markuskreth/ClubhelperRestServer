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
	this.list = new ExtendableItemList();
	this.list.push(1);
	this.list.push(1);
	this.list.push(3);
	assert.equal(this.list.length(), 3);
});

QUnit.test("Inner List of ExtendableItemList invisible", function(assert) {
	this.list = new ExtendableItemList();
	this.list.push(1);
	assert.notOk(this.list._list);
});
