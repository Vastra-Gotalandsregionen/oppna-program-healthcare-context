/**
 * Copyright 2010 Västra Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 *
 */

/**
 * 
 */
package se.vgregion.portal.auditlog;

import static org.junit.Assert.*;

import java.io.StringWriter;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.junit.Test;

/**
 * @author anders.bergkvist@omegapoint.se
 * 
 */
public class PatientAccessAuditLoggerTest {

    private static final String NEW_LINE = System.getProperty("line.separator");

    /**
     * Test method for
     * {@link se.vgregion.portal.auditlog.PatientAccessAuditLogger#logRequestParametersInAuditLog(se.vgregion.portal.auditlog.AuditLogInfoContainer)}
     * .
     */
    @Test
    public final void testLogRequestParametersInAuditLog() {
        String loggerName = "logger.name";

        StringWriter logWriter = getLoggerView(loggerName, Level.INFO);

        PatientAccessAuditLogger logger = new PatientAccessAuditLogger(loggerName);

        AuditLogInfoContainer container = new AuditLogInfoContainer();
        container.setPatientId("1234567890");
        container.setRemoteHost("RemoteHost");
        container.setRemoteIpAddress("127.0.0.1");
        container.setRemotePort(123);
        container.setRemoteUser("RemoteUser");
        container.setSearcherId("SearcherId");

        logger.logRequestParametersInAuditLog(container);

        String logContent = logWriter.toString();

        assertTrue(logContent.startsWith("INFO - AuditLogInfoParameters:"));
        assertTrue(logContent.endsWith("*************************************************" + NEW_LINE + NEW_LINE));
    }

    private StringWriter getLoggerView(String loggerName, Level logLevel) {
        Logger logger = Logger.getLogger(loggerName);
        logger.setLevel(logLevel);
        final StringWriter writer = new StringWriter();
        Appender appender = new WriterAppender(new SimpleLayout(), writer);
        logger.addAppender(appender);
        return writer;
    }

}
