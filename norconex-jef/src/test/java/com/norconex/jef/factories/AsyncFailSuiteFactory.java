package com.norconex.jef.factories;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.norconex.jef.AsyncJobGroup;
import com.norconex.jef.IJob;
import com.norconex.jef.JobException;
import com.norconex.jef.JobRunner;
import com.norconex.jef.SyncJobGroup;
import com.norconex.jef.jobs.CrashingJob;
import com.norconex.jef.jobs.SleepyJob;
import com.norconex.jef.suite.IJobSuiteFactory;
import com.norconex.jef.suite.JobSuiteOLD;

public class AsyncFailSuiteFactory implements IJobSuiteFactory {

    private String suiteNamePrefixString;
    private int suiteNameSuffixInt;
    
    public AsyncFailSuiteFactory() {
        super();
    }

    public void setSuiteNamePrefixString(String dummyString) {
        this.suiteNamePrefixString = dummyString;
    }
    public void setSuiteNameSuffixInt(int dummyInt) {
        this.suiteNameSuffixInt = dummyInt;
    }

    @Override
    public JobSuiteOLD createJobSuite() {
        String suiteName = "test.failsuite";
        if (suiteNamePrefixString != null) {
            suiteName = suiteNamePrefixString + "-" + suiteName;
        }
        if (suiteNameSuffixInt != 0) {
            suiteName = suiteName + "-" + suiteNameSuffixInt;
        }
        
        
        IJob syncGroup = new SyncJobGroup(suiteName, new IJob[] {
                new AsyncJobGroup("asyncGroup1", new IJob[] {
                        new CrashingJob(3, RuntimeException.class),
                        new CrashingJob(5, NullPointerException.class),
                        new CrashingJob(7, IllegalArgumentException.class),
                }),
                new AsyncJobGroup("asyncGroup2", new IJob[] {
                        new SleepyJob(3, 1),
                        new SleepyJob(5, 1),
                        new SleepyJob(7, 1),
                }),
                new AsyncJobGroup("asyncGroup3", new IJob[] {
                        new SleepyJob(2, 2),
                        new SleepyJob(4, 2),
                }),
        });
        JobSuiteOLD context = new JobSuiteOLD(syncGroup);  

        return context;
    }

    @Test
    public void testJobSuite() throws JobException {
        JobSuiteOLD suite = new AsyncFailSuiteFactory().createJobSuite();
        JobRunner runner = new JobRunner();
        boolean success = runner.runSuite(suite, true);
        assertFalse("Suite was expected to fail, but did not.", success);
    }
    
}
