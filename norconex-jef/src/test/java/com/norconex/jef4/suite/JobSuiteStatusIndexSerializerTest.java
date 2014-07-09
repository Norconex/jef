package com.norconex.jef4.suite;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.norconex.commons.lang.file.FileUtil;
import com.norconex.jef4.job.IJob;
import com.norconex.jef4.jobs.SleepyJob;
import com.norconex.jef4.status.JobSuiteStatusSnapshot;

public class JobSuiteStatusIndexSerializerTest {

    @Test
    public void testWriteJobSuiteIndex() throws IOException {
        
        JobSuiteConfig config = new JobSuiteConfig();
        config.setWorkdir("c:\\temp\\jef-tests");
        IJob job = new SleepyJob(5, 1);
        JobSuite suite = new JobSuite(job, config);
        Assert.assertTrue("Execution returned false.", suite.execute());

        JobSuiteStatusSnapshot tree = 
                JobSuiteStatusSnapshot.newSnapshot(
                        new File("c:\\temp\\jef-tests\\latest\\"
                        + FileUtil.toSafeFileName(job.getName()) + ".index"));
        System.out.println("TREE: " + tree);
        Assert.assertEquals(1d, tree.getRoot().getProgress(), 0d);
    }

}
