package com.norconex.jef.factories;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.norconex.jef.AsyncJobGroup;
import com.norconex.jef.IJob;
import com.norconex.jef.JobException;
import com.norconex.jef.JobRunner;
import com.norconex.jef.SyncJobGroup;
import com.norconex.jef.jobs.RecoverableSleepyJob;
import com.norconex.jef.jobs.SleepyJob;
import com.norconex.jef.suite.IJobSuiteFactory;
import com.norconex.jef.suite.JobSuite;

public class BigRecoverySuiteFactory implements IJobSuiteFactory {

    public BigRecoverySuiteFactory() {
        super();
    }

    @Override
    public JobSuite createJobSuite() {
        
        IJob suite = new AsyncJobGroup("test-RecoverableJobs", new IJob[] {
                new RecoverableSleepyJob(30, 5),
                new SyncJobGroup("test.sync.1", new IJob[] {
                        new RecoverableSleepyJob(6, 2),
                        new AsyncJobGroup("test.async.2", new IJob[] {
                                new SleepyJob(7, 3),
                                new SleepyJob(13, 9)
                        }),
                        new RecoverableSleepyJob(12, 4),
                        new RecoverableSleepyJob(18, 6)
                }),    
                new RecoverableSleepyJob(60, 10),
                new SyncJobGroup("test.sync.2", new IJob[] {
                        new RecoverableSleepyJob(10, 5),
                        new RecoverableSleepyJob(11, 6)
                }),
        });
        
        return new JobSuite(suite);
    }

    @Test
    public void testJobSuite() throws JobException {
        JobSuite suite = new BigRecoverySuiteFactory().createJobSuite();
        JobRunner runner = new JobRunner();
        boolean success = runner.runSuite(suite, true);
        assertTrue("Suite did not complete properly: "
                + suite.getSuiteStatus(), success);
    }
}
