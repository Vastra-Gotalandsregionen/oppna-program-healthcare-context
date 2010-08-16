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

package se.vgregion.portal.auditlog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Audit logging, to be used when patient info is accessed.
 */
public class PatientAccessAuditLogger {

    private final Logger LOGGER;

    public PatientAccessAuditLogger(String loggerName) {
        LOGGER = LoggerFactory.getLogger(loggerName);
    }

    /**
     * Logging audit log parameters from AuditLogInfoContainer.
     * 
     * @param auditLogInfoContainer
     *            Map of parameter requests
     */
    public void logRequestParametersInAuditLog(AuditLogInfoContainer auditLogInfoContainer) {
        LOGGER.info(auditLogInfoContainer.toFlatLogMessage());
    }
}
