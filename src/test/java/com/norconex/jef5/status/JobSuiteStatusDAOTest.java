/* Copyright 2018-2020 Norconex Inc.
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
package com.norconex.jef5.status;

import static java.time.Duration.ofMinutes;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Locale;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.norconex.commons.lang.map.Properties;

public class JobSuiteStatusDAOTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testJobStatusWriteRead() throws IOException {
        JobSuiteStatusDAO dao = new JobSuiteStatusDAO(
                "mysuite", folder.newFolder().toPath());

        Instant earlier1 = Instant.now().minus(ofMinutes(60));
        JobStatusData jsd1 = new JobStatusData();
        jsd1.setStartTime(earlier1);
        jsd1.setLastActivity(earlier1.plus(ofMinutes(10)));
        jsd1.setEndTime(earlier1.plus(ofMinutes(20)));
        jsd1.setNote("Note:\n  This is a note 1.");
        jsd1.setProgress(98.76);
        jsd1.setStopRequested(true);
        Properties props1 = jsd1.getProperties();
        props1.add("bigD1", BigDecimal.valueOf(11.1));
        props1.add("klazz1", Integer.class);
        props1.set("locale1", Locale.CANADA_FRENCH);

        Instant earlier2 = Instant.now().minus(ofMinutes(30));
        JobStatusData jsd2 = new JobStatusData();
        jsd2.setStartTime(earlier2);
        jsd2.setLastActivity(earlier2.plus(ofMinutes(5)));
        jsd2.setEndTime(earlier2.plus(ofMinutes(15)));
        jsd2.setNote("Note:\n  This is a note 2.");
        jsd2.setProgress(12.34);
        jsd2.setStopRequested(false);
        Properties props2 = jsd2.getProperties();
        props2.add("bigD2", BigDecimal.valueOf(22.2));
        props2.add("klazz2", Integer.class);
        props2.set("locale2", Locale.CANADA_FRENCH);

        Instant earlier3 = Instant.now().minus(ofMinutes(10));
        JobStatus jobStatus = new JobStatus("myjob",
                new TreeSet<>(Arrays.asList(jsd1, jsd2)));
        jobStatus.setStartTime(earlier3.plus(ofMinutes(1)));
        jobStatus.setLastActivity(earlier3.plus(ofMinutes(2)));
        jobStatus.setEndTime(earlier3.plus(ofMinutes(3)));
        jobStatus.setNote("Note:\n  This is a note 3.");
        jobStatus.setProgress(13.666);
        Properties props3 = jobStatus.getProperties();
        props3.add("bigD3", BigDecimal.valueOf(33.3));
        props3.add("klazz3", Integer.class);
        props3.set("locale3", Locale.CANADA_FRENCH);

        dao.write(jobStatus);
        JobStatus newJobStatus = dao.read("myjob");

        Assert.assertEquals(jobStatus, newJobStatus);
    }

}
