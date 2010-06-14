package se.vgregion.portal.patientcontext;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
        String result = controller.view();

        assertEquals(result, ConfigurationController.EDIT_JSP);
    }
}
