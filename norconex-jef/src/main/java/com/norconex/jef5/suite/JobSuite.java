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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.norconex.commons.lang.file.FileUtil;
import com.norconex.commons.lang.time.DateUtil;
import com.norconex.jef5.JefException;
import com.norconex.jef5.event.IJefEventListener;
import com.norconex.jef5.event.JefEvent;
import com.norconex.jef5.job.IJob;
import com.norconex.jef5.job.IJobVisitor;
import com.norconex.jef5.job.group.IJobGroup;
import com.norconex.jef5.session.IJobSessionVisitor;
import com.norconex.jef5.session.JobSession;
import com.norconex.jef5.session.JobSessionUpdater;
import com.norconex.jef5.session.JobState;
import com.norconex.jef5.session.NEW.JobSuiteSession;
import com.norconex.jef5.session.NEW.JobSuiteSessionDAO;
import com.norconex.jef5.shutdown.IShutdownHook;
import com.norconex.jef5.shutdown.ShutdownException;
import com.norconex.jef5.shutdown.impl.FileShutdownHook;


//TODO rename JobExecutor and move to root package?

/**
 * A job suite is an amalgamation of jobs, represented as a single executable
 * unit.  It can be seen as of one big job made of several sub-jobs.
 * Configurations applied to a suite affects all jobs associated
 * with the suite.
 * All jobs making up a suite must have unique identifiers.
 * @author Pascal Essiembre
 */
public final class JobSuite {

    private static final Logger LOG = LoggerFactory.getLogger(JobSuite.class);
    
    
    public static final String SESSION_SUBDIR = "session";
    public static final String SESSION_BACKUP_SUBDIR = "backups/session";
    
    /** Associates job id with current thread. */
    private static final ThreadLocal<String> CURRENT_JOB_ID = 
            new InheritableThreadLocal<>();
    
    private final Map<String, IJob> jobs = new HashMap<>();
    private final IJob rootJob;
//    private final JobSuiteConfig config;
    private Path workdir;
    private boolean backupDisabled;
    //TODO rename JobEvent* to just Event*
    private final List<IJefEventListener> eventListeners = new ArrayList<>();
    
    private final JobHeartbeatGenerator heartbeatGenerator;
    
//    private JobSessionFacade jobSessionFacade;
//    private final IJobSessionStore jobSessionStore;
    private JobSuiteSession suiteSession;
    private final JobSuiteSessionDAO suiteSessionDAO;
    
    //TODO consider making configurable?
    //TODO have it optinally implement JefEventListener instead of 
    // setup/destroy methods?  In case one wants to react to whatever
    // event.
    IShutdownHook shutdownHook = new FileShutdownHook();

    public JobSuite(final IJob rootJob) {
        this(rootJob, null);
    }

    public JobSuite(final IJob rootJob, JobSuiteConfig config) {
        super();
        Objects.requireNonNull(rootJob, "rootJob");
        this.rootJob = rootJob;
        JobSuiteConfig cfg = 
                ObjectUtils.defaultIfNull(config, new JobSuiteConfig());
        
        
        //TODO do the remaining as the first thing when execute is called
        // to prevent processing/file creation from happening until
        // actually started.

        //TODO have a reset/clean method so a new execute can start fresh? 
        
        this.workdir = resolveWorkdir(cfg.getWorkdir());
        this.suiteSessionDAO = 
                new JobSuiteSessionDAO(rootJob.getId(), getSessionDir());
//        try {
//            this.suiteSession = JobSuiteSession.getInstance(this);
//        } catch (IOException e) {
//            throw new JefException("Cannot create JEF suite session.", e);
//        }
        this.eventListeners.addAll(cfg.getEventListeners());
        this.heartbeatGenerator = new JobHeartbeatGenerator(this);
        this.backupDisabled = cfg.isBackupDisabled();
        
        accept((job, jobSession) -> jobs.put(job.getId(), job));

        // register listening objects 
        registerListener(rootJob);
    }
    
    
    public Path getSessionDir() {
        return getSessionDir(workdir, getId());
    }
    public static Path getSessionDir(Path suiteWorkdir, String suiteId) {
        return suiteWorkdir.resolve(Paths.get(
                FileUtil.toSafeFileName(suiteId), SESSION_SUBDIR));
    }

