package se.vgregion.portal.concurrency;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.portlet.MockResourceResponse;

import javax.portlet.ResourceResponse;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * @author Patrik Bergström
 */
public class ThreadSynchronizationManagerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadSynchronizationManagerTest.class);

    private ThreadSynchronizationManager manager = ThreadSynchronizationManager.getInstance();
    private String sessionId = "1";

    @Before
    public void setup() {
        manager.setTimeout(1);
        manager.setTimeoutTimeUnit(TimeUnit.SECONDS);
    }

    // Test that the synchronization works as expected.
    @Test
    public void testSynchronize() throws InterruptedException, UnsupportedEncodingException {

        MockResourceResponse r1 = new MockResourceResponse();
        MockResourceResponse r2 = new MockResourceResponse();
        MockResourceResponse r3 = new MockResourceResponse();
        MockResourceResponse r4 = new MockResourceResponse();
        MockResourceResponse r5 = new MockResourceResponse();

        // Start five threads at different times.

        Thread t1 = new Thread(new Poller("t1", r1));
        Thread t2 = new Thread(new Poller("t2", r2));
        Thread t3 = new Thread(new Poller("t3", r3));
        Thread t4 = new Thread(new Poller("t4", r4));
        Thread t5 = new Thread(new Poller("t5", r5));

        t1.start();
        t2.start();
        Thread.sleep(600);
        t3.start();
        Thread.sleep(600); // After this sleep t1 and t2 are timed (600+600>1000) out while t3 is not timed out.
        t4.start();
        t5.start();

        Thread.sleep(100); // It's realistic to sleep some time before someone triggers the notifyBlockedThread.

        // Trigger the update. Soon we'll se which threads have timed out.
        manager.notifyBlockedThread(sessionId);

        // Wait for all to finish
        t1.join();
        t2.join();
        t3.join();
        t4.join();
        t5.join();

        // Verify the result
        assertEquals("update=false", r1.getContentAsString());
        assertEquals("update=false", r2.getContentAsString());
        assertEquals("update=true", r3.getContentAsString());
        assertEquals("update=true", r4.getContentAsString());
        assertEquals("update=true", r5.getContentAsString());
    }

    private class Poller implements Runnable {

        private String threadName;
        private ResourceResponse response;

        private Poller(String threadName, ResourceResponse response) {
            this.threadName = threadName;
            this.response = response;
        }

        @Override
        public void run() {
            manager.pollForChange(sessionId, response);
            try {
                LOGGER.info(threadName + ": " + ((MockResourceResponse) response).getContentAsString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
