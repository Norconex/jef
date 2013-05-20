/* Copyright 2010-2013 Norconex Inc.
 * 
 * This file is part of Norconex JEF.
 * 
 * Norconex JEF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Norconex JEF is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Norconex JEF. If not, see <http://www.gnu.org/licenses/>.
 */
package com.norconex.jef.suite;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.norconex.commons.lang.Sleeper;
import com.norconex.jef.IJob;
import com.norconex.jef.IJobContext;
import com.norconex.jef.IJobGroup;
import com.norconex.jef.JobContext;
import com.norconex.jef.JobException;
import com.norconex.jef.error.IErrorHandler;
import com.norconex.jef.log.FileLogManager;
import com.norconex.jef.log.ILogManager;
import com.norconex.jef.progress.IJobProgressListener;
import com.norconex.jef.progress.IJobProgressSerializer;
import com.norconex.jef.progress.IJobStatus;
import com.norconex.jef.progress.JobProgressPropertiesFileSerializer;
import com.norconex.jef.progress.JobProgressStateChangeAdapter;

/**
 * A job suite is an amalgamation of jobs, represented as a single executable
 * unit.  It can be seen as of one big job made of several sub-jobs.
 * Configurations applied to a suite affects all jobs associated
 * with the suite.
 * All jobs making up a suite must have unique identifiers.
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 */
@SuppressWarnings("nls")
public final class JobSuite {

    /** Empty progress listeners. */
    private static final IJobProgressListener[] EMPTY_PROGRESS_LISTENERS =
            new IJobProgressListener[] {};
    /** Empty suite life-cycle listeners. */
    private static final ISuiteLifeCycleListener[] EMPTY_SUITE_LISTENERS =
        new ISuiteLifeCycleListener[] {};
    /** Empty error handlers. */
    private static final IErrorHandler[] EMPTY_ERROR_HANDLERS =
            new IErrorHandler[] {};
    /** Empty suite stop request listeners. */
    private static final ISuiteStopRequestListener[] EMPTY_STOP_LISTENERS =
        new ISuiteStopRequestListener[] {};

    private static final int STOP_WAIT_DELAY = 5;
    
    /** Job progress serializer. */
    private final IJobProgressSerializer progressSerializer;
    /** Job log manager. */
    private final ILogManager logManager;
    /** Job progress listeners. */
    private final List<IJobProgressListener> progressListeners =
            Collections.synchronizedList(new ArrayList<IJobProgressListener>());
    /** Job error handlers. */
    private final List<IErrorHandler> errorHandlers =
            Collections.synchronizedList(new ArrayList<IErrorHandler>());
    /** Job suite listeners. */
    private final List<ISuiteLifeCycleListener> suiteListeners =
            Collections.synchronizedList(
                    new ArrayList<ISuiteLifeCycleListener>());
    /** Job suite stop request listeners. */
    private final List<ISuiteStopRequestListener> stopListeners =
            Collections.synchronizedList(
                    new ArrayList<ISuiteStopRequestListener>());
    
    /** Root job. */
    private final IJob rootJob;
    /** Unique identifiers of all jobs making up the suite in logical order. */
    private final List<String> jobIds = new ArrayList<String>();
    /** Mapping of jobIds and actual jobs for fast-access. */
    private final Map<String, IJob> jobByIds = new HashMap<String, IJob>();
    
    private final IJobSuiteStopRequestHandler stopRequestHandler;
    
    /** Unique identifier for this job suite. */
    private String namespace;

    private final Map<String, IJobContext> jobContexts = 
            new HashMap<String, IJobContext>();
    
