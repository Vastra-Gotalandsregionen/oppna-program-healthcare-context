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
import org.springframework.mock.web.portlet.MockPortletPreferences;
import org.springframework.ui.ModelMap;
import se.vgregion.portal.configuration.ConfigurationController;
import se.vgregion.portal.configuration.ConfigurationFormBean;
import se.vgregion.portal.patient.event.PatientEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This action do that and that, if it has something special it is.
 *
 * @author <a href="mailto:david.rosell@redpill-linpro.com">David Rosell</a>
 */
public class ConfigurationControllerTest {
    private ConfigurationController controller;

    @Before
    public void setUp() throws Exception {
        controller = new ConfigurationController();
    }

    @Test
    public void testView() throws Exception {
        ModelMap model = new ModelMap();
        MockPortletPreferences mockPrefs = new MockPortletPreferences();

        String result = controller.view(model, mockPrefs);

        assertTrue(model.containsAttribute("configure"));
        ConfigurationFormBean formBean = (ConfigurationFormBean) model.get("configure");
        assertEquals(formBean.getGroupCode(), PatientEvent.DEFAULT_GROUP_CODE);
        
        assertEquals(result, ConfigurationController.EDIT_JSP);
    }
}
