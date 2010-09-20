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

package se.vgregion.portal.requestlogger;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.util.PortalUtil;

/**
 * View controller
 */
@Controller
@RequestMapping("VIEW")
public class RequestLoggerController {

    public static final String VIEW_JSP_URL = "requestlogger";

    @RenderMapping
    public String showSearchForm(ModelMap model, RenderRequest request, PortletPreferences preferences) {
        Map<String, String> requestInfo = getRequestInfo(request);
        model.addAttribute("requestInfoMap", requestInfo);
        return VIEW_JSP_URL;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getRequestInfo(PortletRequest request) {
        Map<String, String> result = new HashMap<String, String>();

        HttpServletRequest httpRequest = PortalUtil.getHttpServletRequest(request);
        Enumeration<String> names = httpRequest.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            result.put(name, httpRequest.getHeader(name));
        }

        result.put("RemoteUser", httpRequest.getRemoteUser());
        result.put("RemoteAddr", httpRequest.getRemoteAddr());
        result.put("RemoteHost", httpRequest.getRemoteHost());
        result.put("RemotePort", String.valueOf(httpRequest.getRemotePort()));

        return result;
    }
}