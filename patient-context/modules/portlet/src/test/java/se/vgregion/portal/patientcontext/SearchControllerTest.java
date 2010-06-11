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
import org.springframework.mock.web.portlet.MockRenderRequest;
import org.springframework.ui.ModelMap;
import se.vgregion.portal.patient.event.PatientContext;
import se.vgregion.portal.patient.event.PatientEvent;

import javax.portlet.RenderRequest;

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

    private PatientContext initPatientContext(String inputText) {
        PatientContext pCtx = new PatientContext();
        PatientEvent patient = new PatientEvent();
        patient.setInputText(inputText);
        pCtx.setCurrentPatient(patient);
        return pCtx;
    }

    @Test
    public void testSearchEvent() throws Exception {
    }

    @Test
    public void testResetEvent() throws Exception {
    }
}
