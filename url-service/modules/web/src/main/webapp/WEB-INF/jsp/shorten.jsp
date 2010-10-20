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
		<title>Förkorta länk</title>
	</head>
	<body>
		<form action=''>
			<p><label for="longurl">Länk</label><input id="longurl" name='longurl' value='${longUrl}'> <input type='submit' value='Förkorta länk'></p>
			<p><label for="slug">Nyckel (valfri)</label><input id="slug" name='slug' value='${slug}'></p>

			

		</form>

		<c:if test="${not empty shortUrl}">
			<p><a href="${shortUrl}">${shortUrl}</a></p>
		</c:if>
		<c:if test="${not empty error}">
			<p>${error}</p>
		</c:if>

		<p><a href="javascript:location.href='http://localhost:8080/?longurl='+encodeURIComponent(location.href)">Förkorta länk</a>, drag denna länk till dina bokmärken för att enkelt skapa korta länkar</p>
	</body>
</html>
