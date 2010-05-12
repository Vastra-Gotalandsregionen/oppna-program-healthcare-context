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

package se.vgregion.portal.patientlistner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.portlet.bind.annotation.EventMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import se.vgregion.portal.patient.event.Patient;

import javax.portlet.*;

/**
 * This action do that and that, if it has something special it is.
 *
 * @author <a href="mailto:david.rosell@redpill-linpro.com">David Rosell</a>
 */
@Controller
@RequestMapping("VIEW")
@SessionAttributes("patient")
public class ListnerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListnerController.class);

    public static final String VIEW_JSP = "view";

    @RenderMapping
    public String view(ModelMap model) throws PortletSecurityException {
        if (!model.containsKey("patient")) {
            model.addAttribute("patient", new Patient());
        }

        return VIEW_JSP;
    }

    @EventMapping("{http://vgregion.se/patientcontext/events}pctx.change")
    public void changeListner(EventRequest request, ModelMap model) {
        Event event = request.getEvent();
        Patient patient = (Patient)event.getValue();
        LOGGER.debug("Listner personnummer: "+patient.getPersonNumber());

        model.addAttribute("patient", patient);
    }


    @EventMapping("{http://vgregion.se/patientcontext/events}pctx.reset")
    public void resetListner(ModelMap model) {
        model.addAttribute("patient", new Patient());
    }
}
