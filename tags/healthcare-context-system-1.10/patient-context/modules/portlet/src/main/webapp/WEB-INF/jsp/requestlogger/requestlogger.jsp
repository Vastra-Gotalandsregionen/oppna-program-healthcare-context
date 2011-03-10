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

<%@page session="false" contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<br/>
<table>
    <tr>
        <th>Name</th>
        <th>Value</th>
    </tr>
    <tr>
        <th colspan="2">Request Info</th>
    </tr>
    <c:forEach items="${requestInfoMap}" var="requestInfo">
        <tr>
            <td><c:out value="${requestInfo.key}"/></td>
            <td><c:out value="${requestInfo.value}"/></td>
        </tr>
    </c:forEach>
    <tr>
        <th colspan="2">Request Header</th>
    </tr>
    <c:forEach items="${requestHeaderMap}" var="requestInfo">
        <tr>
            <td><c:out value="${requestInfo.key}"/></td>
            <td><c:out value="${requestInfo.value}"/></td>
        </tr>
    </c:forEach>
    <tr>
        <th colspan="2">Request Attribute</th>
    </tr>
    <c:forEach items="${requestAttributeMap}" var="requestInfo">
        <tr>
            <td><c:out value="${requestInfo.key}"/></td>
            <td><c:out value="${requestInfo.value}"/></td>
        </tr>
    </c:forEach>
    <tr>
        <th colspan="2">Request Parameter</th>
    </tr>
    <c:forEach items="${requestParameterMap}" var="requestInfo">
        <tr>
            <td><c:out value="${requestInfo.key}"/></td>
            <td><c:out value="${requestInfo.value}"/></td>
        </tr>
    </c:forEach>
</table>