    /**
     * Creates a new job suite using a {@link FileLogManager} and
     * a {@link JobProgressPropertiesFileSerializer}.
     * @param job root job for the suite
     */
    public JobSuite(final IJob job) {
        this(job, new JobProgressPropertiesFileSerializer(getDefaultWorkDir()));
    }
    /**
     * Creates a new job suite using a {@link FileLogManager}.
     * @param job root job for the suite
     * @param progressSerializer job progress serializer
     */
    public JobSuite(
            final IJob job, final IJobProgressSerializer progressSerializer) {
        this(job, progressSerializer, new FileLogManager(getDefaultWorkDir()));
    }
    /**
     * Creates a new job suite using a
     *  {@link JobProgressPropertiesFileSerializer}.
     * @param job root job for the suite
     * @param logManager Log4J log manager
     * @since 1.1.1
     */
    public JobSuite(
            final IJob job,
            final ILogManager logManager) {
        this(job, new JobProgressPropertiesFileSerializer(
                getDefaultWorkDir()), logManager);
    }
    /**
     * Creates a new job suite.
     * @param job root job for the suite
     * @param progressSerializer job progress serializer
     * @param logManager Log4J log manager
     */
    public JobSuite(
            final IJob job,
            final IJobProgressSerializer progressSerializer,
            final ILogManager logManager) {
        this(job, progressSerializer, logManager, 
                new FileStopRequestHandler(
                        job.getId(), getDefaultWorkDir()));
        
    }
    /**
     * Creates a new job suite.
     * @param job root job for the suite
     * @param progressSerializer job progress serializer
     * @param logManager Log4J log manager
     */
    public JobSuite(
            final IJob job,
            final IJobProgressSerializer progressSerializer,
            final ILogManager logManager,
            final IJobSuiteStopRequestHandler stopRequestAdviser) {
        super();
        this.stopRequestHandler = stopRequestAdviser;
        if (job == null) {
            throw new IllegalArgumentException("Job cannot be null.");
        }
        if (progressSerializer == null) {
            throw new IllegalArgumentException(
                    "Progress serializer cannot be null.");
        }
        if (logManager == null) {
            throw new IllegalArgumentException(
                    "Log manager cannot be null.");
        }
        this.rootJob = job;
        this.progressSerializer = progressSerializer;
        this.namespace = job.getId();
        this.logManager = logManager;
        

        loadJobIds(job);

        // Add serialization support.
        addJobProgressListener(new JobProgressStateChangeAdapter() {
            @Override
            public void jobStateChanged(final IJobStatus progress) {
                serialize(progress);
            }
            @Override
            public void jobRunningVerified(final IJobStatus progress) {
                serialize(progress);
            }
            private void serialize(final IJobStatus progress) {
                try {
                    getJobProgressSerializer().serialize(namespace, progress);
                } catch (IOException e) {
                    throw new JobException("Cannot serialize progress", e);
                }
            }
        });
    }

    /**
     * Gets the job suite status.  This is equivalent of getting the 
     * status on the root job for this suite.
     * @return suite status
     */
    public IJobStatus.Status getSuiteStatus() {
        return getJobProgress(getRootJob()).getStatus();
    }
    
    /**
     * Gets the suite root job.
     * @return suite root job
     */
    public IJob getRootJob() {
        return rootJob;
    }

    public IJobContext getJobContext(String jobId) {
        return jobContexts.get(jobId);
    }
    public IJobContext getJobContext(IJob job) {
        return jobContexts.get(job.getId());
    }
    
    /**
     * Gets all job identifiers in order provided by the suite hierarchy.
     * @return job unique identifiers
     */
    public String[] getJobIds() {
        return jobIds.toArray(new String[]{});
    }

    public IJobSuiteStopRequestHandler getStopRequestHandler() {
        return stopRequestHandler;
    }
    /**
     * Loads all job identifiers.
     * @param parentJob parent job, to grab identifiers from
     */
    private void loadJobIds(final IJob parentJob) {
        if (jobIds.contains(parentJob.getId())) {
            throw new IllegalArgumentException("Job suite '" + rootJob.getId()
                    + "' contains two or more jobs with the same id: '"
                    + parentJob.getId() + "'.");
        }
        jobIds.add(parentJob.getId());
        jobByIds.put(parentJob.getId(), parentJob);
        jobContexts.put(parentJob.getId(), 
                new JobContext(parentJob.createJobContext()));
        if (parentJob instanceof IJobGroup) {
            IJob[] jobs = ((IJobGroup) parentJob).getJobs();
            for (int i = 0; i < jobs.length; i++) {
                loadJobIds(jobs[i]);
            }
        }
    }

