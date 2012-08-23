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
import org.springframework.web.portlet.bind.annotation.EventMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import se.vgregion.portal.patient.event.PatientContext;
import se.vgregion.portal.patient.event.PatientEvent;

import javax.portlet.*;
import javax.xml.namespace.QName;

/**
 * Search view controller.
 * Handles:
 *    Coordinates user input and validation.
 *    Sending out search events.
 *
 * @author <a href="mailto:david.rosell@redpill-linpro.com">David Rosell</a>
 */
@Controller
@RequestMapping("VIEW")
public class SearchController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

    /**
     * jsp name.
     */
    public static final String VIEW_JSP = "search";

    /**
     * Render view.
     *
     * @param model ModelMap
     * @param request RenderRequest
     * @return path to view jsp
     */
    @RenderMapping
    public String view(ModelMap model, RenderRequest request) {
        PortletSession session = request.getPortletSession();
        if (session.getAttribute("patientContext") == null) {
            session.setAttribute("patientContext", new PatientContext());
        }
        PatientContext patientContext = (PatientContext) session.getAttribute("patientContext");
        SearchPatientFormBean formBean = new SearchPatientFormBean();
        if (patientContext.getCurrentPatient() != null) {
            PatientEvent patient = patientContext.getCurrentPatient();
            if (patient.getPersonNummer() != null) {
                formBean.setSearchText(patient.getPersonNummer().getNormal());
            } else {
                formBean.setSearchText(patient.getInputText());
            }
        }

        model.addAttribute("searchPatient", formBean);

        if (LOGGER.isDebugEnabled()) {
            for (PatientEvent history : patientContext.getPatientHistory()) {
                LOGGER.debug(history.toString());
            }
        }

        return VIEW_JSP;
    }

    /**
     * Patient search event.
     *
     * @param formBean FormBean from view
     * @param patientContext PatientContext
     * @param response ActionResponse for event propagation
     * @param prefs PortletPreferences
     */
    @ActionMapping("searchEvent")
    public void searchEvent(@ModelAttribute("searchPatient") SearchPatientFormBean formBean,
                            @ModelAttribute("patientContext") PatientContext patientContext,
                            ActionResponse response, PortletPreferences prefs) {
        String groupCode = prefs.getValue("group.code", PatientEvent.DEFAULT_GROUP_CODE);

        // Log patient
        PatientEvent patient;
        if (formBean.getHistorySearchText() == null
                || "0".equals(formBean.getHistorySearchText())) {
            // validate search patient
            if (formBean.getSearchText() == null) {
                return;
            }
            patient = new PatientEvent(formBean.getSearchText(), groupCode);
        } else {
            patient = new PatientEvent(formBean.getHistorySearchText(), groupCode);
        }


        // patient selection changed
        if (!patient.equals(patientContext.getCurrentPatient())) {
            // update patient context
            patientContext.addToHistory(patient);
            patientContext.setCurrentPatient(patient);
        }
        // patient change event
        QName qname = new QName("http://vgregion.se/patientcontext/events", "pctx.change");
        response.setEvent(qname, patient);
    }

    /**
     * Listener method for change PatienEvent's.
     *
     * @param request EventRequest
     * @param model ModelMap
     */
    @EventMapping("{http://vgregion.se/patientcontext/events}pctx.change")
    public void changeListener(EventRequest request, ModelMap model) {
        Event event = request.getEvent();
        PatientEvent patient = (PatientEvent) event.getValue();

        PortletSession session = request.getPortletSession();

        if (session.getAttribute("patientContext") == null) {
            session.setAttribute("patientContext", new PatientContext());
        }
        PatientContext patientContext = (PatientContext) session.getAttribute("patientContext");

        // patient selection changed
        if (!patient.equals(patientContext.getCurrentPatient())) {
            // update patient context
            patientContext.addToHistory(patient);
            patientContext.setCurrentPatient(patient);
        }
    }

    /**
     * Reset PatientEvent.
     * Remove current active PatientEvent from context.
     *
     * @param patientContext PatientContext.
     * @param response ActionResponse for event propagation.
     */
    @ActionMapping("resetEvent")
    public void resetEvent(@ModelAttribute("patientContext") PatientContext patientContext,
                           ActionResponse response) {
        patientContext.clear();
        // event
        QName qname = new QName("http://vgregion.se/patientcontext/events", "pctx.reset");
        response.setEvent(qname, "reset");
    }
}
