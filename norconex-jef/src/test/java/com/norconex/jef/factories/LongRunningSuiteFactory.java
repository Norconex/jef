package com.norconex.jef.factories;

import com.norconex.jef.AsyncJobGroup;
import com.norconex.jef.IJob;
import com.norconex.jef.suite.IJobSuiteFactory;
import com.norconex.jef.suite.JobSuiteOLD;
import com.norconex.jef.jobs.PrintProgressListener;
import com.norconex.jef.jobs.SleepyJob;

public class LongRunningSuiteFactory implements IJobSuiteFactory {

    public LongRunningSuiteFactory() {
        super();
    }

    @Override
    public JobSuiteOLD createJobSuite() {
        
        IJob job = new AsyncJobGroup("test.async.longRunning", new IJob[] {
                new SleepyJob(5 * 60, 30),
                new SleepyJob(5 * 60, 60),
        });

        JobSuiteOLD jobSuiteOLD = new JobSuiteOLD(job);
        jobSuiteOLD.addJobProgressListener(new PrintProgressListener());
        return jobSuiteOLD;
    }

}
