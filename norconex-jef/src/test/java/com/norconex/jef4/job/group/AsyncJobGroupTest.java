/* Copyright 2010-2017 Norconex Inc.
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
package com.norconex.jef4.job.group;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.norconex.jef4.job.IJob;
import com.norconex.jef4.jobs.SleepyJob;
import com.norconex.jef4.status.IJobStatus;
import com.norconex.jef4.suite.JobSuite;

public class AsyncJobGroupTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Test
    public void testExecuteGroup() throws IOException {
        IJob job1 = new SleepyJob(30, 3);
        IJob job2 = new SleepyJob(20, 2);
        IJob job3 = new SleepyJob(10, 1);
        IJob rootJob = new AsyncJobGroup(
                "async sleepy jobs", 2, job1, job2, job3);
        
        JobSuite suite = new JobSuite(
                rootJob, JEFTestUtil.newConfigWithTempWorkdir(folder));
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
