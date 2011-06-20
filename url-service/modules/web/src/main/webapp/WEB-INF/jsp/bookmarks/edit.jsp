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
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://vgregion.se/urlservice/functions" prefix="f" %>


<!DOCTYPE html>
<html>
	<head>
		<title>Spara favorit</title>
		
		<link rel="shortcut icon" href="http://www.vgregion.se/VGRimages/favicon.ico" type="image/x-icon" />
		
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/reset.css" type="text/css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/typography.css" type="text/css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/forms.css" type="text/css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/urlservice.css" type="text/css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/jquery-ui-1.8.7.custom.css" type="text/css" />

		<script src="${pageContext.request.contextPath}/resources/js/jquery-1.4.4.js"></script> 
		<script src="${pageContext.request.contextPath}/resources/js/jquery-ui-1.8.7.custom.min.js"></script> 
		<script src="${pageContext.request.contextPath}/resources/js/url-service.js"></script> 
		
	</head>
	<body>
		<h2>Spara favorit</h2>
	
		<form action='' method="post" id="editbookmarks">
			<p><label for="longurl">Länk</label>
			<c:choose>
				<c:when test="${edit}">
					<span>${longUrl}</span> 
				</c:when>
				<c:otherwise>
					<input id="longurl" name='longurl' value='${longUrl}' class="input300">
				</c:otherwise>
			</c:choose>
			</p>
			<p><label for="keywords">Nyckelord (valfri)</label>
				<input name="keywords" value="${selectedKeywords}" class="tags input300" />
			</p>
			
			<p><label for="slug">Kortord (valfri)</label><input id="slug" name='slug' value='${slug}' class="input300"></p>
			
			<p><input type='submit' value='Spara favorit'></p>
		</form>
		

		<c:if test="${not empty shortUrl}">
			<p>Din privata kortlänk: <a href="${shortUrl}">${shortUrl}</a></p>
			<p>Delad kortlänk: <a href="${globalShortUrl}">${globalShortUrl}</a></p>
			
			<div>
				<p>QR kod för kortlänk:</p>
			<img style="border: 1px solid black" src="${pageContext.request.contextPath}/qr?url=${globalShortUrl}&width=150&height=150" alt="QR kod för favoriten" />
			</p>
		</c:if>
		<c:if test="${not empty error}">
			<p>${error}</p>
		</c:if>

		<div><a href="${pageContext.request.contextPath}/u/${user.name}/b"><img src="${pageContext.request.contextPath}/resources/img/link.png" /> Se alla dina favoriter</a></div>

		<div id="bookmarklet"><a href="javascript:location.href='${domain}/b/new?longurl='+encodeURIComponent(location.href)">Förkorta länk</a>, drag denna länk till dina bokmärken för att enkelt skapa korta länkar</div>

	</body>
</html>
