package com.norconex.jef.factories;

import com.norconex.jef.jobs.LoopProcessJob;
import com.norconex.jef.suite.IJobSuiteFactory;
import com.norconex.jef.suite.JobSuite;

public class SingleJobFactory implements IJobSuiteFactory {

    public SingleJobFactory() {
        super();
    }

    @Override
    public JobSuite createJobSuite() {
        
        return new JobSuite(new LoopProcessJob("loopingJob", "infiniteLoop.bat"));//"C:\\Development\\eclipse\\workspaces\\norconex\\com.norconex.jef\\infiniteLoop.bat"));
        
    }

}
