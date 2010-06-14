package se.vgregion.portal.patientlistner;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.portlet.MockEvent;
import org.springframework.mock.web.portlet.MockEventRequest;
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
        String result = controller.view(model);

        assertEquals(ListnerController.VIEW_JSP, result);
        assertTrue(model.containsKey("patient"));
        PatientEvent patient = (PatientEvent) model.get("patient");
        assertEquals("", patient.getInputText());
        assertNull(patient.getPersonNummer());
    }

    @Test
    public void testViewRevisit() throws Exception {
        PatientEvent pe = new PatientEvent();
        pe.setInputText("19121212-1212");
        model.addAttribute("patient", pe);

        controller.view(model);

        PatientEvent patient = (PatientEvent) model.get("patient");
        assertEquals(pe, patient);
    }

    @Test
    public void testChangeListnerPatientEventWithPersonNumber() throws Exception {
        PatientEvent pe = new PatientEvent();
        pe.setInputText("19121212-1212");
        Event mockEvent = new MockEvent("{http://vgregion.se/patientcontext/events}pctx.change", pe);
        EventRequest mockReq = new MockEventRequest(mockEvent);

        controller.changeListner(mockReq, model);

        PatientEvent patient = (PatientEvent) model.get("patient");
        assertEquals(pe, patient);
    }

    @Test
    public void testChangeListnerPatientEventWithInputText() throws Exception {
        PatientEvent pe = new PatientEvent();
        pe.setInputText("aaa");
        Event mockEvent = new MockEvent("{http://vgregion.se/patientcontext/events}pctx.change", pe);
        EventRequest mockReq = new MockEventRequest(mockEvent);

        controller.changeListner(mockReq, model);

        PatientEvent patient = (PatientEvent) model.get("patient");
        assertEquals(pe, patient);
    }


    @Test
    public void testChangeListnerPatientAreChangedInModel() throws Exception {
        PatientEvent peModel = new PatientEvent();
        peModel.setInputText("19121212-1213");
        model.addAttribute("patient", peModel);

        PatientEvent pe = new PatientEvent();
        pe.setInputText("19121212-1212");
        Event mockEvent = new MockEvent("{http://vgregion.se/patientcontext/events}pctx.change", pe);
        EventRequest mockReq = new MockEventRequest(mockEvent);

        controller.changeListner(mockReq, model);

        PatientEvent patient = (PatientEvent) model.get("patient");
        assertEquals(pe, patient);
    }


    @Test
    public void testResetListner() throws Exception {
        PatientEvent pe = new PatientEvent();
        pe.setInputText("19121212-1212");
        model.addAttribute("patient", pe);

        controller.resetListner(model);

        PatientEvent patient = (PatientEvent) model.get("patient");
        assertFalse(pe.equals(patient));
        assertNotSame(pe, patient);
    }
}
