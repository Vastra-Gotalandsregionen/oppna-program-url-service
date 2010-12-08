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
		<h2>Dina favoriter</h2>
	
		<table class="bookmarks">
				<c:forEach var="bookmark" items="${bookmarks}">
					<tr>
						<td><a href="${bookmark.longUrl.url}" class="longurl">${bookmark.longUrl.url}</a>
							<c:forEach var="keyword" items="${bookmark.keywords}">
								<span class="keyword">${keyword.name}</span>
							</c:forEach>			
						</td>
						<td><a href="b/${not empty bookmark.slug ? bookmark.slug : bookmark.hash}"><img src="${pageContext.request.contextPath}/resources/img/link.png" /></a>
						</td>
						<td>
							<c:if test="${owner}">
								<a href="b/${bookmark.hash}/edit"><img src="${pageContext.request.contextPath}/resources/img/page_edit.png" /> Ändra</a>
							</c:if>
						</td>
					</tr>
				</c:forEach>
		</table>
		
		<div><a href="${pageContext.request.contextPath}/b/new"><img src="${pageContext.request.contextPath}/resources/img/page_add.png" /> Skapa ny favorit</a></div>
		<div id="bookmarklet"><a href="javascript:location.href='${domain}/b/new?longurl='+encodeURIComponent(location.href)">Förkorta länk</a>, drag denna länk till dina bokmärken för att enkelt skapa korta länkar</div>

	</body>
</html>
