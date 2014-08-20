package com.norconex.jef4.job.group;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import com.norconex.jef4.job.IJob;
import com.norconex.jef4.jobs.SleepyJob;
import com.norconex.jef4.status.IJobStatus;
import com.norconex.jef4.suite.JobSuite;

public class AsyncJobGroupTest {

    @Test
    public void testExecuteGroup() {
        IJob job1 = new SleepyJob(30, 3);
        IJob job2 = new SleepyJob(20, 2);
        IJob job3 = new SleepyJob(10, 1);
        IJob rootJob = new AsyncJobGroup("async sleepy jobs", 2, job1, job2, job3);
        
        JobSuite suite = new JobSuite(rootJob);
        Assert.assertTrue("Suite failed.", suite.execute());
        
        assertStatus(suite.getJobStatus(job1));
        assertStatus(suite.getJobStatus(job2));
        assertStatus(suite.getJobStatus(job3));
        assertStatus(suite.getJobStatus(rootJob));
    }

    private void assertStatus(IJobStatus status) {
        System.out.println("Status of \"" + status.getJobId() + "\": "
                + status.getState() + " (" + status.getProgress() + ")");
        assertTrue(status.getProgress() == 1d);
//        assertTrue(status.getState() == JobState.COMPLETED);
    }
}
