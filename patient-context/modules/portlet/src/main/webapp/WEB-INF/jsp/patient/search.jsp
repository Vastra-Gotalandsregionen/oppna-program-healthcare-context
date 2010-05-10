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

<%--
  Created by IntelliJ IDEA.
  User: david
  Date: May 10, 2010
  Time: 11:40:53 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %> 

<portlet:actionURL var="searchEvent" name="searchEvent" />

<div class="module-content">
    <h1>Search</h1>
    
    <form:form method="POST" action="${searchEvent}" commandName="patientContext">
        <table>
            <tr>
                <td>Personnummer</td>
                <td><form:input path="personNumber"/></td>
                <td><input type="submit"/></td>
            </tr>
        </table>
    </form:form>
</div>