    public Path getSessionBackupDir(LocalDateTime date) {
        return getSessionBackupDir(workdir, getId(), date);
    }
    public static Path getSessionBackupDir(
            Path suiteWorkdir, String suiteId, LocalDateTime date) {
        return FileUtil.toDateFormattedDir(suiteWorkdir.resolve(Paths.get(
                FileUtil.toSafeFileName(suiteId), 
                        SESSION_BACKUP_SUBDIR)).toFile(),
                DateUtil.toDate(date), "yyyy/MM/dd/HH-mm-ss").toPath();
    }
    
    public Path getSessionIndex() {
        return getSessionIndex(getSessionDir());
    }    
    /**
     * Gets the path to job suite index.
     * @param sessionDir suite working directory
     * @return file the index file
     */
    public static Path getSessionIndex(Path sessionDir) {
        return sessionDir.resolve("suite.index"); // make it "suite.jef"? 
    }


    public IJob getRootJob() {
        return rootJob;
    }

    public Path getWorkdir() {
        return workdir;
    }
    
    // make package visibility?
    public JobSuiteSessionDAO getJobSuiteSessionDAO() {
        return suiteSessionDAO;
    }
    
    
//    public IJobSessionStore getJobSessionStore() {
//        return jobSessionStore;
//    }
    
    public boolean execute() {
        return execute(false);
    }
    public boolean execute(boolean resumeIfIncomplete) {
        boolean success = false;
        //TODO why catching exception here??? so we report it with status 
        //instead?
        try {
            success = doExecute(resumeIfIncomplete);
        } catch (Throwable e) {
            LOG.error("Job suite execution failed: {}", getId(), e);
        }
        if (!success) {
            fire(JefEvent.SUITE_ABORTED, null, this);
        }
        return success;
    }
    
    private boolean doExecute(boolean resumeIfIncomplete) throws IOException {
        boolean success = false;

        LOG.info("Initialization...");
//        this.jobSessionFacade = resolveJobSessionFacade(resumeIfIncomplete);
        suiteSession = resolveSuiteSession(resumeIfIncomplete);
        
        heartbeatGenerator.start();
        
        shutdownHook.setup(this);
//        StopRequestMonitor stopMonitor = new StopRequestMonitor(this);
//        stopMonitor.start();

        LOG.info("Starting execution.");
        fire(JefEvent.SUITE_STARTED, null, this);
        
        try {
            success = runJob(getRootJob());
        } finally {
//            stopMonitor.stopMonitoring();
            shutdownHook.destroy();
            JobState jobState = suiteSession.getRootSession().getState();
            if (success) {
                if (jobState == JobState.COMPLETED) {
                    fire(JefEvent.SUITE_COMPLETED, null, this);
                } else if (jobState == JobState.PREMATURE_TERMINATION) {
                    fire(JefEvent.SUITE_TERMINATED_PREMATURALY, null, this);
                } else {
                    LOG.error("JobSuite ended but job state does not "
                            + "reflect completion: {}", jobState);
                }
            }
            heartbeatGenerator.terminate();
        }
        return success;
    }

    private void registerListener(Object obj) {
        if (obj == null) {
            return;
        }
        if (obj instanceof IJefEventListener) {
            eventListeners.add((IJefEventListener) obj);
            if (obj instanceof IJobGroup) {
                for (IJob childJob : ((IJobGroup) obj).getJobs()) {
                    registerListener(childJob);;
                }
            }
        }
    }
    
