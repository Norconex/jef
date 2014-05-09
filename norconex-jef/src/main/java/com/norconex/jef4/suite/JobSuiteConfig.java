package com.norconex.jef4.suite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.norconex.jef4.job.IJobErrorListener;
import com.norconex.jef4.job.IJobLifeCycleListener;
import com.norconex.jef4.log.ILogManager;
import com.norconex.jef4.status.IJobStatusStore;

public class JobSuiteConfig implements Serializable {

    private static final long serialVersionUID = 5521879402369478461L;

    private IJobStatusStore jobStatusStore;
    private ILogManager logManager;
    private String workdir;
    
    private final List<IJobLifeCycleListener> jobLifeCycleListeners =
            new ArrayList<IJobLifeCycleListener>();
    private final List<IJobErrorListener> jobErrorListeners =
            new ArrayList<IJobErrorListener>();
    private final List<ISuiteLifeCycleListener> suiteLifeCycleListeners =
            new ArrayList<ISuiteLifeCycleListener>();
//    private final List<ISuiteStopRequestListener> stopListeners =
//            new ArrayList<ISuiteStopRequestListener>();

    
    public JobSuiteConfig() {
        super();
    }

    /**
     * Gets the job status serializer.
     * @return job status serializer
     */
    public IJobStatusStore getJobStatusSerializer() {
        return jobStatusStore;
    }
    public void setJobStatusSerializer(
            IJobStatusStore jobStatusStore) {
        this.jobStatusStore = jobStatusStore;
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
