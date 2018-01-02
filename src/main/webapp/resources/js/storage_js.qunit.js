/**
 * 
 */
var personStorage;

QUnit.module("Storage Tests", {

	beforeEach : function() {
		origRepo = repo;
		origAjax = ajax;
		repo = repoReplacement;
		ajax = ajaxReplacement;
		personStorage = new Storage("person");
		personStorage.clearAll();
	},
	afterEach : function() {
		repo = origRepo;
		ajax = origAjax;
		personStorage.clearAll();
	}
});

QUnit.test("Determining property delegation", function(assert) {

	assert.equal(0, personStorage.length());
	personStorage.set({prename:"Test"}, 1);
	assert.equal(1, personStorage.length());
	
});

QUnit.test("Test iteration", function(assert) {

	personStorage.set({prename:"Test1"}, 1);
	personStorage.set({prename:"Test2"}, 2);
	personStorage.set({prename:"Test3"}, 3);
	assert.equal(3, personStorage.length());
	var persons = [];
	for(const p of personStorage) {
		persons.push(p);
	}
	assert.equal(persons.length, 3);
	assert.equal(persons[2].prename, "Test1");
	assert.equal(persons[1].prename, "Test2");
	assert.equal(persons[0].prename, "Test3");
	
});

