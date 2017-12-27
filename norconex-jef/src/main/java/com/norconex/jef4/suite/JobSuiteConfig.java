/* Copyright 2010-2017 Norconex Inc.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.norconex.jef4.event.IJobEventListener;
import com.norconex.jef4.status.IJobStatusStore;

//TODO really have a config still??? given it contains so little, shall
// we move these settings directly on JobSuite? Else, make IXMLConfigurable?

public class JobSuiteConfig {

    private IJobStatusStore jobStatusStore;
//    private ILogManager logManager;
    private String workdir;
    
    private final List<IJobEventListener> eventListeners = new ArrayList<>();
    
    public JobSuiteConfig() {
        super();
    }

//    /**
//     * Gets the Log4J log manager.
//     * @return Log4J log manager
//     */
//    public ILogManager getLogManager() {
//        return logManager;
//    }
//    public void setLogManager(ILogManager logManager) {
//        this.logManager = logManager;
//    }

    public String getWorkdir() {
        return workdir;
    }

    public void setWorkdir(String workdir) {
        this.workdir = workdir;
    }

    public IJobStatusStore getJobStatusStore() {
        return jobStatusStore;
    }

    public void setJobStatusStore(IJobStatusStore jobStatusStore) {
        this.jobStatusStore = jobStatusStore;
    }

    public List<IJobEventListener> getEventListeners() {
        return eventListeners;
    }
    public void setEventListeners(IJobEventListener... eventListeners) {
        this.eventListeners.clear();
        this.eventListeners.addAll(Arrays.asList(eventListeners));
    }
    public void addEventListeners(IJobEventListener... eventListeners) {
        this.eventListeners.addAll(Arrays.asList(eventListeners));
    }
    public void removeEventListeners() {
        this.eventListeners.clear();
    }
    public void removeEventListeners(IJobEventListener... eventListeners) {
        this.eventListeners.removeAll(Arrays.asList(eventListeners));
    }
}