    /**
     * Gets the namespace (unique identifier) associated with this suite.
     * This is the job id of the root job.
     * @return namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Adds a job progress listener.
     * @param listener job progress listener to add
     */
    public void addJobProgressListener(final IJobProgressListener listener) {
        synchronized (progressListeners) {
            progressListeners.add(0, listener);
        }
    }
    /**
     * Removes a job progress listener.
     * @param listener job progress listener to remove
     */
    public void removeJobProgressListener(final IJobProgressListener listener) {
        synchronized (progressListeners) {
            progressListeners.remove(listener);
        }
    }
    /**
     * Gets all job progress listeners.
     * @return all job progress listeners
     */
    public IJobProgressListener[] getJobProgressListeners() {
        synchronized (progressListeners) {
            return progressListeners.toArray(EMPTY_PROGRESS_LISTENERS);
        }
    }

    /**
     * Adds a job progress listener.
     * @param listener job progress listener to add
     */
    public void addSuiteStopRequestListener(
            final ISuiteStopRequestListener listener) {
        synchronized (stopListeners) {
            stopListeners.add(0, listener);
        }
    }
    /**
     * Removes a job progress listener.
     * @param listener job progress listener to remove
     */
    public void removeSuiteStopRequestListener(
            final ISuiteStopRequestListener listener) {
        synchronized (stopListeners) {
            stopListeners.remove(listener);
        }
    }
    /**
     * Gets all job progress listeners.
     * @return all job progress listeners
     */
    public ISuiteStopRequestListener[] getSuiteStopRequestListeners() {
        synchronized (stopListeners) {
            return stopListeners.toArray(EMPTY_STOP_LISTENERS);
        }
    }
    
    /**
     * Adds a suite life cycle listener.
     * @param listener suite life cycle listener to add
     */
    public void addSuiteLifeCycleListener(
            final ISuiteLifeCycleListener listener) {
        synchronized (suiteListeners) {
            suiteListeners.add(0, listener);
        }
    }
    /**
     * Removes a suite life cycle listener.
     * @param listener suite life cycle listener to remove
     */
    public void removeSuiteLifeCycleListener(
            final ISuiteLifeCycleListener listener) {
        synchronized (suiteListeners) {
            suiteListeners.remove(listener);
        }
    }
    /**
     * Gets all suite life cycle listeners.
     * @return all suite life cycle listeners
     */
    public ISuiteLifeCycleListener[] getSuiteLifeCycleListeners() {
        synchronized (suiteListeners) {
            return suiteListeners.toArray(EMPTY_SUITE_LISTENERS);
        }
    }

    /**
     * Adds an error handler.
     * @param handler error handler to add
     */
    public void addErrorHandler(final IErrorHandler handler) {
        synchronized (errorHandlers) {
            errorHandlers.add(0, handler);
        }
    }
    /**
     * Removes an error handler.
     * @param handler error handler to remove
     */
    public void removeErrorHandler(final IErrorHandler handler) {
        synchronized (errorHandlers) {
            errorHandlers.remove(handler);
        }
    }
    /**
     * Gets all error handlers.
     * @return all error handlers
     */
    public IErrorHandler[] getErrorHandlers() {
        synchronized (errorHandlers) {
            return errorHandlers.toArray(EMPTY_ERROR_HANDLERS);
        }
    }

    /**
     * Gets the job progress serializer.
     * @return job progress serializer
     */
    public IJobProgressSerializer getJobProgressSerializer() {
        return progressSerializer;
    }

    /**
     * Gets the Log4J log manager.
     * @return Log4J log manager
     */
    public ILogManager getLogManager() {
        return logManager;
    }

