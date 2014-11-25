/* Copyright 2010-2014 Norconex Inc.
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
                                    suite.getId(), status.getJobId())));
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
