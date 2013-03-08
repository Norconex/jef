package com.norconex.jef.util;

import java.util.concurrent.TimeUnit;

import com.norconex.jef.JobException;

/**
 * Convenience class to put to sleep the currently running thread.
 * @author Pascal Essiembre
 * @since 2.0
 */
public final class Sleeper {

    //TODO move to Commons Lang.
    
    public static final long ONE_SECOND = 1000;
    public static final long ONE_MINUTE = 60 * ONE_SECOND;
    public static final long ONE_HOUR = 60 * ONE_MINUTE;
    
    private Sleeper() {
        super();
    }

    /**
     * Sleeps for the number of milliseconds specified.
     * @param milliseconds milliseconds
     */
    @SuppressWarnings("nls")
    public static void sleepMillis(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new JobException("Cannot sleep.", e);
        }
    }
    /**
     * Sleeps for the number of nanoseconds specified.
     * @param nanos nanoseconds
     */
    @SuppressWarnings("nls")
    public static void sleepNanos(long nanoSeconds) {
        long milis = TimeUnit.NANOSECONDS.toMillis(nanoSeconds);
        int nanoRemains = (int) (nanoSeconds 
                - TimeUnit.MILLISECONDS.toNanos(milis));
        try {
            Thread.sleep(milis, nanoRemains);
        } catch (InterruptedException e) {
            throw new JobException("Cannot sleep.", e);
        }
    }
    /**
     * Sleeps for the number of seconds specified.
     * @param seconds seconds
     */
    public static void sleepSeconds(int seconds) {
        sleepMillis(seconds * ONE_SECOND);
    }
    /**
     * Sleeps for the number of minutes specified.
     * @param minutes minutes
     */
    public static void sleepMinutes(int minutes) {
        sleepMillis(minutes * ONE_MINUTE);
    }
    /**
     * Sleeps for the number of hours specified.
     * @param hours hours
     */
    public static void sleepHours(int hours) {
        sleepMillis(hours * ONE_HOUR);
    }
}
