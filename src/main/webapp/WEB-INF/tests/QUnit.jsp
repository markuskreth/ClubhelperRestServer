<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Person Class Tests</title>
<link rel="stylesheet"
	href=<c:url value='/resources/css/qunit-2.1.1.css' />>
</head>
<body>
	<div id="qunit"></div>
	<div id="qunit-fixture"></div>
	<script src=<c:url value='/resources/js/jquery-3.1.1.min.js' />></script>
	<script src=<c:url value='/resources/js/qunit-2.1.1.js' />></script>
	<script src=<c:url value='/resources/js/person.js' />></script>
	<script src=<c:url value='/resources/js/testdata.js' />></script>
	<script src=<c:url value='/resources/js/moment-with-locales.min.js' />></script>
	<script>
		var baseUrl = location.protocol + '//' + location.host
				+ <c:url value='/' />;

		QUnit.module("Person Class Tests", {

			beforeEach : function() {
				sessionStorage.clear();
				origRepo = repo;
				repo = repoReplacement;
			},
			afterEach : function() {
				repo = origRepo
			}
		});

		QUnit.test("Creating Person from Ajax result", function(assert) {

			var person1 = new PersonInstance(1, personMarkusResponse);
			assert.equal("Markus", person1.prename);
			assert.equal("Kreth", person1.surname);
			assert.equal("43 Jahre", person1.age());
			assert.equal("21.08.1973", person1.birthday());
			assert.equal(1, person1.personId);
		});

		QUnit.test("Creating Person from Factory Method", function(assert) {

			var done = assert.async();

			Person(1, function(person1) {
				assert.equal("Markus", person1.prename);
				assert.equal("Kreth", person1.surname);
				assert.equal("43 Jahre", person1.age());
				assert.equal("21.08.1973", person1.birthday());
				assert.equal(1, person1.personId);
				done();
			});

		});

		QUnit.test("Contact Function Test", function(assert) {

			var person1 = new PersonInstance(1, personMarkusResponse);
			person1.setContacts(contactsMarkusResponse);

			person1.contacts(function(items) {
				assert.ok(items);
				assert.equal(3, items.length);

				var item = items[0];
				assert.equal("Telefon", item.type);
				assert.equal("+495112618291", item.value);

				item = items[1];
				assert.equal("Email", item.type);
				assert.equal("markus.kreth@web.de", item.value);

				item = items[2];
				assert.equal("Mobile", item.type);
				assert.equal("01742521286", item.value);
			});
		});

		QUnit.test("Repo Replacement Test", function(assert) {

			var testfunction = function(items) {

				assert.ok(items);
				assert.equal(3, items.length);

				var item = items[0];
				assert.equal("Telefon", item.type);
				assert.equal("+495112618291", item.value);

				item = items[1];
				assert.equal("Email", item.type);
				assert.equal("markus.kreth@web.de", item.value);

				item = items[2];
				assert.equal("Mobile", item.type);
				assert.equal("01742521286", item.value);

			};

			repo("xxxx/contact/for/1", testfunction);
		});

		QUnit.test("Contact Request Test", function(assert) {

			var person1 = new PersonInstance(1, personMarkusResponse);

			var done = assert.async();

			person1.contacts(function(items) {

				assert.ok(items);
				assert.equal(3, items.length);

				var item = items[0];
				assert.equal("Telefon", item.type);
				assert.equal("+495112618291", item.value);

				item = items[1];
				assert.equal("Email", item.type);
				assert.equal("markus.kreth@web.de", item.value);

				item = items[2];
				assert.equal("Mobile", item.type);
				assert.equal("01742521286", item.value);

				done();
			});
		});

		QUnit.test("Reload Person with contacts from SessionStorage", function(assert) {

			var done = assert.async();
			
			Person(1, function(person0) {

				person0.contacts(function(items) {
					// nothing to do - checked in other tests
					
					// deactivating internet repo
					repo = null;
					
					// now from Cache					
					Person(1, function(person1) {

						assert.equal("Markus", person1.prename, "prename found");
						assert.equal("Kreth", person1.surname, "surname found");
						assert.equal("43 Jahre", person1.age(), "age correct");
						assert.equal("21.08.1973", person1.birthday(), "birthday shown.");
						assert.equal(1, person1.personId);
						done();
					});

				});
			});

		});

		QUnit.test("Relative REquest Test", function(assert) {

			var person1 = new PersonInstance(1, personMarkusResponse);

			var done = assert.async();

			person1.relatives(function(relPerson) {
				
				assert.ok(relPerson);
				var expected = new PersonInstance(person51Response.id, person51Response);
				assert.equal(expected.personId, relPerson.personId);
				assert.equal(expected.prename, relPerson.prename);
				assert.equal(expected.surname, relPerson.surname);
				assert.equal(expected.age(), relPerson.age());
				assert.equal(expected.birthday(), relPerson.birthday());

				relPerson.contacts(function(items) {
					assert.ok(items);

					assert.equal(3, items.length);

					var item = items[0];
					assert.equal("Telefon", item.type);
					assert.equal("+49000000", item.value);

					done();
				});
			});
		});
		
	</script>
</body>
</html>

