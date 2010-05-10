package se.vgregion.portal.patientcontext;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

/**
 * This action do that and that, if it has something special it is.
 *
 * @author <a href="mailto:david.rosell@redpill-linpro.com">David Rosell</a>
 */
@Controller
@RequestMapping("EDIT")
public class ConfigurationController {

    public static final String EDIT_JSP = "configuration";

    @RenderMapping
    public String view() {
        return EDIT_JSP;
    }
}
