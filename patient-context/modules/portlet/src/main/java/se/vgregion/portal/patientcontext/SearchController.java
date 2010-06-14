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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import se.vgregion.portal.patient.event.PatientContext;
import se.vgregion.portal.patient.event.PatientEvent;

import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.xml.namespace.QName;

/**
 * This action do that and that, if it has something special it is.
 *
 * @author <a href="mailto:david.rosell@redpill-linpro.com">David Rosell</a>
 */
@Controller
@RequestMapping("VIEW")
@SessionAttributes("patientContext")
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
        if (!model.containsKey("patientContext")) {
            model.addAttribute("patientContext", new PatientContext());
        }
        PatientContext patientContext = (PatientContext) model.get("patientContext");
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
     */
    @ActionMapping("searchEvent")
    public void searchEvent(@ModelAttribute("searchPatient") SearchPatientFormBean formBean,
                            @ModelAttribute("patientContext") PatientContext patientContext,
                            ActionResponse response) {
        // Log patient
        LOGGER.debug("1-search: " + formBean.getSearchText());
        LOGGER.debug("1-history: " + formBean.getHistorySearchText());
        PatientEvent patient;
        if (formBean.getHistorySearchText() == null
                || "0".equals(formBean.getHistorySearchText())) {
            // validate search patient
            if (formBean.getSearchText() == null) {
                return;
            }
            patient = new PatientEvent(formBean.getSearchText());
        } else {
            patient = new PatientEvent(formBean.getHistorySearchText());
        }


        // patient selection changed
        if (!patient.equals(patientContext.getCurrentPatient())) {
            // update patient context
            patientContext.addToHistory(patient);
            patientContext.setCurrentPatient(patient);

            // patient-context change event
            // TODO: Fire a patient-context changed to all other searchController's
            // - need IPC over pages to function
        }
        // patient change event
        QName qname = new QName("http://vgregion.se/patientcontext/events", "pctx.change");
        response.setEvent(qname, patient);
    }

    /**
     * Reset PatientEvent.
     * Remove current active PatientEvent from context.
     *
     * @param patientContext PatientContext.
     * @param response ActionResponse for event propagation.
     */
    @ActionMapping("resetEvent")
    public void resetEvent(@ModelAttribute("patientContext") PatientContext patientContext, ActionResponse response) {
        patientContext.clear();
        // event
        QName qname = new QName("http://vgregion.se/patientcontext/events", "pctx.reset");
        response.setEvent(qname, "reset");
    }
}
