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

package se.vgregion.portal.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import se.vgregion.portal.patient.event.PatientEvent;

import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;

/**
 * Configuration Controller for the the portlet.
 * Used to configure group-code for a portlet.
 *
 * The group-code is used for configuring which listner portlet should
 * listen to which event sender portlet.
 * The group-code are stored in PortletPreferences. 
 *
 * @author <a href="mailto:david.rosell@redpill-linpro.com">David Rosell</a>
 */
@Controller
@RequestMapping("EDIT")
public class ConfigurationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationController.class);

    /**
     * jsp name.
     */
    public static final String EDIT_JSP = "configuration";

    /**
     * Nothing here yet.
     *
     * @return edit view jsp.
     */
    @RenderMapping
    public String view(ModelMap model, PortletPreferences prefs) {
        String currentGroupCode = prefs.getValue("group.code", PatientEvent.DEFAULT_GROUP_CODE);

        ConfigurationFormBean formBean = new ConfigurationFormBean();
        formBean.setGroupCode(currentGroupCode);

        model.addAttribute("configure", formBean);

        return EDIT_JSP;
    }

    @ActionMapping("configureEvent")
    public void configureEvent(@ModelAttribute("configure") ConfigurationFormBean formBean,
                               ActionResponse response, PortletPreferences prefs) {
        String currentGroupCode = prefs.getValue("group.code", PatientEvent.DEFAULT_GROUP_CODE);
        String newGroupCode = formBean.getGroupCode();

        if (!currentGroupCode.equals(newGroupCode)) {
            try {
                prefs.setValue("group.code", newGroupCode);
                prefs.store();
            } catch (Exception e) {
                LOGGER.error("Failed to update group.code", e);
            }
        }
    }

}
