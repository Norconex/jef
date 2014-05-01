package com.norconex.jef.factories;

import com.norconex.jef.jobs.LoopProcessJob;
import com.norconex.jef.suite.IJobSuiteFactory;
import com.norconex.jef.suite.JobSuiteOLD;

public class SingleJobFactory implements IJobSuiteFactory {

    public SingleJobFactory() {
        super();
    }

    @Override
    public JobSuiteOLD createJobSuite() {
        
        return new JobSuiteOLD(new LoopProcessJob("loopingJob", "infiniteLoop.bat"));//"C:\\Development\\eclipse\\workspaces\\norconex\\com.norconex.jef\\infiniteLoop.bat"));
        
    }

}
