package se.vgregion.portal.patientlistner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.portlet.bind.annotation.EventMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import javax.portlet.*;

/**
 * This action do that and that, if it has something special it is.
 *
 * @author <a href="mailto:david.rosell@redpill-linpro.com">David Rosell</a>
 */
@Controller
@RequestMapping("VIEW")
@SessionAttributes("personNumber")
public class ListnerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListnerController.class);

    public static final String VIEW_JSP = "view";

    @RenderMapping
    public String view(ModelMap model, RenderRequest request) throws PortletSecurityException {
        if (!model.containsKey("personNumber")) {
            model.addAttribute("personNumber", "No one selected");
        }

        return VIEW_JSP;
    }

    @EventMapping("{http://vgregion.se/patientcontext/events}pctx.personNumber")
    public void listner(EventRequest request, EventResponse response, ModelMap model) {
        Event event = request.getEvent();
        String pNo = (String)event.getValue();
        LOGGER.debug("person nummer: "+pNo);
        model.addAttribute("personNumber", pNo);

        response.setRenderParameter("pNo", pNo);
    }

}