    private JobSuiteSession resolveSuiteSession(boolean resumeIfIncomplete)
            throws IOException {
        
        JobSuiteSession session = 
                JobSuiteSession.getInstance(getSessionIndex());
//        JobSessionFacade facade = JobSessionFacade.get(getSuiteIndexFile());
        
        if (session != null) {
            LOG.info("Previous execution detected.");
            JobSession rootSession = session.getRootSession();
            JobState state = rootSession.getState();
            ensureValidExecutionState(state);
            if (resumeIfIncomplete && !state.isOneOf(
                    JobState.COMPLETED, JobState.PREMATURE_TERMINATION)) {
                LOG.info("Resuming from previous execution.");
//TODO fix this:                prepareStatusTreeForResume(statusTree);
            } else {
                // Back-up so we can start clean
                //TODO only backup if backup dir set...
                if (backupDisabled) {
                    LOG.info("Deleting previous execution session.");
                    suiteSessionDAO.delete();
                    //deleteSuite(facade);
                } else {
                    LOG.info("Backing up previous execution session.");
                    backupSuite(session);
                }
                session = null;
            }
        } else {
            LOG.info("No previous execution detected.");
        }
        if (session == null) {
            session = JobSuiteSession.getInstance(this);
            session.toXML(getSessionIndex());
//            writeJobSuiteIndex();
//            facade = JobSessionFacade.get(getSuiteIndexFile());
        }
        return session;
    }
        
    
    
    /**
     * Gets the job status for the root job.  Has the same effect as invoking
     * <code>getJobStatus(getRootJob())</code>.
     * @return root job status
     */
    public JobSession getRootSession() {
        return getJobSession(getRootJob());
    }
    
    public JobSession getJobSession(IJob job) {
        if (job == null) {
            return null;
        }
        return getJobSession(job.getId());
    }
    public JobSession getJobSession(String jobId) {
        if (suiteSession != null) {
            return suiteSession.getSession(jobId);
        }
        return null;
//        try {
//            Path indexFile = getSuiteIndexFile();
//            JobSessionFacade snapshot = 
//                    JobSessionFacade.get(indexFile);
//            if (snapshot != null) {
//                return snapshot.getSession(jobId);
//            }
//            return null;
//        } catch (IOException e) {
//            throw new JefException("Cannot obtain job session.", e);
//        }
    }
    
    
    public void accept(IJobSessionVisitor visitor) {
        suiteSession.accept(visitor);
    }
    
    /**
     * Accepts a job suite visitor.
     * @param visitor job suite visitor
     */
    public void accept(IJobVisitor visitor) {
        accept(visitor, null);
    }
    /**
     * Accepts a job suite visitor, filtering jobs and job progresses to
     * those of the same type as the specified job class instance.
     * @param visitor job suite visitor
     * @param jobClassFilter type to filter jobs and job progresses
     */
    public void accept(IJobVisitor visitor, Class<IJob> jobClassFilter) {
        accept(visitor, getRootJob(), jobClassFilter);
    }    

    private void accept(
            IJobVisitor visitor, IJob job, Class<IJob> jobClassFilter) {
        if (job == null) {
            return;
        }
        if (jobClassFilter == null || jobClassFilter.isInstance(job)) {
            JobSession jobSession = getJobSession(job);
            visitor.accept(job, jobSession);
        }
        if (job instanceof IJobGroup) {
            for (IJob childJob : ((IJobGroup) job).getJobs()) {
                accept(visitor, childJob, jobClassFilter);
            }
        }
    }
    
