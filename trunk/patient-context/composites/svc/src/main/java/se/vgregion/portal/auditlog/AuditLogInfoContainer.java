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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.Map;
import java.util.SortedMap;

/**
 * Package private class containing data related to patient info
 * access audit logging.
 * It defines a base set of audit information that can be extended
 * by providing data in a Map.
 * It can be used straight out of the box, but can also be extended
 * to add this functionality.
 * e.g se.vgregion.portal.pli.logging.PliAuditLogger
 * 
 * @author hangyl
 * 
 */
public final class AuditLogInfoContainer {

    private static final String NEW_LINE = System.getProperty("line.separator");

    public enum AuditLogParameters {
        SEARCHER_ID("searcherId"), PATIENT_ID("pid"), REMOTE_HOST("remoteHost"), REMOTE_IP("remoteIp"), REMOTE_PORT(
                "remotePort"), REMOTE_USER("remoteUser");

        private String key;

        private AuditLogParameters(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    AuditLogInfoContainer() {
    }

    private String remoteIpAddress; // The IP-address of the client that sent the request
    private String remoteHost; // The fully qualified name of the client that sent the request,
    // or the IP address of the client if the name cannot be determined
    private int remotePort; // Port of the remote client
    private String remoteUser; // The login of the user making this request e.g. wpsadmin
    private String patientId; // Patient id, e.g. patientSSN
    private String searcherId; // User id, not necessarily same as remoteUser (e.g. Liferay userId)

    private SortedMap<String, String> additionalAuditParameters;

    public void setRemoteIpAddress(String remoteIpAddress) {
        this.remoteIpAddress = remoteIpAddress;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void setSearcherId(String searcherId) {
        this.searcherId = searcherId;
    }

    public void setAdditionalAuditParameters(SortedMap<String, String> additionalAuditParameters) {
        this.additionalAuditParameters = additionalAuditParameters;
    }

    /**
     * @return The IP-address of the client that sent the request e.g. 140.166.117.131
     */
    public String getRemoteIpAddress() {
        return remoteIpAddress;
    }

    /**
     * @return The fully qualified name of the client that sent the request, or the IP address of the client if the
     *         name cannot be determined e.g. hansgyllensten.knowit.local (or 140.166.117.131)
     */
    public String getRemoteHost() {
        return remoteHost;
    }

    /**
     * @return Port of the remote client e.g. 3652
     */
    public int getRemotePort() {
        return remotePort;
    }

    /**
     * @return The login of the user making this request e.g. hangyl
     */
    public String getRemoteUser() {
        return remoteUser;
    }

    /**
     * @return The patientId, e.g. a SSN
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * @return The searcherId, same as remoteUser or e.g. Liferay userId
     */
    public String getSearcherId() {
        return searcherId;
    }

    /**
     * @return the additionalAuditParameters, any additional parameters that should be logged
     */
    public Map<String, String> getAdditionalAuditParameters() {
        return additionalAuditParameters;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    /**
     * Construct "flat" log message of current info.
     * 
     * @return A complete flat log message.
     */
    public String toFlatLogMessage() {
        StringBuilder sb = new StringBuilder();

        sb.append("AuditLogInfoParameters: ").append(NEW_LINE);

        sb.append(messageLine(AuditLogParameters.PATIENT_ID.getKey(), getPatientId()));
        sb.append(messageLine(AuditLogParameters.SEARCHER_ID.getKey(), getSearcherId()));
        sb.append(messageLine(AuditLogParameters.REMOTE_HOST.getKey(), getRemoteHost()));
        sb.append(messageLine(AuditLogParameters.REMOTE_IP.getKey(), getRemoteIpAddress()));
        sb.append(messageLine(AuditLogParameters.REMOTE_PORT.getKey(), String.valueOf(getRemotePort())));
        sb.append(messageLine(AuditLogParameters.REMOTE_USER.getKey(), getRemoteUser()));

        if (additionalAuditParameters != null) {
            sb.append(NEW_LINE);
            for (Map.Entry<String, String> entry : additionalAuditParameters.entrySet()) {
                sb.append(messageLine(entry.getKey(), entry.getValue()));
            }
        }

        sb.append("*************************************************").append(NEW_LINE);

        return sb.toString();
    }

    private String messageLine(String key, String value) {
        StringBuilder sb = new StringBuilder();
        sb.append("  ").append(key).append(" = ").append(value).append(";").append(NEW_LINE);
        return sb.toString();
    }
}
