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

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.portlet.MockEvent;
import org.springframework.mock.web.portlet.MockEventRequest;
import org.springframework.mock.web.portlet.MockPortletPreferences;
import org.springframework.ui.ModelMap;
import se.vgregion.portal.patient.event.PatientEvent;

import javax.portlet.Event;
import javax.portlet.EventRequest;

import static org.junit.Assert.*;

/**
 * This action do that and that, if it has something special it is.
 *
 * @author <a href="mailto:david.rosell@redpill-linpro.com">David Rosell</a>
 */
public class ListnerControllerTest {
    private ListnerController controller;
    private ModelMap model;

    @Before
    public void setUp() throws Exception {
        controller = new ListnerController();
        model = new ModelMap();
    }

    @Test
    public void testViewFirstAccess() throws Exception {
        MockPortletPreferences mockPrefs = new MockPortletPreferences();

        String result = controller.view(model, mockPrefs);

        assertEquals(ListnerController.VIEW_JSP, result);
        assertTrue(model.containsKey("patient"));
        PatientEvent patient = (PatientEvent) model.get("patient");
        assertEquals("", patient.getInputText());
        assertNull(patient.getPersonNummer());
    }

    @Test
    public void testViewRevisit() throws Exception {
        MockPortletPreferences mockPrefs = new MockPortletPreferences();

        PatientEvent pe = new PatientEvent("19121212-1212", PatientEvent.DEFAULT_GROUP_CODE);
        model.addAttribute("patient", pe);

        controller.view(model, mockPrefs);

        PatientEvent patient = (PatientEvent) model.get("patient");
        assertEquals(pe, patient);
    }

    @Test
    public void testChangeListnerPatientEventWithPersonNumber() throws Exception {
        PatientEvent pe = new PatientEvent("19121212-1212", PatientEvent.DEFAULT_GROUP_CODE);
        Event mockEvent = new MockEvent("{http://vgregion.se/patientcontext/events}pctx.change", pe);
        EventRequest mockReq = new MockEventRequest(mockEvent);

        controller.changeListner(mockReq, model);

        PatientEvent patient = (PatientEvent) model.get("patient");
        assertEquals(pe, patient);
    }

    @Test
    public void testChangeListnerPatientEventWithInputText() throws Exception {
        PatientEvent pe = new PatientEvent("aaa", PatientEvent.DEFAULT_GROUP_CODE);
        Event mockEvent = new MockEvent("{http://vgregion.se/patientcontext/events}pctx.change", pe);
        EventRequest mockReq = new MockEventRequest(mockEvent);

        controller.changeListner(mockReq, model);

        PatientEvent patient = (PatientEvent) model.get("patient");
        assertEquals(pe, patient);
    }


    @Test
    public void testChangeListnerPatientAreChangedInModel() throws Exception {
        PatientEvent peModel = new PatientEvent("19121212-1213", PatientEvent.DEFAULT_GROUP_CODE);
        model.addAttribute("patient", peModel);

        PatientEvent pe = new PatientEvent("19121212-1212", PatientEvent.DEFAULT_GROUP_CODE);
        Event mockEvent = new MockEvent("{http://vgregion.se/patientcontext/events}pctx.change", pe);
        EventRequest mockReq = new MockEventRequest(mockEvent);

        controller.changeListner(mockReq, model);

        PatientEvent patient = (PatientEvent) model.get("patient");
        assertEquals(pe, patient);
    }


    @Test
    public void testResetListner() throws Exception {
        PatientEvent pe = new PatientEvent("19121212-1212", PatientEvent.DEFAULT_GROUP_CODE);
        model.addAttribute("patient", pe);

        controller.resetListner(model);

        PatientEvent patient = (PatientEvent) model.get("patient");
        assertFalse(pe.equals(patient));
        assertNotSame(pe, patient);
    }
}
