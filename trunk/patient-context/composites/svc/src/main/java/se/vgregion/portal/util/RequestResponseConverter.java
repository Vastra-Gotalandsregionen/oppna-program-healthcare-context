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
 * Helper methods to convert things between portal and servlet container. 
 */
package se.vgregion.portal.util;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interface whose implementations convert from {@link PortletRequest}s and {@link PortletResponse}s to the
 * corresponding {@link HttpServletRequest} and {@link HttpServletResponse}, respectively, from which they originate.
 *
 * @author Anders Asplund - Logica
 * @author Hans Gyllensten, VGR-IT (vgrId: hangy2)
 */
public interface RequestResponseConverter {

    /**
     * Converts a PortletResonse to a HttpServletResponse.
     *
     * @param response the response
     * @return HttpServletResponse
     */
    HttpServletResponse getHttpServletResponse(PortletResponse response);

    /**
     * Converts a PortletRequest to a HttpServletRequest.
     *
     * @param request the request
     * @return HttpServletRequest
     */
    HttpServletRequest getHttpServletRequest(PortletRequest request);

}