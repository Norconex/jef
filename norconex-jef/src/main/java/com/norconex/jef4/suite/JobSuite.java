/* Copyright 2010-2020 Norconex Inc.
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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.norconex.commons.lang.file.FileUtil;
import com.norconex.commons.lang.log.Log4jCheck;
import com.norconex.jef4.JEFException;
import com.norconex.jef4.JEFUtil;
import com.norconex.jef4.job.IJob;
import com.norconex.jef4.job.IJobErrorListener;
import com.norconex.jef4.job.IJobLifeCycleListener;
import com.norconex.jef4.job.IJobVisitor;
import com.norconex.jef4.job.JobErrorEvent;
import com.norconex.jef4.job.JobException;
import com.norconex.jef4.job.group.IJobGroup;
import com.norconex.jef4.log.FileLogManager;
import com.norconex.jef4.log.ILogManager;
import com.norconex.jef4.log.ThreadSafeLayout;
import com.norconex.jef4.status.FileJobStatusStore;
import com.norconex.jef4.status.IJobStatus;
import com.norconex.jef4.status.IJobStatusStore;
import com.norconex.jef4.status.IJobStatusVisitor;
import com.norconex.jef4.status.JobDuration;
import com.norconex.jef4.status.JobState;
import com.norconex.jef4.status.JobStatusUpdater;
import com.norconex.jef4.status.JobSuiteStatusSnapshot;
import com.norconex.jef4.status.MutableJobStatus;

/**
 * A job suite is an amalgamation of jobs, represented as a single executable
 * unit.  It can be seen as of one big job made of several sub-jobs.
 * Configurations applied to a suite affects all jobs associated
 * with the suite.
 * All jobs making up a suite must have unique identifiers.
 * @author Pascal Essiembre
 */
public final class JobSuite {

    private static final Logger LOG = LogManager.getLogger(JobSuite.class);

    /** Associates job id with current thread. */
    private static final ThreadLocal<String> CURRENT_JOB_ID =
            new InheritableThreadLocal<>();

    private final Map<String, IJob> jobs = new HashMap<>();
    private final IJob rootJob;
    private final JobSuiteConfig config;
    private final String workdir;
    private final ILogManager logManager;
    private final IJobStatusStore jobStatusStore;
    private JobSuiteStatusSnapshot jobSuiteStatusSnapshot;
    private final List<IJobLifeCycleListener> jobLifeCycleListeners;
    private final List<IJobErrorListener> jobErrorListeners;
    private final List<ISuiteLifeCycleListener> suiteLifeCycleListeners;
    private final JobHeartbeatGenerator heartbeatGenerator;


    public JobSuite(final IJob rootJob) {
        this(rootJob, new JobSuiteConfig());
    }

    public JobSuite(final IJob rootJob, JobSuiteConfig config) {
        super();
        this.rootJob = rootJob;
        this.config = config;
        this.workdir = resolveWorkdir(config.getWorkdir());
        this.logManager = resolveLogManager(config.getLogManager());
        this.jobStatusStore =
                resolveJobStatusStore(config.getJobStatusStore());
        this.jobLifeCycleListeners =
                Collections.unmodifiableList(config.getJobLifeCycleListeners());
        this.suiteLifeCycleListeners = Collections.unmodifiableList(
                config.getSuiteLifeCycleListeners());
        this.jobErrorListeners =
                Collections.unmodifiableList(config.getJobErrorListeners());
        this.heartbeatGenerator = new JobHeartbeatGenerator(this);

        accept(new IJobVisitor() {
            @Override
            public void visitJob(IJob job, IJobStatus jobStatus) {
                jobs.put(job.getId(), job);
            }
        });
    }

    public IJob getRootJob() {
        return rootJob;
    }
    public JobSuiteConfig getConfig() {
        return config;
    }

    /**
     * Gets the job status for the root job.  Has the same effect as invoking
     * <code>getJobStatus(getRootJob())</code>.
     * @return root job status
     */
    public IJobStatus getStatus() {
        return getJobStatus(getRootJob());
    }

    public IJobStatus getJobStatus(IJob job) {
        if (job == null) {
            return null;
        }
        return getJobStatus(job.getId());
    }
    public IJobStatus getJobStatus(String jobId) {
        if (jobSuiteStatusSnapshot != null) {
            return jobSuiteStatusSnapshot.getJobStatus(jobId);
        }
        try {
            File indexFile = JEFUtil.getSuiteIndexFile(getWorkdir(), getId());
            JobSuiteStatusSnapshot snapshot =
                    JobSuiteStatusSnapshot.newSnapshot(indexFile);
            if (snapshot != null) {
                return snapshot.getJobStatus(jobId);
            }
            return null;
        } catch (IOException e) {
            throw new JEFException("Cannot obtain suite status.", e);
        }
    }

