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
		<title>Favoriter</title>
		
		<link rel="shortcut icon" href="http://www.vgregion.se/VGRimages/favicon.ico" type="image/x-icon" />
		
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/reset.css" type="text/css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/typography.css" type="text/css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/forms.css" type="text/css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/urlservice.css" type="text/css" />
		
		<script src="${pageContext.request.contextPath}/resources/js/jquery-1.4.4.js"></script> 
		<script src="${pageContext.request.contextPath}/resources/js/url-service.js"></script> 
		
	</head>
	<body>
		<h2>Allas favoriter</h2>
	
		<p>Här kan man spara sina favoritlänkar och även se de länkar som andra sparat som sina favoriter</p>
		
		<h4>Följande användare har sparat sina länkar</h4>
		<ul id="users">
			<c:forEach var="user" items="${users}">
				<li><a href="${pageContext.request.contextPath}/u/${user.name}/b">${user.name}</a></li>
			</c:forEach>
		</ul>


		<div><a href="${pageContext.request.contextPath}/b/new"><img src="${pageContext.request.contextPath}/resources/img/page_add.png" /> Skapa ny favorit</a></div>
	</body>
</html>
