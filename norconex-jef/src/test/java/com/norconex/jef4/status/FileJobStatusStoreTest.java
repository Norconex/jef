/* Copyright 2017-2020 Norconex Inc.
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
package com.norconex.jef4.status;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.norconex.commons.lang.config.XMLConfigurationUtil;
import com.norconex.commons.lang.log.CountingConsoleAppender;
import com.norconex.commons.lang.map.Properties;

public class FileJobStatusStoreTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testWriteRead() throws IOException {
        FileJobStatusStore f = new FileJobStatusStore();
        f.setStatusDirectory(new File("/blah/somedir").getAbsolutePath());
        System.out.println("Writing/Reading this: " + f);
        XMLConfigurationUtil.assertWriteRead(f);
    }

    @Test
    public void testJobStatusWriteRead() throws IOException {
        FileJobStatusStore f = new FileJobStatusStore();
        f.setStatusDirectory(folder.newFolder().getAbsolutePath());

        long min = 1 * 60 * 1000;
        long earlier = System.currentTimeMillis() - 60 * min;

        JobDuration jd = new JobDuration();
        jd.setResumedStartTime(new Date(earlier));
        jd.setResumedLastActivity(new Date(earlier + 10 * min));
        jd.setStartTime(new Date(earlier + 20 * min));
        jd.setEndTime(new Date(earlier + 30 * min));

        MutableJobStatus jobStatus = new MutableJobStatus("myJobId");
        jobStatus.setDuration(jd);
        jobStatus.setLastActivity(new Date(earlier + 25 * min));
        jobStatus.setNote("Note:\n  This is a note.");
        jobStatus.setProgress(98.76);
        jobStatus.setResumeAttempts(3);
        jobStatus.setStopRequested(true);
        Properties props = jobStatus.getProperties();
        props.addBigDecimal("bigD", BigDecimal.valueOf(33.3));
        props.addClass("klazz", Integer.class);
        props.setLocale("locale", Locale.CANADA_FRENCH);

        f.write("mySuite", jobStatus);
        IJobStatus newJobStatus = f.read("mysuite", "myJobId");

        Assert.assertEquals(jobStatus, newJobStatus);
    }


    @Test
    public void testValidation() throws IOException {
        String xml =
           "<statusStore class=\"com.norconex.jef4.status.FileJobStatusStore\">"
         + "<statusDir>/tmp/jeflogs</statusDir>"
         + "</statusStore>";

        CountingConsoleAppender appender = new CountingConsoleAppender();
        appender.startCountingFor(XMLConfigurationUtil.class, Level.WARN);

        try (Reader r = new StringReader(xml)) {
            XMLConfigurationUtil.newInstance(r);
        } finally {
            appender.stopCountingFor(XMLConfigurationUtil.class);
        }
        Assert.assertEquals("Validation warnings/errors were found.",
                0, appender.getCount());
    }
}
