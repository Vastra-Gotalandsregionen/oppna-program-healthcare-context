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
  Time: 2:40:12 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<div class="module-content">
    <h1>Patient</h1>

    <c:if test="${patient.inputText != ''}">
        <p>Input: ${patient.inputText}</p>

        <h2>Person nummer:</h2>
        <c:if test="${patient.personNummer != null}">
            <p>Short-format: ${patient.personNummer.short} </p>

            <p>Normal-format: ${patient.personNummer.normal} </p>

            <p>Full-format: ${patient.personNummer.full} </p>

            <h2>Validation:</h2>

            <p>Gender: ${patient.personNummer.gender}</p>

            <p class="${patient.personNummer.checkNumberValid ? '' : 'error'}">
                Is checknumber valid: ${patient.personNummer.checkNumberValid}
                <span class="${patient.personNummer.checkNumberValid ?  'hidden' : ''}">
                    (It ought to be ${patient.personNummer.calculatedCheckNumber})
                </span>
            </p>

            <p class="${patient.personNummer.monthValid ? '' : 'error'}">Is month
                valid: ${patient.personNummer.monthValid}</p>

            <p class="${patient.personNummer.dayValid ? '' : 'error'}">Is day
                valid: ${patient.personNummer.dayValid}</p>
        </c:if>
        <c:if test="${patient.personNummer == null}">
            Input is not a personnummer
        </c:if>
    </c:if>
</div>

<portlet:resourceURL var="resourceUrl" escapeXml="false"/>

<script type="text/javascript" src="<%= request.getContextPath() %>/js/listener.js"></script>

<liferay-util:html-bottom>
    <script type="text/javascript">
        AUI().ready(function (A) {
            pollForNewPatient(A, '<%= resourceUrl %>');
        });
    </script>
</liferay-util:html-bottom>