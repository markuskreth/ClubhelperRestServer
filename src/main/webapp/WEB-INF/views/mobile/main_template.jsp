<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<!doctype html>
<html>
<head>
<title>Clubhelper Mobile</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="http://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.css" />
<script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
<script
	src="http://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.js"></script>
</head>
<body>
	<div data-role="page">
		<div data-role="header" data-position="fixed">
			<tiles:insertAttribute name="top"></tiles:insertAttribute>
		</div>
		<!-- /header -->

		<div role="main" class="ui-content">
			<div id="page">
				<div id="content" class="width25 floatRight leftColumn">

					<div class="width75 floatLeft">
						<div class="gradient">
							<tiles:insertAttribute name="content"></tiles:insertAttribute>
						</div>
					</div>
				</div>
			</div>
		</div>
	<!-- /content -->

	<div data-role="footer" data-position="fixed">
		Copyright Markus Kreth - MTV Groﬂ-Buchholz
	</div>
	<!-- /footer -->

	</div>
	<!-- /page -->
</body>
</html>