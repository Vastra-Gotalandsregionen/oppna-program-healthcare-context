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

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.portlet.MockActionResponse;
import org.springframework.mock.web.portlet.MockPortletPreferences;
import org.springframework.mock.web.portlet.MockRenderRequest;
import org.springframework.ui.ModelMap;
import se.vgregion.portal.patient.event.PatientContext;
import se.vgregion.portal.patient.event.PatientEvent;

import javax.portlet.RenderRequest;
import javax.xml.namespace.QName;

import static org.junit.Assert.*;

/**
 * This action do that and that, if it has something special it is.
 *
 * @author <a href="mailto:david.rosell@redpill-linpro.com">David Rosell</a>
 */
public class SearchControllerTest {
    private SearchController controller;
    private ModelMap model;

    @Before
    public void setUp() throws Exception {
        controller = new SearchController();
        model = new ModelMap();
    }

    @Test
    public void testViewNoPatientContext() throws Exception {
        RenderRequest mockReq = new MockRenderRequest();
        String result = controller.view(model, mockReq);

        assertEquals(SearchController.VIEW_JSP, result);

        assertTrue(model.containsKey("patientContext"));
        PatientContext pCtx = (PatientContext) model.get("patientContext");
        assertNull(pCtx.getCurrentPatient());
        assertNotNull(pCtx.getPatientHistory());
        assertEquals(0, pCtx.getPatientHistorySize());
        assertEquals(0, pCtx.getPatientHistory().size());

        assertTrue(model.containsKey("searchPatient"));
        SearchPatientFormBean formBean = (SearchPatientFormBean) model.get("searchPatient");
        assertNotNull(formBean);
        assertNull(formBean.getSearchText());
        assertNull(formBean.getHistorySearchText());
    }

    @Test
    public void testViewPatientContext() throws Exception {
        RenderRequest mockReq = new MockRenderRequest();
        PatientContext pCtx = initPatientContext("19121212-1212");
        model.addAttribute("patientContext", pCtx);

        String result = controller.view(model, mockReq);

        assertEquals(SearchController.VIEW_JSP, result);

        assertSame(pCtx, model.get("patientContext"));

        assertNotNull(pCtx.getPatientHistory());
        assertEquals(0, pCtx.getPatientHistorySize());
        assertEquals(0, pCtx.getPatientHistory().size());

        assertTrue(model.containsKey("searchPatient"));
        SearchPatientFormBean formBean = (SearchPatientFormBean) model.get("searchPatient");
        assertEquals(pCtx.getCurrentPatient().getPersonNummer().getNormal(), formBean.getSearchText());
        assertNull(formBean.getHistorySearchText());
    }

    @Test
    public void testViewPatientContextNotPersonNummer() throws Exception {
        RenderRequest mockReq = new MockRenderRequest();
        PatientContext pCtx = initPatientContext("aaa");
        model.addAttribute("patientContext", pCtx);

        String result = controller.view(model, mockReq);
        SearchPatientFormBean formBean = (SearchPatientFormBean) model.get("searchPatient");
        assertEquals(pCtx.getCurrentPatient().getInputText(), formBean.getSearchText());
        assertNull(formBean.getHistorySearchText());
    }

    @Test
    public void testSearchEventFormBeanEmpty() throws Exception {
        SearchPatientFormBean formBean = new SearchPatientFormBean();
        PatientContext pCtx = new PatientContext();
        MockActionResponse mockRes = new MockActionResponse();
        MockPortletPreferences mockPrefs = new MockPortletPreferences();

        controller.searchEvent(formBean, pCtx, mockRes, mockPrefs);

        assertNull(pCtx.getCurrentPatient());
        assertEquals(0, pCtx.getPatientHistorySize());
        assertFalse(mockRes.getEventNames().hasNext());
    }

    @Test
    public void testSearchEventFirstSearch() throws Exception {
        SearchPatientFormBean formBean = new SearchPatientFormBean();
        formBean.setSearchText("191212121212");
        PatientContext pCtx = new PatientContext();
        MockActionResponse mockRes = new MockActionResponse();
        MockPortletPreferences mockPrefs = new MockPortletPreferences();

        controller.searchEvent(formBean, pCtx, mockRes, mockPrefs);

        assertNotNull(pCtx.getCurrentPatient());
        assertEquals(1, pCtx.getPatientHistorySize());
        assertEquals(1, pCtx.getPatientHistory().size());
        assertTrue(mockRes.getEventNames().hasNext());
        PatientEvent patient = (PatientEvent) mockRes.getEvent(new QName("http://vgregion.se/patientcontext/events", "pctx.change"));
        assertSame(patient, pCtx.getCurrentPatient());
        assertSame(patient, pCtx.getPatientHistory().get(0));
    }

    @Test
    public void testSearchEventSecondSearch() throws Exception {
        SearchPatientFormBean formBean = new SearchPatientFormBean();
        formBean.setSearchText("191212121212");
        PatientContext pCtx = new PatientContext();
        MockActionResponse mockRes = new MockActionResponse();
        MockPortletPreferences mockPrefs = new MockPortletPreferences();

        controller.searchEvent(formBean, pCtx, mockRes, mockPrefs);

        formBean.setSearchText("191212121213");
        controller.searchEvent(formBean, pCtx, mockRes, mockPrefs);

        PatientEvent patient = pCtx.getCurrentPatient();
        assertNotNull(patient);
        assertEquals("191212121213", patient.getInputText());
        assertEquals(2, pCtx.getPatientHistorySize());
        assertEquals(2, pCtx.getPatientHistory().size());
        assertTrue(mockRes.getEventNames().hasNext());

        PatientEvent patientEvent = (PatientEvent) mockRes.getEvent(new QName("http://vgregion.se/patientcontext/events", "pctx.change"));
        assertSame(patientEvent, pCtx.getCurrentPatient());
    }

