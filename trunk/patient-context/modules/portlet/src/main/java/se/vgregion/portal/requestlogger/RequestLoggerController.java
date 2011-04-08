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

import com.liferay.portal.util.PortalUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

/**
 * Utility controller that log incoming request parameters.
 * Useful for tracking request header information.
 *
 * @author Anders Bergkvist
 * @author David Rosell  
 */
@Controller
@RequestMapping("VIEW")
public class RequestLoggerController {

    public static final String VIEW_JSP_URL = "requestlogger";

    @RenderMapping
    public String showSearchForm(ModelMap model, RenderRequest request, PortletPreferences preferences) {
        model.addAttribute("requestInfoMap", getRequestInfo(request));
        model.addAttribute("requestHeaderMap", getRequestHeader(request));
        model.addAttribute("requestAttributeMap", getRequestAttribure(request));
        model.addAttribute("requestParameterMap", getRequestAttribure(request));
        return VIEW_JSP_URL;
    }

    private Map<String, String> getRequestHeader(PortletRequest request) {
        Map<String, String> result = new TreeMap<String, String>();

        HttpServletRequest httpRequest = PortalUtil.getHttpServletRequest(request);
        Enumeration<String> names = httpRequest.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            result.put(name, httpRequest.getHeader(name));
        }

        return result;
    }

    private Map<String, String> getRequestParameter(PortletRequest request) {
        Map<String, String> result = new TreeMap<String, String>();

        HttpServletRequest httpRequest = PortalUtil.getHttpServletRequest(request);
        Enumeration<String> names = httpRequest.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            result.put(name, httpRequest.getParameter(name));
        }

        return result;
    }

    private Map<String, String> getRequestAttribure(PortletRequest request) {
        Map<String, String> result = new TreeMap<String, String>();

        HttpServletRequest httpRequest = PortalUtil.getHttpServletRequest(request);
        Enumeration<String> names = httpRequest.getAttributeNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            result.put(name, httpRequest.getAttribute(name).toString());
        }

        return result;
    }

    private Map<String, String> getRequestInfo(PortletRequest request) {
        Map<String,String> requestResult = new TreeMap<String, String>();

        HttpServletRequest httpRequest = PortalUtil.getHttpServletRequest(request);

        requestResult.put("RemoteUser", httpRequest.getRemoteUser());
        requestResult.put("P3P.USER_LOGIN_ID", getRemoteUserId(request));
        requestResult.put("RemoteAddr", httpRequest.getRemoteAddr());
        requestResult.put("RemoteHost", httpRequest.getRemoteHost());
        requestResult.put("RemotePort", String.valueOf(httpRequest.getRemotePort()));
        requestResult.put("AuthType", httpRequest.getAuthType());
        requestResult.put("CharacterEncoding", httpRequest.getCharacterEncoding());
        requestResult.put("ContentLength", String.valueOf(httpRequest.getContentLength()));
        requestResult.put("ContentType", httpRequest.getContentType());
        requestResult.put("ContextPath", httpRequest.getContextPath());
        requestResult.put("LocalAddr", httpRequest.getLocalAddr());
        requestResult.put("Locale", httpRequest.getLocale().toString());
        requestResult.put("LocalName", httpRequest.getLocalName());
        requestResult.put("LocalPort", String.valueOf(httpRequest.getLocalPort()));
        requestResult.put("Method", httpRequest.getMethod());
        requestResult.put("PathInfo", httpRequest.getPathInfo());
        requestResult.put("PathTranslated", httpRequest.getPathTranslated());
        requestResult.put("Protocol", httpRequest.getProtocol());
        requestResult.put("QueryString", httpRequest.getQueryString());
        requestResult.put("RequestedSessionId", httpRequest.getRequestedSessionId());
        requestResult.put("RequestURI", httpRequest.getRequestURI());
        requestResult.put("Scheme", httpRequest.getScheme());
        requestResult.put("ServerName", httpRequest.getServerName());
        requestResult.put("ServerPort", String.valueOf(httpRequest.getServerPort()));
        requestResult.put("ServletPath", httpRequest.getServletPath());

        return requestResult;
    }

    private String getRemoteUserId(PortletRequest request) {
        Map<String, ?> userInfo = (Map<String, ?>) request.getAttribute(PortletRequest.USER_INFO);
        String userId = "";
        if (userInfo != null) {
            userId = (String) userInfo.get(PortletRequest.P3PUserInfos.USER_LOGIN_ID.toString());
        }
        return userId;
    }

}