    public boolean execute() {
        return execute(false);
    }
    public boolean execute(boolean resumeIfIncomplete) {
        boolean success = false;
        try {
            success = doExecute(resumeIfIncomplete);
        } catch (Throwable e) {
            LOG.fatal("Job suite execution failed: " + getId(), e);
        }
        if (!success) {
            fire(suiteLifeCycleListeners, "suiteAborted", this);
        }
        return success;
    }


    public void accept(IJobStatusVisitor visitor) {
        jobSuiteStatusSnapshot.accept(visitor);
    }

    /**
     * Accepts a job suite visitor.
     * @param visitor job suite visitor
     * @since 1.1
     */
    public void accept(IJobVisitor visitor) {
        accept(visitor, null);
    }
    /**
     * Accepts a job suite visitor, filtering jobs and job progresses to
     * those of the same type as the specified job class instance.
     * @param visitor job suite visitor
     * @param jobClassFilter type to filter jobs and job progresses
     * @since 1.1
     */
    public void accept(IJobVisitor visitor, Class<IJob> jobClassFilter) {
        accept(visitor, getRootJob(), jobClassFilter);
    }


    /**
     * Gets the job identifier representing the currently running job for the
     * current thread.
     * @return job identifier or <code>null</code> if no job is currently
     *         associated with the current thread
     */
    public static String getCurrentJobId() {
        return CURRENT_JOB_ID.get();
    }
    /**
     * Sets a job identifier as the currently running job for the
     * the current thread.  This method is called by the framework.
     * Framework users may call this method when implementing their own
     * threads to associated a job with the thread.  Framework code
     * may rely on this to behave as expected.  Otherwise, it is best
     * advised not to use this method.
     * @param jobId job identifier
     */
    public static void setCurrentJobId(String jobId) {
        CURRENT_JOB_ID.set(jobId);
    }

    /*default*/ IJobStatusStore getJobStatusStore() {
        return jobStatusStore;
    }
    public String getId() {
        IJob job = getRootJob();
        if (job != null) {
            return job.getId();
        }
        return null;
    }
    public String getWorkdir() {
        return workdir;
    }
    public ILogManager getLogManager() {
        return logManager;
    }
    /*default*/ File getSuiteIndexFile() {
        File indexFile = JEFUtil.getSuiteIndexFile(getWorkdir(), getId());
        if (!indexFile.exists()) {
            File indexDir = indexFile.getParentFile();
            if (!indexDir.exists()) {
                try {
                    FileUtils.forceMkdir(indexDir);
                } catch (IOException e) {
                    throw new JEFException("Cannot create index directory: "
                            + indexDir, e);
                }
            }
        }
        return indexFile;
    }
    /*default*/ File getSuiteStopFile() {
        return new File(getWorkdir() + File.separator
                + "latest" + File.separator
                + FileUtil.toSafeFileName(getId()) + ".stop");
    }
    /*default*/ List<IJobLifeCycleListener> getJobLifeCycleListeners() {
        return jobLifeCycleListeners;
    }
    /*default*/ List<IJobErrorListener> getJobErrorListeners() {
        return jobErrorListeners;
    }
    /*default*/ List<ISuiteLifeCycleListener> getSuiteLifeCycleListeners() {
        return suiteLifeCycleListeners;
    }

    private boolean doExecute(boolean resumeIfIncomplete) throws IOException {
        boolean success = false;

        LOG.info("Initialization...");

        //--- Initialize ---
        initialize(resumeIfIncomplete);

        //--- Add Log Appender ---
        Appender appender = getLogManager().createAppender(getId());
        if (appender != null) {
            appender.setLayout(new ThreadSafeLayout(appender.getLayout()));
            Logger.getRootLogger().addAppender(appender);
        }

        heartbeatGenerator.start();

        StopRequestMonitor stopMonitor = new StopRequestMonitor(this);
        stopMonitor.start();

        LOG.info("Starting execution.");
        fire(suiteLifeCycleListeners, "suiteStarted", this);

        try {
            success = runJob(getRootJob());
        } finally {
            stopMonitor.stopMonitoring();
            JobState jobState = jobSuiteStatusSnapshot.getRoot().getState();
            if (success) {
                if (jobState == JobState.COMPLETED) {
                    fire(suiteLifeCycleListeners, "suiteCompleted", this);
                } else if (jobState == JobState.PREMATURE_TERMINATION) {
                    fire(suiteLifeCycleListeners,
                            "suiteTerminatedPrematuraly", this);
                } else {
                    LOG.error("JobSuite ended but job state does not "
                            + "reflect completion: " + jobState);
                }
            }

            // Remove appender
            if (appender != null) {
                appender.close();
                if (Log4jCheck.present()) {
                    Logger.getRootLogger().removeAppender(appender);
                }
            }
            heartbeatGenerator.terminate();
        }

        return success;
    }