    /**
     * Gets the job progress associated with one of the suite's job.
     * @param job the job for which to get its progress
     * @return a job progress
     * @since 1.1
     */
    public IJobStatus getJobProgress(IJob job) {
        try {
            return getJobProgressSerializer().deserialize(
                    namespace, job.getId(), getJobContext(job));
        } catch (IOException e) {
            throw new JobException("Cannot deserialize job progress for job: "
                    + job.getId(), e);
        }
    }

    /**
     * Gets the job progress associated with one of the suite's job.
     * @param jobId the job identifier of the job for which to get progress
     * @return a job progress
     * @since 1.1
     */
    public IJobStatus getJobProgress(String jobId) {
        if (jobId == null) {
            return null;
        }
        return getJobProgress(getJob(jobId));
    }

    /**
     * Gets the job instance matching the given job identifier.
     * @param jobId unique job identifier
     * @return a job
     * @since 1.1
     */
    public IJob getJob(String jobId) {
        return jobByIds.get(jobId);
    }

    /**
     * Stops this job suite.
     * @since 2.0
     */
    public void stop() {
        final IJobStatus progress = getJobProgress(getRootJob());
        for (ISuiteLifeCycleListener listener : suiteListeners) {
            listener.suiteStopping(this);
        }
        for (IJobProgressListener listener : progressListeners) {
            listener.jobStopping(progress);
        }

        accept(new AbstractJobSuiteVisitor() {
            @Override
            public void visitJob(final IJob job) {
                final IJobStatus jobProgress = getJobProgress(job);
                new Thread(){
                    @Override
                    public void run() {
                        stopJob(job, jobProgress);
                    }
                }.start();
            }
        });
    }

    
    
    /**
     * Accepts a job suite visitor.
     * @param visitor job suite visitor
     * @since 1.1
     */
    public void accept(IJobSuiteVisitor visitor) {
        accept(visitor, null);
    }
    /**
     * Accepts a job suite visitor, filtering jobs and job progresses to
     * those of the same type as the specified job class instance.
     * @param visitor job suite visitor
     * @param jobFilterClass type to filter jobs and job progresses
     * @since 1.1
     */
    public void accept(IJobSuiteVisitor visitor, Class<IJob> jobFilterClass) {
        visitor.visitJobSuite(this);
        String[] ids = getJobIds();
        for (int i = 0; i < ids.length; i++) {
            String jobId = ids[i];
            IJob ajob = getJob(jobId);
            if (jobFilterClass == null || jobFilterClass.isInstance(ajob)) {
                visitor.visitJob(getJob(jobId));
                visitor.visitJobProgress(getJobProgress(jobId));
            }
        }
    }
    
    /**
     * Gets the default path to the JEF working directory for file-system
     * related operations.  The path is determined in one of the following
     * way (in order):
     * <ul>
     *   <li>The system property "jef.job.dir".
     *   <li>The system property "user.home", appended with
     *       "/norconex/jef/jobs"
     * </ul>
     * @return path to default working directory
     */
    public static String getDefaultWorkDir() {
        String jobDir = System.getProperty("jef.job.dir");
        if (jobDir == null) {
            jobDir = System.getProperty("user.home")
                    + "/norconex/jef/jobs";
        }
        File dir = new File(jobDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return jobDir;
    }    
    
    private void stopJob(final IJob job, final IJobStatus progress) {
        job.stop(progress, JobSuite.this);
        while (progress.getStatus()  == IJobStatus.Status.RUNNING) {
            Sleeper.sleepSeconds(STOP_WAIT_DELAY);
        }
        if (progress.getStatus() == IJobStatus.Status.STOPPED) {
            for (IJobProgressListener listener : progressListeners) {
                listener.jobStopped(progress);
            }
            if (job.getId().equals(getNamespace())) {
                for (ISuiteLifeCycleListener listener : suiteListeners) {
                    listener.suiteStopped(JobSuite.this);
                }
            }
        }
    }
}