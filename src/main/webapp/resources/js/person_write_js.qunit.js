
		QUnit.module("Person Class Tests", {

			beforeEach : function() {
				sessionStorage.clear();
				origRepo = repo;
				origAjax = ajax;
				repo = repoReplacement;
				ajax = ajaxReplacement;
			},
			afterEach : function() {
				repo = origRepo;
				ajax = origAjax;
			}
		});
		
		QUnit.test("Set Contact on Person", function(assert) {

			var done = assert.async();
			
			Person(1, function(p) {
				p.contacts(function (contacts) {
					var con = contacts[0];
					con.type = "testtype";
					con.value = "testvalue";
					p.updateContact(con, function(n){});
					assert.equal(1, ajaxHistory.length);
					var ajaxCall = ajaxHistory[0];
					assert.equal("put",  ajaxCall.type);
					assert.equal("testtype",  ajaxCall.object.type);
					assert.equal("testvalue",  ajaxCall.object.value);

					done();
				})
			})
		});
