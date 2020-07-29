/* Copyright 2010-2018 Norconex Inc.
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
package com.norconex.jef5.job.group;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.norconex.jef5.JEFTestUtil;
import com.norconex.jef5.job.IJob;
import com.norconex.jef5.job.impl.SleepyJob;
import com.norconex.jef5.status.JobStatus;
import com.norconex.jef5.suite.JobSuite;

public class AsyncJobGroupTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    private File folder = null;
    private int sleepMultiplier = 1;

    public AsyncJobGroupTest() {
        super();
    }

    @Test
    public void testExecuteGroup() throws IOException {
        IJob job1 = new SleepyJob(10 * sleepMultiplier, 3);
        IJob job2 = new SleepyJob(5 * sleepMultiplier, 2);
        IJob job3 = new SleepyJob(3 * sleepMultiplier, 1);
        IJob rootJob = new AsyncJobGroup(
                "async sleepy jobs", 2, job1, job2, job3);
        
        JobSuite suite = new JobSuite(
                rootJob, JEFTestUtil.newConfig(folder, tempFolder));
        Assert.assertTrue("Suite failed.", suite.execute());
        
        assertStatus(suite.getJobStatus(job1));
        assertStatus(suite.getJobStatus(job2));
        assertStatus(suite.getJobStatus(job3));
        assertStatus(suite.getJobStatus(rootJob));
    }

    private void assertStatus(JobStatus status) {
        System.out.println("Status of \"" + status.getJobId() + "\": "
                + status.getState() + " (" + status.getProgress() + ")");
        assertTrue(status.getProgress() == 1d);
//        assertTrue(status.getState() == JobState.COMPLETED);
    }
    
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: <app> workdir [sleepScale]");
            System.out.println("  workdir    JEF working directory.");
            System.out.println("  sleepScale sleep multiplier (default 1).");
            System.exit(-1);
        }
        File dir = new File(args[0]);
        FileUtils.forceMkdir(dir);
        int scale = 1;
        if (args.length == 2) {
            scale = Integer.parseInt(args[1]);
        }
        
        AsyncJobGroupTest test = new AsyncJobGroupTest();
        test.folder = dir;
        test.sleepMultiplier = scale;
        test.testExecuteGroup();
    }
}
