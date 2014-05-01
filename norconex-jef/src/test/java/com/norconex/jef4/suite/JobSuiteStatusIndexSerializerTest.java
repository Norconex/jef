package com.norconex.jef4.suite;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.norconex.jef4.jobs.SleepyJob;

public class JobSuiteStatusIndexSerializerTest {

    @Test
    public void testWriteJobSuiteIndex() {
        
        JobSuiteConfig config = new JobSuiteConfig();
        config.setWorkdir("c:\\temp\\jef-tests");
        JobSuite suite = new JobSuite(config, new SleepyJob(5, 1));
        suite.execute();
        
        fail("Not yet implemented");
    }

}
