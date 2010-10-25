<%--

    Copyright 2010 Västra Götalandsregionen

      This library is free software; you can redistribute it and/or modify
      it under the terms of version 2.1 of the GNU Lesser General Public
      License as published by the Free Software Foundation.

      This library is distributed in the hope that it will be useful,
      but WITHOUT ANY WARRANTY; without even the implied warranty of
      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
      GNU Lesser General Public License for more details.

      You should have received a copy of the GNU Lesser General Public
      License along with this library; if not, write to the
      Free Software Foundation, Inc., 59 Temple Place, Suite 330,
      Boston, MA 02111-1307  USA


--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
	<head>
		<title>Administration - URL tjänst</title>
	</head>
	<body>
		
		
		
		<h2>Redirect regler</h2>
		
		${error}
		
		<form action="admin/redirectrules" method="post">
			<input type="hidden" name="type" value="redirectRule">
			<table>
				<tr>
					<th>Mönster</th>
					<th>URL</th>
					<th></th>
				</tr>
				<c:forEach var="rule" items="${redirectRules}">
					<tr>
						<td>${rule.pattern}</td>
						<td>${rule.url}</td>
						<td><input type="submit" value="Ta bort" name="delete-${rule.id}">
					</tr>
				</c:forEach>
					<tr>
						<td><input name="pattern"></td>
						<td><input name="url"></td>
						<td><input type="submit" value="Lägg till" name="add">
					</tr>
			</table>
		</form>		
	
	</body>
</html>
