/* Copyright 2010-2018 Norconex Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.norconex.jef5.suite;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.norconex.commons.lang.Sleeper;
import com.norconex.jef5.JefException;
import com.norconex.jef5.session.JobSession;

public class JobHeartbeatGenerator extends Thread {

    //TODO have a isRunning method on JobSessionFacade instead?
    public static final long HEARTBEAT_INTERVAL = 5000;
    
    private final Queue<JobSession> statuses = 
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
                for (JobSession status : statuses) {
                    status.setLastActivity(suite.getJobSuiteSessionDAO().touch(
                            status.getJobId()));
                }
                Sleeper.sleepMillis(HEARTBEAT_INTERVAL);
            }
        } catch (IOException e) {
            throw new JefException("Cannot update status heartbeat.", e);
        }
    }

    public void register(JobSession status) {
        statuses.add(status);
    }
    public void unregister(JobSession status) {
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
