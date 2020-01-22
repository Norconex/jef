/* Copyright 2020 Norconex Inc.
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
package com.norconex.jef4.log;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.norconex.commons.lang.config.XMLConfigurationUtil;
import com.norconex.commons.lang.file.FileUtil;
import com.norconex.commons.lang.log.Log4jCheck;
import com.norconex.jef4.job.IJob;
import com.norconex.jef4.job.group.JEFTestUtil;
import com.norconex.jef4.jobs.SleepyJob;
import com.norconex.jef4.status.JobSuiteStatusSnapshot;
import com.norconex.jef4.suite.JobSuite;
import com.norconex.jef4.suite.JobSuiteConfig;

// To test, remove log4j from classpath and add slf4j and log4j-over-slf4j.
@Ignore
public class NoLogManagerTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testWriteRead() throws IOException {
        NoLogManager f = new NoLogManager();
        System.out.println("Writing/Reading this: " + f);
        XMLConfigurationUtil.assertWriteRead(f);
    }

    //Test for: https://github.com/Norconex/jef/issues/13
    @Test
    public void testWithLog4jOverSLF4J() throws IOException {

        System.out.println("Testing with Log4j? " + Log4jCheck.present());

        JobSuiteConfig config = JEFTestUtil.newConfigWithTempWorkdir(folder);
        config.setLogManager(new NoLogManager());

        IJob job = new SleepyJob(2, 1);
        JobSuite suite = new JobSuite(job, config);

        FileUtils.touch(getIndexFile(suite)); // creates empty index file
        Assert.assertTrue("Execution returned false.", suite.execute());

        JobSuiteStatusSnapshot tree =
                JobSuiteStatusSnapshot.newSnapshot(getIndexFile(suite));
        Assert.assertEquals(1d, tree.getRoot().getProgress(), 0d);
    }

    private File getIndexFile(JobSuite suite) {
        return new File(suite.getConfig().getWorkdir(), "latest/"
                + FileUtil.toSafeFileName(suite.getId()) + ".index");
    }
}
