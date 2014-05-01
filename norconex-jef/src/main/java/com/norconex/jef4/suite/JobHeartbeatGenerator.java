package com.norconex.jef4.suite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.norconex.commons.lang.Sleeper;
import com.norconex.jef4.status.MutableJobStatus;

public class JobHeartbeatGenerator extends Thread {

    private static final long HEARTBEAT_INTERVAL = 5000;
    
    private final List<MutableJobStatus> statuses = 
            Collections.synchronizedList(new ArrayList<MutableJobStatus>());
    private final JobSuite suite;
    
    private boolean terminate = false;
    
    public JobHeartbeatGenerator(JobSuite suite) {
        super();
        this.suite = suite;
    }

    @Override
    public void run() {
        while(!terminate) {
            for (MutableJobStatus status : statuses) {
                status.setLastActivity(new Date(suite.getJobStatusStore().touch(
                        suite.getName(), status.getJobName())));
            }
            Sleeper.sleepMillis(HEARTBEAT_INTERVAL);
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
