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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.norconex.jef4.job.IJobErrorListener;
import com.norconex.jef4.job.IJobLifeCycleListener;
import com.norconex.jef4.log.ILogManager;
import com.norconex.jef4.status.IJobStatusStore;

public class JobSuiteConfig {

    private IJobStatusStore jobStatusStore;
    private ILogManager logManager;
    private String workdir;
    
    private final List<IJobLifeCycleListener> jobLifeCycleListeners =
            new ArrayList<IJobLifeCycleListener>();
    private final List<IJobErrorListener> jobErrorListeners =
            new ArrayList<IJobErrorListener>();
    private final List<ISuiteLifeCycleListener> suiteLifeCycleListeners =
            new ArrayList<ISuiteLifeCycleListener>();
    
    public JobSuiteConfig() {
        super();
    }

    /**
     * Gets the Log4J log manager.
     * @return Log4J log manager
     */
    public ILogManager getLogManager() {
        return logManager;
    }
    public void setLogManager(ILogManager logManager) {
        this.logManager = logManager;
    }

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

    public List<IJobLifeCycleListener> getJobLifeCycleListeners() {
        return jobLifeCycleListeners;
    }
    public void setJobLifeCycleListeners(
            IJobLifeCycleListener... jobLifeCycleListeners) {
        this.jobLifeCycleListeners.clear();
        this.jobLifeCycleListeners.addAll(Arrays.asList(jobLifeCycleListeners));
    }
    public List<IJobErrorListener> getJobErrorListeners() {
        return jobErrorListeners;
    }
    public void setJobErrorListeners(IJobErrorListener... errorListeners) {
        this.jobErrorListeners.clear();
        this.jobErrorListeners.addAll(Arrays.asList(errorListeners));
    }
    public List<ISuiteLifeCycleListener> getSuiteLifeCycleListeners() {
        return suiteLifeCycleListeners;
    }
    public void setSuiteLifeCycleListeners(
            ISuiteLifeCycleListener... suiteLifeCycleListeners) {
        this.suiteLifeCycleListeners.clear();
        this.suiteLifeCycleListeners.addAll(
                Arrays.asList(suiteLifeCycleListeners));
    }


}
