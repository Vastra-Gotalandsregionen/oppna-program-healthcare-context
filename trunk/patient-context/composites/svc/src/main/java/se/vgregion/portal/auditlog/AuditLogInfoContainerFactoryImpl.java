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

import java.util.Map;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;

import se.vgr.ldapservice.LdapService;
import se.vgr.ldapservice.LdapUser;

/**
 * 
 * @author Hans Gyllensten, VGR-IT (vgrId: hangy2)
 * 
 */
@Component
public class AuditLogInfoContainerFactoryImpl implements AuditLogInfoContainerFactory {
    private RequestResponseConverter requestResponseConverter;

    private LdapService ldapService = null;

    @Autowired
    public AuditLogInfoContainerFactoryImpl(LdapService ldapService) {
        this.ldapService = ldapService;
    }

    /**
     * {@inheritDoc}
     */
    @Required
    @Autowired
    public void setRequestResponseConverter(RequestResponseConverter requestResponseConverter) {
        this.requestResponseConverter = requestResponseConverter;
    }

    /**
     * {@inheritDoc}
     */
    public AuditLogInfoContainer getAuditLogInfoContainer(String patientId, PortletRequest portletRequest) {
        HttpServletRequest httpServletRequest = requestResponseConverter.getHttpServletRequest(portletRequest);
        AuditLogInfoContainer container = new AuditLogInfoContainer();

        container.setPatientId(patientId);

        String searcherId = getUserId(portletRequest);
        container.setSearcherId(searcherId);

        container.setRemoteIpAddress(httpServletRequest.getRemoteAddr());
        container.setRemoteHost(httpServletRequest.getRemoteHost());
        container.setRemotePort(httpServletRequest.getRemotePort());

        String remoteUser = getUserLoginId(portletRequest);
        container.setRemoteUser(remoteUser);

        return container;
    }

    /**
     * @param request
     * @return user id from LDAP if available, else liferay user id
     */
    private String getUserId(PortletRequest request) {
        LdapUser ldapUser = null;
        String loggedInUser = getUserLoginId(request);
        try {
            ldapUser = ldapService.getLdapUserByUid(loggedInUser);
        } catch (Exception e) {
            // Do nothing, we'll use Liferay user id instead
        }

        if (ldapUser != null) {
            loggedInUser = ldapUser.getAttributeValue("cn");
        } else {
            loggedInUser = "[LiferayUser:]" + loggedInUser;
        }

        return loggedInUser;
    }

    @SuppressWarnings("unchecked")
    private String getUserLoginId(PortletRequest portletRequest) {
        Map<String, ?> userInfo = (Map<String, ?>) portletRequest.getAttribute(PortletRequest.USER_INFO);
        return (String) ((userInfo != null) ? userInfo.get(PortletRequest.P3PUserInfos.USER_LOGIN_ID.toString())
                : "");
    }
}