    //TODO document this is not a public method?
    public boolean runJob(final IJob job) {
        if (job == null) {
            throw new IllegalArgumentException("Job cannot be null.");
        }
        boolean success = false;
        Thread.currentThread().setName(job.getId());
        setCurrentJobId(job.getId());

        MutableJobStatus status =
                (MutableJobStatus) jobSuiteStatusSnapshot.getJobStatus(job);
        if (status.getState() == JobState.COMPLETED) {
            LOG.info("Job skipped: " + job.getId() + " (already completed)");
            fire(jobLifeCycleListeners, "jobSkipped", status);
            return true;
        }

        boolean errorHandled = false;
        try {
            if (status.getResumeAttempts() == 0) {
                status.getDuration().setStartTime(new Date());
                LOG.info("Running " + job.getId() + ": BEGIN ("
                        + status.getDuration().getStartTime() + ")");
                fire(jobLifeCycleListeners, "jobStarted", status);
            } else {
                LOG.info("Running " + job.getId()
                        + ": RESUME (" + new Date() + ")");
                fire(jobLifeCycleListeners, "jobResumed", status);
                status.getDuration().setEndTime(null);
                status.setNote("");
            }

            heartbeatGenerator.register(status);
            //--- Execute ---
            job.execute(new JobStatusUpdater(status) {
                @Override
                protected void statusUpdated(MutableJobStatus status) {
                    try {
                        getJobStatusStore().write(getId(), status);
                    } catch (IOException e) {
                        throw new JEFException(
                                "Cannot persist status update for job: "
                                        + status.getJobId(), e);
                    }
                    fire(jobLifeCycleListeners, "jobProgressed", status);
                    IJobStatus parentStatus =
                            jobSuiteStatusSnapshot.getParent(status);
                    if (parentStatus != null) {
                        IJobGroup jobGroup =
                                (IJobGroup) jobs.get(parentStatus.getJobId());
                        if (jobGroup != null) {
                            jobGroup.groupProgressed(status);
                        }
                    }
                }
            }, this);
            success = true;
        } catch (Exception e) {
            success = false;
            LOG.error("Execution failed for job: " + job.getId(), e);
            fire(jobErrorListeners, "jobError",
                    new JobErrorEvent(e, this, status));
            if (status != null) {
                status.setNote("Error occured: " + e.getLocalizedMessage());
            }
            errorHandled = true;
            //System.exit(-1)
        } finally {
            heartbeatGenerator.unregister(status);
            status.getDuration().setEndTime(new Date());
            try {
                getJobStatusStore().write(getId(), status);
            } catch (IOException e) {
                LOG.error("Cannot save final status.", e);
            }
            if (!success && !errorHandled) {
                LOG.fatal("Fatal error occured in job: " + job.getId());
            }
            LOG.info("Running " + job.getId()
                    + ": END (" + status.getDuration().getStartTime() + ")");

            // If stopping or stopped, corresponding events will have been
            // fired already and we do not fire additional ones.
            if (status.getState() != JobState.STOPPING
                    && status.getState() != JobState.STOPPED) {
                if (success) {
                    fire(jobLifeCycleListeners, "jobCompleted", status);
                } else {
                    fire(jobLifeCycleListeners,
                            "jobTerminatedPrematuraly", status);
                }
            }
        }
        return success;
    }

    public void stop() throws IOException {
        if (!getSuiteStopFile().createNewFile()) {
            throw new IOException(
                    "Could not create stop file: " + getSuiteStopFile());
        }
    }

    public static void stop(File indexFile) throws IOException {
        if (indexFile == null || !indexFile.exists() || !indexFile.isFile()) {
            throw new JEFException("Invalid index file: " + indexFile);
        }
        String stopPath =
                StringUtils.removeEnd(indexFile.getAbsolutePath(), "index");
        stopPath += ".stop";
        if (!new File(stopPath).createNewFile()) {
            throw new IOException(
                    "Could not create stop file: " + stopPath);
        }
    }