    /**
     * Gets the job identifier representing the currently running job for the
     * current thread.
     * @return job identifier or <code>null</code> if no job is currently
     *         associated with the current thread
     */
    public static String getRunningJobId() {
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
    
    public String getId() {
        IJob job = getRootJob();
        if (job != null) {
            return job.getId();
        }
        return null;
    }
    
//    /**
//     * Gets the latest index file created for a job suite (if one exists).
//     * @param suiteWorkdir suite working directory
//     * @param suiteId suite unique ID (ID of the root job)
//     * @return file the index file
//     */
//    public static Path getSuiteIndexFile(
//            Path suiteWorkdir, String suiteId) {
//        return suiteWorkdir.resolve(
//                FileUtil.toSafeFileName(suiteId) + ".index");
//    }
//    
//    public Path getSuiteIndexFile() {
//        Path indexFile = getSuiteIndexFile(workdir, getId());
//        if (!indexFile.toFile().exists()) {
//            Path indexDir = indexFile.getParent();
//            if (!indexDir.toFile().exists()) {
//                try {
//                    Files.createDirectories(indexDir);
//                } catch (IOException e) {
//                    throw new JefException(
//                            "Cannot create index directory: " + indexDir, e);
//                }
//            }
//        }
//        return indexFile;
//    }
    
    
//    /*default*/ File getSuiteStopFile() {
//        return new File(workdir + File.separator 
//                + "latest" + File.separator 
//                + FileUtil.toSafeFileName(getId()) + ".stop");
//    }

    
    //TODO document this is not a public method?
    //TODO Wrap this logic in a JobRunner class, passing it to job groups?
    public boolean runJob(final IJob job) {
        if (job == null) {
            throw new IllegalArgumentException("Job cannot be null.");
        }
        if (StringUtils.isBlank(job.getId())) {
            throw new IllegalArgumentException("Job id cannot be blank.");
        }
        
        boolean success = false;
        Thread.currentThread().setName(job.getId());
        setCurrentJobId(job.getId());
        
        JobSession jobSession = suiteSession.getSession(job);
        if (jobSession.getState() == JobState.COMPLETED) {
            LOG.info("Job skipped: " + job.getId() + " (already completed)");
            fire(JefEvent.JOB_SKIPPED, jobSession, job);
            return true;
        }

        boolean errorHandled = false;
        try {
            if (!jobSession.isResumed()) {
                jobSession.setStartTime(LocalDateTime.now());
                LOG.info("Running {}: START ({})", 
                        job.getId(), LocalDateTime.now());
                fire(JefEvent.JOB_STARTED, jobSession, job);
            } else {
                LOG.info("Running {}: RESUME ({})", 
                        job.getId(), jobSession.getStartTime());  
                fire(JefEvent.JOB_RESUMED, jobSession, job);
                jobSession.setEndTime(null);
                jobSession.setNote("");  
            }

            heartbeatGenerator.register(jobSession);
            //--- Execute ---
            job.execute(new JobSessionUpdater(jobSession, js -> {
                try {
                    suiteSessionDAO.write(js);
                } catch (IOException e) {
                    throw new JefException(
                            "Cannot persist status update for job: "
                                    + js.getJobId(), e);
                }
                fire(JefEvent.JOB_PROGRESSED, js, job);
                JobSession parentSession = 
                        suiteSession.getParentSession(js);
                if (parentSession != null) {
                    IJobGroup jobGroup = 
                            (IJobGroup) jobs.get(parentSession.getJobId());
                    if (jobGroup != null) {
                        jobGroup.groupProgressed(js);
                    }
                }
            }), this);
            success = true;
        } catch (Exception e) {
            success = false;
            LOG.error("Execution failed for job: " + job.getId(), e);
            fire(JefEvent.JOB_ERROR, jobSession, job, e);
            if (jobSession != null) {
                jobSession.setNote("Error occured: " + e.getLocalizedMessage());
            }
            errorHandled = true;
            //System.exit(-1)
        } finally {
            heartbeatGenerator.unregister(jobSession);
            jobSession.setEndTime(LocalDateTime.now());
            try {
                suiteSessionDAO.write(jobSession);
            } catch (IOException e) {
                LOG.error("Cannot save final status.", e);
            }
            if (!success && !errorHandled) {
                LOG.error("Fatal error occured in job: {}.", job.getId());
            }
            LOG.info("Running " + job.getId()  
                    + ": END (" + jobSession.getStartTime() + ")");
            
            // If stopping or stopped, corresponding events will have been
            // fired already and we do not fire additional ones.
            if (jobSession.getState() != JobState.STOPPING
                    && jobSession.getState() != JobState.STOPPED) {
                if (success) {
                    fire(JefEvent.JOB_COMPLETED, jobSession, job);
                } else {
                    fire(JefEvent.JOB_TERMINATED_PREMATURALY, jobSession, job);
                }
            }
        }
        return success;
    }
    
    public void stop() throws ShutdownException {
        shutdownHook.shutdown(getSessionIndex());
//        if (!getSuiteStopFile().createNewFile()) {
//            throw new IOException(
//                    "Could not create stop file: " + getSuiteStopFile());
//        }
    }
    public static void stop(Path indexFile) throws ShutdownException {
        //TODO if configurable, grab hook impl. from index file.
        new FileShutdownHook().shutdown(indexFile);
    }
    
    
//    public static void stop(File indexFile) throws IOException {
//        if (indexFile == null || !indexFile.exists() || !indexFile.isFile()) {
//            throw new JefException("Invalid index file: " + indexFile);
//        }
//        String stopPath = 
//                StringUtils.removeEnd(indexFile.getAbsolutePath(), "index");
//        stopPath += ".stop";
//        if (!new File(stopPath).createNewFile()) {
//            throw new IOException(
//                    "Could not create stop file: " + stopPath);
//        }        
//    }
    


    
    //TODO is below still required once we handle resumes differently???
    
    
    
//    // This preparation is required otherwise, stopping of a resumed job
//    // will fail, because of previous "stopRequested" flag being set.
//    // "resumeAttempts" on the root must be incremented for resume to work, 
//    // but technically the root attempts should always be incremented whenever
//    // there is at least one child job that needs to be incremented.
//    // This method fixes: https://github.com/Norconex/collector-http/issues/69
//    private void prepareStatusTreeForResume(JobSuiteStatusSnapshot statusTree) {
//        statusTree.accept(new IJobStatusVisitor() {
//            @Override
//            public void visitJobStatus(JobSessionStatus status) {
//                status.setStopRequested(false);
//                JobDuration duration = status.getDuration();
//                if (status.isStarted() && !status.isCompleted()) {
//throw new RuntimeException("re-Implement this");
////TODO fix this.....
////                    status.incrementResumeAttempts();
////                    if (duration != null) {
////                        duration.setResumedStartTime(
////                                duration.getStartTime());
////                        duration.setResumedLastActivity(
////                                status.getLastActivity());
////                    }
//                }
//            }
//        });
//    }
    
    private void ensureValidExecutionState(JobState state) {
        if (state == JobState.RUNNING) {
            throw new JefException("JOB SUITE ALREADY RUNNING. There is "
                    + "already an instance of this job suite running. "
                    + "Either stop it, or wait for it to complete.");
        }
        if (state == JobState.STOPPING) {
            throw new JefException("JOB SUITE STOPPING. "
                    + "There is an instance of this job suite currently "
                    + "stopping.  Wait for it to stop, or terminate the "
                    + "process.");
        }
    }
    
    private void backupSuite(JobSuiteSession suiteSession) { // throws IOException {
        JobSession suiteStatus = suiteSession.getRootSession();
        LocalDateTime backupDate = suiteStatus.getEndTime();
        if (backupDate == null) {
            backupDate = suiteStatus.getLastActivity();
        }
        if (backupDate == null) {
            backupDate = LocalDateTime.now();
        }
        try {
            suiteSessionDAO.backup(getSessionBackupDir(backupDate));
            
//            // Backup status files
//            jobSessionStore.backup(getId(), backupDate);
//
//            // Backup suite index
//            Path indexFile = getSuiteIndexFile();
//            Path backupFile = FileUtil.toDateFormattedDir(
//                    workdir.resolve(FileUtil.toSafeFileName(getId())).toFile(), 
//                    DateUtil.toDate(
//                            backupDate), "yyyy/MM/dd/HH-mm-ss").toPath();
//            Files.move(indexFile, backupFile);
            
//            String date = new SimpleDateFormat(
//                    "yyyyMMddHHmmssSSSS").format(DateUtil.toDate(backupDate));
//            Path indexFile = getSuiteIndexFile();
//
//            Path backupDir = FileUtil.createDateDirs(workdir.resolve(
//                    "backups").toFile(), DateUtil.toDate(backupDate)).toPath();
//            Files.createDirectories(backupDir);
//            Path backupFile = backupDir.resolve(
//                    date + "_" + indexFile.getFileName());
//            Files.move(indexFile, backupFile);
        } catch (IOException e) {
            throw new JefException("Suite session backup unsuccessful.", e);
        }
    }

//    private void deleteSuite(JobSessionFacade facade) {// throws IOException {
//        try {
//            jobSessionStore.remove(getId());
//            Path indexFile = getSuiteIndexFile();
//            Files.delete(indexFile);
//        } catch (IOException e) {
//            throw new JefException("Suite session delete unsuccessful.", e);
//        }

//        try {
//            Path backupDir = FileUtil.createDateDirs(workdir.resolve(
//                    "backup").toFile(), DateUtil.toDate(backupDate)).toPath();
//            Files.createDirectories(backupDir);
//            Path backupFile = backupDir.resolve(
//                    date + "_" + indexFile.getFileName());
//            Files.move(indexFile, backupFile);
//        } catch (IOException e) {
//            throw new JefException("Could not backup suite index.", e);
//        }
//    }
    
    
//    //TODO move these writeXX methods to JobSessionFacade??
//    private void writeJobSuiteIndex() 
//            throws IOException {
//        
//        Path indexFile = getSuiteIndexFile();
//        
//        StringWriter out = new StringWriter();
//        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
//        out.write("<suite-index>");
//            
//        //--- JobStatusSerializer ---
//        out.flush();
//        if (jobSessionStore instanceof IXMLConfigurable) {
//            ((IXMLConfigurable) jobSessionStore).saveToXML(out);
//        }
//        
//        //--- Jobs ---
//        writeJobSuiteIndexJob(out, rootJob);
//        
//        out.write("</suite-index>");
//        out.flush();
//        
//        // Using RandomAccessFile since evidence has shown it is better at 
//        // dealing with files/locks in a way that cause less/no errors.
//        try (RandomAccessFile ras = 
//                new RandomAccessFile(indexFile.toFile(), "rwd");
//                FileChannel channel = ras.getChannel();
//                FileLock lock = channel.lock()) {
//            ras.writeUTF(out.toString());
//        }
//    }
//    private void writeJobSuiteIndexJob(
//            Writer out, IJob job) throws IOException {
//        out.write("<job id=\"");
//        out.write(StringEscapeUtils.escapeXml11(job.getId()));
//        out.write("\">");
//        if (job instanceof IJobGroup) {
//            for (IJob childJob: ((IJobGroup) job).getJobs()) {
//                writeJobSuiteIndexJob(out, childJob);
//            }
//        }
//        out.write("</job>");
//    }
    
    private Path resolveWorkdir(Path configWorkdir) {
        // Default to working directory??
        Path dir = configWorkdir;
        if (configWorkdir == null) {
            dir = Paths.get(".");
        }
        if (!dir.toFile().exists()) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw new JefException(
                        "Cannot create work directory: " + dir, e);
            }
        } else if (!dir.toFile().isDirectory()) {
            throw new JefException("Invalid work directory: " + dir);
            
        }
        LOG.info("JEF work directory is: {}", dir.toAbsolutePath());
        return dir;
    }

//    private IJobSessionStore resolveJobSessionStore(IJobSessionStore store) {
//        IJobSessionStore s = store;
//        if (s == null) {
//            s = new FileJobSessionStore(workdir);
//        }
//        LOG.info("JEF job status store is {}", s.getClass().getSimpleName());
//        return s;
//    }
    
    private void fire(String eventName, JobSession status, Object source) {
        fire(eventName, status, source, null);
    }
    private void fire(String eventName, JobSession status, 
            Object source, Throwable exception) {
        fire(new JefEvent(eventName, status, source, exception));
    }
    public void fire(JefEvent event) {
        if (event == null) {
            return;
        }
        for (IJefEventListener l : eventListeners) {
            l.accept(event);
        }
    }
}