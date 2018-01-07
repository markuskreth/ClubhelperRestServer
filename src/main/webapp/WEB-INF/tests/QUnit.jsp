<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
	<head>
	<meta charset="UTF-8">
	<title>Person Class Tests</title>
	<link rel="stylesheet"
		href=<c:url value='/resources/css/qunit.css' />>
	</head>
	<body>
		<div id="qunit"></div>
		<div id="qunit-fixture"></div>
		<script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
		<script
			src="http://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.js"></script>
		<script src=<c:url value='/resources/js/libs/qunit.js' />></script>
		<script charset="utf-8"
			src=<c:url value='/resources/js/ajax_base.js' />></script>
		<script charset="utf-8" src=<c:url value='/resources/js/storage.js' />></script>
		<script src=<c:url value='/resources/js/group_business.js' />></script>
		<script src=<c:url value='/resources/js/person_detail_business.js' />></script>
		<script src=<c:url value='/resources/js/person.js' />></script>
		<script src=<c:url value='/resources/js/competition_business.js' />></script>
		<script src=<c:url value='/resources/js/libs/log4javascript.js' />></script>
		<script
			src=<c:url value='/resources/js/libs/moment-with-locales.min.js' />></script>
		<script>
			var baseUrl = location.protocol + '//' + location.host
					+ <c:url value='/' />;
			var log = log4javascript.getDefaultLogger();
			log4javascript.setEnabled(false);
		</script>
		<script src=<c:url value='/resources/js/tests/testdata.js' />></script>
		<script src=<c:url value='/resources/js/tests/storage_js.qunit.js' />></script>
		<script src=<c:url value='/resources/js/tests/extendedlist_js.qunit.js' />></script>
		<script src=<c:url value='/resources/js/tests/person_js.qunit.js' />></script>
		<script
			src=<c:url value='/resources/js/tests/person_write_js.qunit.js' />></script>
	</body>
</html>

