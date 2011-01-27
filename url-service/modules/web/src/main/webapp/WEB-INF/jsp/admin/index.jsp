<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
	<head>
		<title>Administration - URL tjänst</title>
		
		<link rel="shortcut icon" href="http://www.vgregion.se/VGRimages/favicon.ico" type="image/x-icon" />
		
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/reset.css" type="text/css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/typography.css" type="text/css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/forms.css" type="text/css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/urlservice.css" type="text/css" />

	</head>
	<body>
		
		
		
		<h2>Redirect regler</h2>
		
		<form action="admin/redirectrules" method="post">
			<div style="height:0px; width:0px; position:absolute; overflow:hidden">
				<!-- Hack, make sure the add action happens on user pressing Enter -->
    			<input type="submit" name="add" />
			</div>
		
			<table>
				<tr>
					<th>Domän</th>
					<th>Mönster</th>
					<th>URL</th>
					<th></th>
				</tr>
				<c:forEach var="rule" items="${redirectRules}">
					<tr>
						<td>${rule.domain}</td>
						<td>${rule.pattern}</td>
						<td>${rule.url}</td>
						<td><input type="submit" value="Ta bort" name="delete-${rule.id}">
					</tr>
				</c:forEach>
					<tr>
						<td><input name="domain"></td>
						<td><input name="pattern"></td>
						<td><input name="url"></td>
						<td><input type="submit" value="Lägg till" name="add">
					</tr>
			</table>
		</form>		

		<h2>Statiska redirects</h2>

		<form action="admin/staticredirects" method="post">
			<div style="height:0px; width:0px; position:absolute; overflow:hidden">
				<!-- Hack, make sure the add action happens on user pressing Enter -->
    			<input type="submit" name="add" />
			</div>
			<table>
				<tr>
					<th>Domän</th>
					<th>Sökväg</th>
					<th>URL</th>
					<th></th>
				</tr>
				<c:forEach var="redirect" items="${staticRedirects}">
					<tr>
						<td>${redirect.domain}</td>
						<td>${redirect.pattern}</td>
						<td>${redirect.url}</td>
						<td><input type="submit" value="Ta bort" name="delete-${redirect.id}">
					</tr>
				</c:forEach>
					<tr>
						<td><input name="domain"></td>
						<td><input name="pattern"></td>
						<td><input name="url"></td>
						<td><input type="submit" value="Lägg till" name="add">
					</tr>
			</table>
		</form>		

		<h2>Applikationer</h2>

		<form action="admin/applications" method="post">
			<div style="height:0px; width:0px; position:absolute; overflow:hidden">
				<!-- Hack, make sure the add action happens on user pressing Enter -->
    			<input type="submit" name="add" />
			</div>
			<table>
				<tr>
					<th>Namn</th>
					<th>API nyckel</th>
					<th></th>
				</tr>
				<c:forEach var="application" items="${applications}">
					<tr>
						<td>${application.name}</td>
						<td>${application.apikey}</td>
						<td><input type="submit" value="Ta bort" name="delete-${redirect.id}">
					</tr>
				</c:forEach>
					<tr>
						<td><input name="name"></td>
						<td></td>
						<td><input type="submit" value="Lägg till" name="add">
					</tr>
			</table>
		</form>		
	
	</body>
</html>
