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
import org.springframework.web.portlet.bind.annotation.EventMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import se.vgregion.portal.patient.event.PatientEvent;

import javax.portlet.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Example listner controller.
 * Implementation example of a controller that listens to Patient Events and presents it's content.
 *
 * @author <a href="mailto:david.rosell@redpill-linpro.com">David Rosell</a>
 */
@Controller
@RequestMapping("VIEW")
public class ListnerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListnerController.class);

    /**
     * jsp name.
     */
    public static final String VIEW_JSP = "view";

    private final Map<String, CountDownLatch> blockedThreads = Collections.synchronizedMap(new HashMap<String, CountDownLatch>());

    /**
     * Render view.
     * Use an empty PatientEvent object to signal that nothing should be rendered.
     * <p/>
     * Filtering has to be done in the view.
     * If we tried to do the filtering in the event processing method we would access the
     * PortletPreferences object the sending portlet, not this portlet.
     * This is a bug in Liferay or Spring Portlet MVC.
     *
     * @param model ModelMap
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
     * listner should show the patient. However if the patient event where sent by a
     * PatientContext portlet on another page it should not automatically go out and look up the
     * patient (in a medical system that the user didn't even know about)
     *
     * @param model ModelMap containing the PatientEvent object.
     * @param prefs PortletPreferences.
     */
    private void groupCodeFiltering(ModelMap model, PortletPreferences prefs) {
        String myGroupCode = prefs.getValue("group.code", PatientEvent.DEFAULT_GROUP_CODE);
        LOGGER.debug("View Listner GroupCode: " + myGroupCode);

        PatientEvent patient = (PatientEvent) model.get("patient");
        LOGGER.debug("Event GroupCode: " + ((patient == null) ? "empty" : patient.getGroupCode()));

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
    public void changeListner(EventRequest request, ModelMap model) {
        Event event = request.getEvent();
        PatientEvent patient = (PatientEvent) event.getValue();

        LOGGER.debug("Listner personnummer input: " + patient.getInputText());
        if (patient.getPersonNummer() != null) {
            LOGGER.debug("Listner personnummer short: " + patient.getPersonNummer().getShort());
            LOGGER.debug("Listner personnummer normal: " + patient.getPersonNummer().getNormal());
            LOGGER.debug("Listner personnummer full: " + patient.getPersonNummer().getFull());
        }

        PortletSession portletSession = request.getPortletSession();

        PatientEvent patientInSession = (PatientEvent) portletSession.getAttribute("patient");

        if (!patient.equals(patientInSession)) {
            portletSession.setAttribute("patient", patient);

            String sessionId = portletSession.getId();
            CountDownLatch countDownLatch = blockedThreads.get(sessionId);
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
        }
    }

    /**
     * Listener method for reset PatientEvent.
     *
     * @param request request
     */
    @EventMapping("{http://vgregion.se/patientcontext/events}pctx.reset")
    public void resetListner(EventRequest request) {
        request.getPortletSession().setAttribute("patient", new PatientEvent("", PatientEvent.DEFAULT_GROUP_CODE));
    }

    @ResourceMapping
    public void pollForChange(ResourceRequest request, ResourceResponse response) {
        String sessionId = request.getPortletSession().getId();
        boolean shouldUpdate = waitForUpdate(sessionId);
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            if (shouldUpdate) {
                writer.append("update=true");
            } else {
                writer.append("update=false");
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            writer.close();
        }
    }

    private boolean waitForUpdate(String sessionId) {

        // A way to wait for a certain event.
        try {
            CountDownLatch countDownLatch;
            if (blockedThreads.containsKey(sessionId)) {
                countDownLatch = blockedThreads.get(sessionId);
            } else {
                countDownLatch = new CountDownLatch(1);
                blockedThreads.put(sessionId, countDownLatch);
            }
            countDownLatch.await(5, TimeUnit.MINUTES);
            return true;
        } catch (InterruptedException e) {
            LOGGER.info(e.getMessage(), e);
        } finally {
            blockedThreads.remove(sessionId);
        }
        return false;
    }
}
