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
	</script>
	<script src=<c:url value='/resources/js/person_js_tests.js' />></script>
</body>
</html>

