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

package se.vgregion.portal.patientcontext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import javax.portlet.*;
import javax.xml.namespace.QName;
import java.security.Principal;

/**
 * This action do that and that, if it has something special it is.
 *
 * @author <a href="mailto:david.rosell@redpill-linpro.com">David Rosell</a>
 */
@Controller
@RequestMapping("VIEW")
public class SearchController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

    public static final String VIEW_JSP = "search";

    @RenderMapping
    public String view(ModelMap model, RenderRequest request) throws PortletSecurityException {
        Long userId = getCurrentUserId(request);

        if (!model.containsKey("patientContext")) {
            PatientContext patientContext = new PatientContext();
            model.put("patientContext", patientContext);
        }

        return VIEW_JSP;
    }

    @ActionMapping("searchEvent")
    public void searchEvent(@ModelAttribute("patientContext") PatientContext patientContext, ActionResponse response) {
        // validator
        final String personNumber = patientContext.getPersonNumber();
        LOGGER.debug(personNumber);
        // event
        QName qname = new QName("http://vgregion.se/patientcontext/events", "pctx.personNumber");
        response.setEvent(qname, personNumber);

    }

    private Long getCurrentUserId(PortletRequest request) throws PortletSecurityException {
        // TODO: Vi borde hantera detta snyggare. Skapa en NOT_LOGGED_IN.jsp som visar ett vettigt felmeddelande
        // istället... Detta problem finns i flera controllers/portlets.

        final Principal userPrincipal = request.getUserPrincipal();
        try {
            String userIdStr = userPrincipal.getName();
            return Long.parseLong(userIdStr);
        } catch (Exception e) {
            LOGGER.warn("No user session exists.");
            throw new PortletSecurityException("No user session exists.", e);
        }
    }
}
