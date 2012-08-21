package se.vgregion.portal.concurrency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.portlet.PortletSession;
import javax.portlet.ResourceResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Patrik Bergström
 */
public class ThreadSynchronizationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadSynchronizationManager.class);

    private final Map<String, CountDownLatch> blockedThreads = Collections.synchronizedMap(
            new HashMap<String, CountDownLatch>());

    private static ThreadSynchronizationManager instance = null;
    private int timeout = 30; // Default
    private TimeUnit timeoutTimeUnit = TimeUnit.SECONDS; // Default

    private ThreadSynchronizationManager() {

    }

    public synchronized static ThreadSynchronizationManager getInstance() {
        if (instance == null) {
            instance = new ThreadSynchronizationManager();
        }
        return instance;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setTimeoutTimeUnit(TimeUnit timeoutTimeUnit) {
        this.timeoutTimeUnit = timeoutTimeUnit;
    }

    public void pollForChange(String sessionId, ResourceResponse response) {
        boolean shouldUpdate = waitForUpdate(sessionId);
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            if (shouldUpdate) {
                writer.append("update=true");
            } else {
                writer.append("update=false");
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            writer.close();
        }
    }

    public void notifyBlockedThread(String sessionId) throws InterruptedException {
        // Sequential operations on blockedThreads are kept in synchronized blocks
        synchronized (this) {
            CountDownLatch countDownLatch = blockedThreads.get(sessionId);
            if (countDownLatch != null) {
                blockedThreads.remove(sessionId);
                countDownLatch.countDown();
                return;
            }
        }

        // There is a small chance that the countDownLatch is null since we are in between two requests.
        // Therefor we try again with a delay.
        Thread.sleep(2000);
        synchronized (this) {
            CountDownLatch countDownLatch = blockedThreads.get(sessionId);
            if (countDownLatch != null) {
                blockedThreads.remove(sessionId);
                countDownLatch.countDown();
            }
        }

    }

    // Count down the CountDownLatch to make a blocked thread continue.
    public void notifyBlockedThread(PortletSession portletSession) throws InterruptedException {
        String sessionId = portletSession.getId();
        notifyBlockedThread(sessionId);
    }

    private boolean waitForUpdate(String sessionId) {

        // A way to wait for a certain event.
        try {
            CountDownLatch countDownLatch;
            synchronized (this) {
                if (blockedThreads.containsKey(sessionId)) {
                    countDownLatch = blockedThreads.get(sessionId);
                } else {
                    countDownLatch = new CountDownLatch(1);
                    blockedThreads.put(sessionId, countDownLatch);
                }
            }
            boolean await = countDownLatch.await(timeout, timeoutTimeUnit);
            return await; // returns true if the CountDownLatch is counted down or false if it times out
        } catch (InterruptedException e) {
            LOGGER.info(e.getMessage(), e);
        }
        return false;
    }

}
