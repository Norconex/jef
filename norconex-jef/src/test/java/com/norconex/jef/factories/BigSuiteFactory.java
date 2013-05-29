package com.norconex.jef.factories;

import com.norconex.jef.AsyncJobGroup;
import com.norconex.jef.IJob;
import com.norconex.jef.SyncJobGroup;
import com.norconex.jef.jobs.PrintProgressListener;
import com.norconex.jef.jobs.SleepyJob;
import com.norconex.jef.suite.IJobSuiteFactory;
import com.norconex.jef.suite.JobSuite;

public class BigSuiteFactory implements IJobSuiteFactory {

    public BigSuiteFactory() {
        super();
    }

    @Override
    public JobSuite createJobSuite() {
        
        
        IJob job = new AsyncJobGroup("test.async.1", new IJob[] {
                new SleepyJob(30, 5),
                new SyncJobGroup("test.sync.1", new IJob[] {
                        new SleepyJob(6, 2),
                        new AsyncJobGroup("test.async.2", new IJob[] {
                                new SleepyJob(7, 3),
                                new SleepyJob(13, 9)
                        }),
                        new SleepyJob(12, 4),
                        new SleepyJob(18, 6)
                }),    
                new SleepyJob(60, 10),
                new SyncJobGroup("test.sync.2", new IJob[] {
                        new SleepyJob(10, 5),
                        new SleepyJob(11, 6)
                }),
        });

        JobSuite jobSuite = new JobSuite(job);
        jobSuite.addJobProgressListener(new PrintProgressListener());
        return jobSuite;
    }
}
