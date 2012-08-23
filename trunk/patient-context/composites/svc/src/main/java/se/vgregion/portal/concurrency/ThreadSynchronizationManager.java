package se.vgregion.portal.concurrency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.portlet.MimeResponse;
import javax.portlet.PortletSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Class to manage synchronization between threads. Basically a number of threads may call
 * {@link ThreadSynchronizationManager#pollForChange(java.lang.String, javax.portlet.MimeResponse)} where they will
 * be blocked as long as another thread calls
 * {@link ThreadSynchronizationManager#notifyBlockedThreads(java.lang.String)} with the same sessionId or a timeout
 * occurs.
 *
 * @author Patrik Bergström
 */
public final class ThreadSynchronizationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadSynchronizationManager.class);
    private static final int DEFAULT_TIMEOUT = 30;

    private final Map<String, CountDownLatch> blockedThreads = Collections.synchronizedMap(
            new HashMap<String, CountDownLatch>());

    private static ThreadSynchronizationManager instance = null;
    private int timeout = DEFAULT_TIMEOUT;
    private TimeUnit timeoutTimeUnit = TimeUnit.SECONDS; // Default

    private ThreadSynchronizationManager() {

    }

    /**
     * Retrieve the singleton instance.
     *
     * @return the singleton instance of {@link ThreadSynchronizationManager}
     */
    public static synchronized ThreadSynchronizationManager getInstance() {
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

    /**
     * This method blocks until {@link ThreadSynchronizationManager#notifyBlockedThreads} is called with the same
     * sessionId, or a timeout occurs. If it returns due to an update of the patient context "update=true" will be
     * written to the response. If it returns due to a timeout "update=false" will be written to the response.
     *
     * @param sessionId the sessionId
     * @param response  the response
     */
    public void pollForChange(String sessionId, MimeResponse response) {
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
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * All threads which handle requests with the given sessionId and which are currently blocked in
     * {@link ThreadSynchronizationManager#pollForChange(java.lang.String, javax.portlet.MimeResponse)} will be
     * released when a call to this method is made.
     *
     * @param sessionId the sessionId of the requests which are handled by blocked threads
     * @throws InterruptedException
     */
    public void notifyBlockedThreads(String sessionId) {
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
        // Therefor we try again with a delay. (It's important that the Thread.sleep() doesn't occur in a synchronized
        // block since that would cause performance issues)
        try {
            final int millis = 2000;
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        synchronized (this) {
            CountDownLatch countDownLatch = blockedThreads.get(sessionId);
            if (countDownLatch != null) {
                blockedThreads.remove(sessionId);
                countDownLatch.countDown();
            }
        }
    }

    /**
     * Overloaded version of {@link ThreadSynchronizationManager#notifyBlockedThreads(java.lang.String)}.
     *
     * @param portletSession the {@link PortletSession} instance
     */
    public void notifyBlockedThreads(PortletSession portletSession) {
        String sessionId = portletSession.getId();
        notifyBlockedThreads(sessionId);
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
