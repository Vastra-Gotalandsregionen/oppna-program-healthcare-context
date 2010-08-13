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
import org.junit.Before;
import org.junit.Test;

/**
 * @author anders.bergkvist@omegapoint.se
 * 
 */
public class PatientAccessAuditLoggerTest {

    private static final String NEW_LINE = System.getProperty("line.separator");

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

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
