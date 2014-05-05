package com.norconex.jef4.suite;

import java.io.IOException;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.norconex.commons.lang.Sleeper;
import com.norconex.jef4.JEFException;
import com.norconex.jef4.status.MutableJobStatus;

public class JobHeartbeatGenerator extends Thread {

    private static final long HEARTBEAT_INTERVAL = 5000;
    
    private final Queue<MutableJobStatus> statuses = 
            new ConcurrentLinkedQueue<>();
    private final JobSuite suite;
    
    private boolean terminate = false;
    
    public JobHeartbeatGenerator(JobSuite suite) {
        super();
        this.suite = suite;
    }

    @Override
    public void run() {
        try {
            while(!terminate) {
                for (MutableJobStatus status : statuses) {
                    status.setLastActivity(
                            new Date(suite.getJobStatusStore().touch(
                                    suite.getName(), status.getJobName())));
                }
                Sleeper.sleepMillis(HEARTBEAT_INTERVAL);
            }
        } catch (IOException e) {
            throw new JEFException("Cannot update status heartbeat.", e);
        }
    }

    public void register(MutableJobStatus status) {
        statuses.add(status);
    }
    public void unregister(MutableJobStatus status) {
        statuses.remove(status);
    }
    
    @Override
    public synchronized void start() {
        terminate = false;
        super.start();
    }
    
    public void terminate() {
        terminate = true;
    }
}
