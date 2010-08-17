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

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * @author anders.bergkvist@omegapoint.se
 * 
 */
public class AuditLogInfoContainerTest {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private AuditLogInfoContainer container;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        container = new AuditLogInfoContainer();

        container.setPatientId("1234567890");
        container.setRemoteHost("RemoteHost");
        container.setRemoteIpAddress("127.0.0.1");
        container.setRemotePort(123);
        container.setRemoteUser("RemoteUser");
        container.setSearcherId("SearcherId");
    }

    /**
     * Test method for {@link se.vgregion.portal.auditlog.AuditLogInfoContainer#toFlatLogMessage()}.
     */
    @Test
    public final void testToFlatLogMessage() {

        String logMessage = container.toFlatLogMessage();

        String expected = buildExpected(AuditLogInfoContainer.AuditLogParameters.PATIENT_ID, "1234567890");
        assertTrue(logMessage.contains(expected));

        expected = buildExpected(AuditLogInfoContainer.AuditLogParameters.REMOTE_HOST, "RemoteHost");
        assertTrue(logMessage.contains(expected));

        expected = buildExpected(AuditLogInfoContainer.AuditLogParameters.REMOTE_IP, "127.0.0.1");
        assertTrue(logMessage.contains(expected));

        expected = buildExpected(AuditLogInfoContainer.AuditLogParameters.REMOTE_PORT, "123");
        assertTrue(logMessage.contains(expected));

        expected = buildExpected(AuditLogInfoContainer.AuditLogParameters.REMOTE_USER, "RemoteUser");
        assertTrue(logMessage.contains(expected));

        expected = buildExpected(AuditLogInfoContainer.AuditLogParameters.SEARCHER_ID, "SearcherId");
        assertTrue(logMessage.contains(expected));

        assertEquals(8, logMessage.split(NEW_LINE).length); // We have one line before and one after info
    }

    /**
     * Test method for {@link se.vgregion.portal.auditlog.AuditLogInfoContainer#toFlatLogMessage()}.
     */
    @Test
    public final void testToFlatLogMessageWithAdditionalParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("apa", "bpa");
        params.put("cpa", "dpa");

        container.setAdditionalAuditParameters(params);

        String logMessage = container.toFlatLogMessage();

        String expected = buildExpected(AuditLogInfoContainer.AuditLogParameters.PATIENT_ID, "1234567890");
        assertTrue(logMessage.contains(expected));

        expected = buildExpected(AuditLogInfoContainer.AuditLogParameters.REMOTE_HOST, "RemoteHost");
        assertTrue(logMessage.contains(expected));

        expected = buildExpected(AuditLogInfoContainer.AuditLogParameters.REMOTE_IP, "127.0.0.1");
        assertTrue(logMessage.contains(expected));

        expected = buildExpected(AuditLogInfoContainer.AuditLogParameters.REMOTE_PORT, "123");
        assertTrue(logMessage.contains(expected));

        expected = buildExpected(AuditLogInfoContainer.AuditLogParameters.REMOTE_USER, "RemoteUser");
        assertTrue(logMessage.contains(expected));

        expected = buildExpected(AuditLogInfoContainer.AuditLogParameters.SEARCHER_ID, "SearcherId");
        assertTrue(logMessage.contains(expected));

        assertTrue(logMessage.contains("apa = bpa"));
        assertTrue(logMessage.contains("cpa = dpa"));

        assertEquals(11, logMessage.split(NEW_LINE).length); // We have one line before and one after info
    }

    private String buildExpected(AuditLogInfoContainer.AuditLogParameters logParameter, String value) {
        StringBuilder expected = new StringBuilder();
        expected.append(logParameter.getKey());
        expected.append(" = ");
        expected.append(value);
        return expected.toString();
    }
}