    private void accept(
            IJobVisitor visitor, IJob job, Class<IJob> jobClassFilter) {
        if (job == null) {
            return;
        }
        if (jobClassFilter == null || jobClassFilter.isInstance(job)) {
            IJobStatus status = null;
            if (jobSuiteStatusSnapshot != null) {
                status = jobSuiteStatusSnapshot.getJobStatus(job);
            }
            visitor.visitJob(job, status);
        }
        if (job instanceof IJobGroup) {
            for (IJob childJob : ((IJobGroup) job).getJobs()) {
                accept(visitor, childJob, jobClassFilter);
            }
        }
    }

    private synchronized void initialize(boolean resumeIfIncomplete)
            throws IOException {

        // Use a a lock file while initializing to fix
        // https://github.com/Norconex/collector-http/issues/634

        File indexFile = getSuiteIndexFile();
        File lockFile = new File(indexFile.getAbsolutePath() + ".lck");
        // If file is not older than 5 seconds, we assume it is already running.
        if (lockFile.exists()) {
            if (FileUtils.isFileNewer(lockFile, System.currentTimeMillis()
                    - JobHeartbeatGenerator.HEARTBEAT_INTERVAL)) {
                throw new JEFException("JOB SUITE ALREADY RUNNING. Wait for "
                        + "previous execution to complete, or stop it.");
            } else {
                // Delete old lock file
                lockFile.delete();
            }
        }

        try (RandomAccessFile raf = new RandomAccessFile(lockFile, "rws");
                FileChannel channel = raf.getChannel()) {

            FileLock lock = channel.tryLock();
            if (lock == null) {
                throw new JEFException("JOB SUITE ALREADY STARTED. Wait for "
                        + "previous execution to complete, or stop it.");
            }

            JobSuiteStatusSnapshot statusTree =
                    JobSuiteStatusSnapshot.newSnapshot(indexFile);

            if (statusTree != null) {
                LOG.info("Previous execution detected.");
                MutableJobStatus status =
                        (MutableJobStatus) statusTree.getRoot();
                JobState state = status.getState();
                ensureValidExecutionState(state);
                if (resumeIfIncomplete && !state.isOneOf(
                        JobState.COMPLETED, JobState.PREMATURE_TERMINATION)) {
                    LOG.info("Resuming from previous execution.");
                    prepareStatusTreeForResume(statusTree);
                } else {
                    // Back-up so we can start clean
                    LOG.info("Backing up previous execution status "
                            + "and log files.");
                    backupSuite(statusTree);
                    statusTree = null;
                }
            } else {
                LOG.info("No previous execution detected.");
            }
            if (statusTree == null) {
                statusTree = JobSuiteStatusSnapshot.create(
                        getRootJob(), getLogManager());
                writeJobSuiteIndex(statusTree);
            }
            this.jobSuiteStatusSnapshot = statusTree;

            lock.release();
        } catch (OverlappingFileLockException e) {
            throw new JEFException(
                    "JOB SUITE ALREADY STARTED by another process.");
        }
        lockFile.deleteOnExit();
    }

    // This preparation is required otherwise, stopping of a resumed job
    // will fail, because of previous "stopRequested" flag being set.
    // "resumeAttempts" on the root must be incremented for resume to work,
    // but technically the root attempts should always be incremented whenever
    // there is at least one child job that needs to be incremented.
    // This method fixes: https://github.com/Norconex/collector-http/issues/69
    private void prepareStatusTreeForResume(JobSuiteStatusSnapshot statusTree) {
        statusTree.accept(new IJobStatusVisitor() {
            @Override
            public void visitJobStatus(IJobStatus jobStatus) {
                MutableJobStatus status = (MutableJobStatus) jobStatus;
                status.setStopRequested(false);
                JobDuration duration = status.getDuration();
                if (status.isStarted() && !status.isCompleted()) {
                    status.incrementResumeAttempts();
                    if (duration != null) {
                        duration.setResumedStartTime(
                                duration.getStartTime());
                        duration.setResumedLastActivity(
                                status.getLastActivity());
                    }
                }
            }
        });
    }

    private void ensureValidExecutionState(JobState state) {
        if (state == JobState.RUNNING) {
            throw new JEFException("JOB SUITE ALREADY RUNNING. There is "
                    + "already an instance of this job suite running. "
                    + "Either stop it, or wait for it to complete.");
        }
        if (state == JobState.STOPPING) {
            throw new JEFException("JOB SUITE STOPPING. "
                    + "There is an instance of this job suite currently "
                    + "stopping.  Wait for it to stop, or terminate the "
                    + "process.");
        }
    }

