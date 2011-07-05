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
		<title>Logga in</title>
		
		<link rel="shortcut icon" href="http://www.vgregion.se/VGRimages/favicon.ico" type="image/x-icon" />
		
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/reset.css" type="text/css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/typography.css" type="text/css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/forms.css" type="text/css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/urlservice.css" type="text/css" />
		
		<script src="${pageContext.request.contextPath}/resources/js/jquery-1.4.4.js"></script> 
		<script src="${pageContext.request.contextPath}/resources/js/url-service.js"></script> 
		
	</head>
	<body>

<form name='f' action='${pageContext.request.contextPath}/j_spring_security_check' method='POST'> 
 <table> 
    <tr><td>VGR ID:</td><td><input type='text' name='j_username' value=''></td></tr> 
    <tr><td>Lösenord:</td><td><input type='password' name='j_password'/></td></tr> 
    <tr><td colspan='2'><input name="submit" type="submit" value="Logga in"/></td></tr> 
  </table> 
</form>
	</body>
</html>
