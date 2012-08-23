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

package se.vgregion.portal.patientlistener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.EventMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import se.vgregion.portal.concurrency.ThreadSynchronizationManager;
import se.vgregion.portal.patient.event.PatientEvent;

import javax.portlet.*;

/**
 * Example listener controller.
 * Implementation example of a controller that listens to Patient Events and presents it's content.
 *
 * @author <a href="mailto:david.rosell@redpill-linpro.com">David Rosell</a>
 */
@Controller
@RequestMapping("VIEW")
public class ListenerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListenerController.class);

    /**
     * jsp name.
     */
    public static final String VIEW_JSP = "view";

    private final ThreadSynchronizationManager threadSynchronizationManager = ThreadSynchronizationManager
            .getInstance();

    /**
     * Render view.
     * Use an empty PatientEvent object to signal that nothing should be rendered.
     * <p/>
     * Filtering has to be done in the view.
     * If we tried to do the filtering in the event processing method we would access the
     * PortletPreferences object the sending portlet, not this portlet.
     * This is a bug in Liferay or Spring Portlet MVC.
     *
     * @param request the request
     * @param model   ModelMap
     * @param prefs   the PortletPreferences
     * @return path to jsp
     */
    @RenderMapping
    public String view(RenderRequest request, ModelMap model, PortletPreferences prefs) {
        // filtering events by group-code
        groupCodeFiltering(model, prefs);

        PatientEvent patient = (PatientEvent) request.getPortletSession().getAttribute("patient");
        if (patient == null) {
            patient = new PatientEvent("", PatientEvent.DEFAULT_GROUP_CODE);
        }
        model.addAttribute("patient", patient);

        return VIEW_JSP;
    }

    /**
     * Filters the model on PatientEvent group-code.
     * <p/>
     * The listener should only resolve the PatientEvent if the event is sent by an associated
     * producer.
     * <p/>
     * ie. if the PatientEvent where sent by the PatientContext portlet on the same page, this
     * listener should show the patient. However if the patient event where sent by a
     * PatientContext portlet on another page it should not automatically go out and look up the
     * patient (in a medical system that the user didn't even know about)
     *
     * @param model ModelMap containing the PatientEvent object.
     * @param prefs PortletPreferences.
     */
    private void groupCodeFiltering(ModelMap model, PortletPreferences prefs) {
        String myGroupCode = prefs.getValue("group.code", PatientEvent.DEFAULT_GROUP_CODE);

        PatientEvent patient = (PatientEvent) model.get("patient");

        if (patient != null && !myGroupCode.equals(patient.getGroupCode())) {
            model.remove("patient");
        }
    }

    /**
     * Listener method for change PatienEvent's.
     *
     * @param request EventRequest
     * @param model   ModelMap
     */
    @EventMapping("{http://vgregion.se/patientcontext/events}pctx.change")
    public void changeListener(EventRequest request, ModelMap model) {
        Event event = request.getEvent();
        PatientEvent patient = (PatientEvent) event.getValue();

        PortletSession portletSession = request.getPortletSession();

        PatientEvent patientInSession = (PatientEvent) portletSession.getAttribute("patient");

        if (!patient.equals(patientInSession)) {
            portletSession.setAttribute("patient", patient);

            threadSynchronizationManager.notifyBlockedThreads(portletSession);
        }
    }

    /**
     * Listener method for reset PatientEvent.
     *
     * @param request request
     */
    @EventMapping("{http://vgregion.se/patientcontext/events}pctx.reset")
    public void resetListener(EventRequest request) {
        PortletSession portletSession = request.getPortletSession();
        if (portletSession.getAttribute("patient") != null) {
            portletSession.setAttribute("patient", new PatientEvent("", PatientEvent.DEFAULT_GROUP_CODE));

            threadSynchronizationManager.notifyBlockedThreads(portletSession);
        }
    }

    /**
     * See {@link ThreadSynchronizationManager#pollForChange(java.lang.String, javax.portlet.MimeResponse)}.
     *
     * @param request  the request
     * @param response the response
     */
    @ResourceMapping
    public void pollForChange(ResourceRequest request, ResourceResponse response) {
        threadSynchronizationManager.pollForChange(request.getPortletSession().getId(), response);
    }

}