    private void backupSuite(JobSuiteStatusSnapshot statusTree) throws IOException {
        IJobStatus suiteStatus = statusTree.getRoot();
        Date backupDate = suiteStatus.getDuration().getEndTime();
        if (backupDate == null) {
            backupDate = suiteStatus.getLastActivity();
        }
        if (backupDate == null) {
            backupDate = new Date();
        }
        // Backup status files
        List<IJobStatus> statuses = statusTree.getJobStatusList();
        for (IJobStatus jobStatus : statuses) {
            getJobStatusStore().backup(
                    getId(), jobStatus.getJobId(), backupDate);
        }
        // Backup log
        getLogManager().backup(getId(), backupDate);

        // Backup suite index
        String date = new SimpleDateFormat(
                "yyyyMMddHHmmssSSSS").format(backupDate);
        File indexFile = getSuiteIndexFile();
        File backupDir =  new File(getWorkdir() + File.separator + "backup");
        try {
            backupDir = FileUtil.createDateDirs(backupDir, backupDate);
        } catch (IOException e) {
            throw new JobException("Could not create backup directory for "
                    + "suite index.");
        }
        File backupFile = new File(backupDir.getAbsolutePath(),
                date + "__"  + FileUtil.toSafeFileName(getId()) + ".index");
        if (!indexFile.renameTo(backupFile)) {
            throw new IOException("Could not create backup file: "
                    + backupFile);
        }
    }

    private void writeJobSuiteIndex(JobSuiteStatusSnapshot statusTree)
            throws IOException {

        File indexFile = getSuiteIndexFile();

        StringWriter out = new StringWriter();
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        out.write("<suite-index>");

        //--- Log Manager ---
        out.flush();
        getLogManager().saveToXML(out);

        //--- JobStatusSerializer ---
        out.flush();
        getJobStatusStore().saveToXML(out);

        //--- Job Status ---
        writeJobId(out, statusTree, statusTree.getRoot());

        out.write("</suite-index>");
        out.flush();

        // Using RandomAccessFile since evidence has shown it is better at
        // dealing with files/locks in a way that cause less/no errors.
        try (RandomAccessFile ras = new RandomAccessFile(indexFile, "rwd");
                FileChannel channel = ras.getChannel();
                FileLock lock = channel.lock()) {
            ras.writeUTF(out.toString());
        }
    }

    private void writeJobId(Writer out,
            JobSuiteStatusSnapshot statusTree, IJobStatus status) throws IOException {
        out.write("<job name=\"");
        out.write(StringEscapeUtils.escapeXml10(status.getJobId()));
        out.write("\">");
        for (IJobStatus child : statusTree.getChildren(status)) {
            writeJobId(out, statusTree, child);
        }
        out.write("</job>");
    }

    private String resolveWorkdir(String configWorkdir) {
        File dir;
        if (StringUtils.isBlank(configWorkdir)) {
            dir = JEFUtil.FALLBACK_WORKDIR;
        } else {
            dir = new File(configWorkdir);
            if (dir.exists() && !dir.isDirectory()) {
                dir = JEFUtil.FALLBACK_WORKDIR;
            }
        }
        if (!dir.exists()) {
            try {
                FileUtils.forceMkdir(dir);
            } catch (IOException e) {
                throw new JEFException("Cannot create work directory: "
                        + dir, e);
            }
        }
        LOG.info("JEF work directory is: " + dir);
        return dir.getAbsolutePath();
    }
    private ILogManager resolveLogManager(ILogManager configLogManager) {
        ILogManager lm = configLogManager;
        if (lm == null) {
            lm = new FileLogManager(workdir);
        }
        LOG.info("JEF log manager is : "
                + lm.getClass().getSimpleName());
        return lm;
    }
    private IJobStatusStore resolveJobStatusStore(
            IJobStatusStore configSerializer) {
        IJobStatusStore serial = configSerializer;
        if (serial == null) {
            serial = new FileJobStatusStore(workdir);
        }
        LOG.info("JEF job status store is : "
                + serial.getClass().getSimpleName());
        return serial;
    }

    private void fire(List<?> listeners, String methodName, Object argument) {
        for (Object l : listeners) {
            try {
                MethodUtils.invokeMethod(l, methodName, argument);
            } catch (NoSuchMethodException | IllegalAccessException
                    | InvocationTargetException e) {
                throw new JobException(
                        "Could not fire event \"" + methodName + "\".", e);
            }
        }
    }

}