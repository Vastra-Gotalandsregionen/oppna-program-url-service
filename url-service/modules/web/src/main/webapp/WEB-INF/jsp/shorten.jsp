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
		
		<link rel="stylesheet" href="/resources/css/reset.css" type="text/css" />
		<link rel="stylesheet" href="/resources/css/typography.css" type="text/css" />
		<link rel="stylesheet" href="/resources/css/forms.css" type="text/css" />
		<link rel="stylesheet" href="/resources/css/urlservice.css" type="text/css" />
	</head>
	<body>
		<form action='' method="post">
			<p><label for="longurl">Länk</label><input id="longurl" name='longurl' value='${longUrl}'> <input type='submit' value='Förkorta länk'></p>
			
			<c:if test="${authenticated}">
				<p><label for="slug">Nyckel (valfri)</label><input id="slug" name='slug' value='${slug}'></p>
			</c:if>
		</form>
		

		<c:if test="${not empty shortUrl}">
			<p><a href="${shortUrl}">${shortUrl}</a></p>
		</c:if>
		<c:if test="${not empty error}">
			<p>${error}</p>
		</c:if>

		<div id="bookmarklet"><a href="javascript:location.href='http://localhost:8080/shorten?longurl='+encodeURIComponent(location.href)">Förkorta länk</a>, drag denna länk till dina bokmärken för att enkelt skapa korta länkar</div>

		<div id="user">
			<c:choose>
				<c:when test="${authenticated}">
					Du är inloggad som ${userid}. 
					<a href="logout">Logga ut</a>
				</c:when>
			    <c:otherwise>
					<a href="spring_security_login">Logga in</a>
				</c:otherwise>
			</c:choose>
		</div>
	</body>
</html>
