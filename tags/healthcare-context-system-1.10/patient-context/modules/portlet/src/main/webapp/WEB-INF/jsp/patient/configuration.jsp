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
  User: David Rosell - Redpill-Linpro
  Date: May 10, 2010
  Time: 11:40:53 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<portlet:actionURL var="configureEvent" name="configureEvent" escapeXml="false"/>

<div id="module-configure" class="module" style="text-align: left">
    <h2>Ange grupp kod</h2>

    <div class="module-content">
        <div class="group-code">
            <form:form cssClass="form-general search-module" method="POST" action="${configureEvent}"
                       commandName="configure">
                <div class="text alt">
                    <form:label path="groupCode" cssClass="structural">Grupp kod</form:label>
                    <form:input path="groupCode" maxlength="13"/>
                </div>
                <div class="button-area alt">
                    <input type="submit" value="Spara"/>
                    <input type="reset" value="Avbryt"/>
                </div>
            </form:form>
        </div>
    </div>
</div>