    @Test
    public void testSearchEventSecondSearchSamePatient() throws Exception {
        SearchPatientFormBean formBean = new SearchPatientFormBean();
        formBean.setSearchText("191212121212");
        PatientContext pCtx = new PatientContext();
        MockActionResponse mockRes = new MockActionResponse();
        MockPortletPreferences mockPrefs = new MockPortletPreferences();

        controller.searchEvent(formBean, pCtx, mockRes, mockPrefs);

        formBean.setSearchText("1212121212");
        controller.searchEvent(formBean, pCtx, mockRes, mockPrefs);

        PatientEvent patient = pCtx.getCurrentPatient();
        assertNotNull(patient);
        assertEquals("191212121212", patient.getInputText());
        assertEquals(1, pCtx.getPatientHistorySize());
        assertEquals(1, pCtx.getPatientHistory().size());
        assertTrue(mockRes.getEventNames().hasNext());

        PatientEvent patientEvent = (PatientEvent) mockRes.getEvent(new QName("http://vgregion.se/patientcontext/events", "pctx.change"));
        assertEquals(patientEvent, pCtx.getCurrentPatient());
    }

    @Test
    public void testSearchEventHistorySearch() throws Exception {
        SearchPatientFormBean formBean = new SearchPatientFormBean();
        formBean.setSearchText("191212121212");
        PatientContext pCtx = new PatientContext();
        MockActionResponse mockRes = new MockActionResponse();
        MockPortletPreferences mockPrefs = new MockPortletPreferences();

        controller.searchEvent(formBean, pCtx, mockRes, mockPrefs);
        formBean.setSearchText("121212-1213");
        controller.searchEvent(formBean, pCtx, mockRes, mockPrefs);

        formBean.setHistorySearchText("121212-1212");
        controller.searchEvent(formBean, pCtx, mockRes, mockPrefs);

        PatientEvent patient = pCtx.getCurrentPatient();
        assertNotNull(patient);
        assertEquals("121212-1212", patient.getInputText());
        assertEquals(2, pCtx.getPatientHistorySize());
        assertEquals(2, pCtx.getPatientHistory().size());
        assertTrue(mockRes.getEventNames().hasNext());

        PatientEvent patientEvent = (PatientEvent) mockRes.getEvent(new QName("http://vgregion.se/patientcontext/events", "pctx.change"));
        assertSame(patientEvent, pCtx.getCurrentPatient());
    }

    @Test
    public void testSearchEventHistorySearchNotInHistory() throws Exception {
        SearchPatientFormBean formBean = new SearchPatientFormBean();
        formBean.setSearchText("191212121212");
        PatientContext pCtx = new PatientContext();
        MockActionResponse mockRes = new MockActionResponse();
        MockPortletPreferences mockPrefs = new MockPortletPreferences();

        controller.searchEvent(formBean, pCtx, mockRes, mockPrefs);
        formBean.setSearchText("121212-1213");
        controller.searchEvent(formBean, pCtx, mockRes, mockPrefs);

        formBean.setHistorySearchText("121212-1214");
        controller.searchEvent(formBean, pCtx, mockRes, mockPrefs);

        PatientEvent patient = pCtx.getCurrentPatient();
        assertNotNull(patient);
        assertEquals("121212-1214", patient.getInputText());
        assertEquals(3, pCtx.getPatientHistorySize());
        assertEquals(3, pCtx.getPatientHistory().size());
        assertTrue(mockRes.getEventNames().hasNext());

        PatientEvent patientEvent = (PatientEvent) mockRes.getEvent(new QName("http://vgregion.se/patientcontext/events", "pctx.change"));
        assertSame(patientEvent, pCtx.getCurrentPatient());
    }

    @Test
    public void testResetEvent() throws Exception {
        PatientContext pCtx = new PatientContext();
        PatientEvent patient = new PatientEvent("121212-1212", PatientEvent.DEFAULT_GROUP_CODE);
        pCtx.setCurrentPatient(patient);
        pCtx.addToHistory(patient);

        MockActionResponse mockRes = new MockActionResponse();

        controller.resetEvent(pCtx, mockRes);

        assertNull(pCtx.getCurrentPatient());
        assertEquals(1, pCtx.getPatientHistorySize());
        assertEquals(patient, pCtx.getPatientHistory().get(0));

        String event = (String) mockRes.getEvent(new QName("http://vgregion.se/patientcontext/events", "pctx.reset"));
        assertEquals("reset", event);
    }

    private PatientContext initPatientContext(String inputText) {
        PatientContext pCtx = new PatientContext();
        PatientEvent patient = new PatientEvent(inputText, PatientEvent.DEFAULT_GROUP_CODE);
        pCtx.setCurrentPatient(patient);
        return pCtx;
    }
}
