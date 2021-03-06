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
package com.norconex.jef5.suite;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.norconex.jef5.JEFTestUtil;
import com.norconex.jef5.job.IJob;
import com.norconex.jef5.job.impl.SleepyJob;
import com.norconex.jef5.status.JobSuiteStatus;

public class JobSuiteTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Test
    public void testWriteJobSuiteIndex() throws IOException {
        
        JobSuiteConfig config = JEFTestUtil.newConfig(folder);
        IJob job = new SleepyJob(5, 1);
        JobSuite suite = new JobSuite(job, config);
        Assert.assertTrue("Execution returned false.", suite.execute());

        JobSuiteStatus tree = 
                JobSuiteStatus.getInstance(suite.getStatusIndex());
        System.out.println("TREE: " + tree);
        Assert.assertEquals(1d, tree.getRootStatus().getProgress(), 0d);
    }

}
