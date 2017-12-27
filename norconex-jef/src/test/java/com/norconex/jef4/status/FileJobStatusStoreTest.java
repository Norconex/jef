/* Copyright 2017 Norconex Inc.
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

import org.junit.Test;

import com.norconex.commons.lang.config.XMLConfigurationUtil;

public class FileJobStatusStoreTest {

    
    @Test
    public void testWriteRead() throws IOException {
        FileJobStatusStore f = new FileJobStatusStore();
        f.setStatusDirectory(new File("/blah/somedir").getAbsolutePath());
        System.out.println("Writing/Reading this: " + f);
        XMLConfigurationUtil.assertWriteRead(f);
    }
    
    @Test
    public void testValidation() throws IOException {
        String xml = 
           "<statusStore class=\"com.norconex.jef4.status.FileJobStatusStore\">"
         + "<statusDir>/tmp/jeflogs</statusDir>"
         + "</statusStore>";
        
//        CountingConsoleAppender appender = new CountingConsoleAppender();
//        appender.startCountingFor(XMLConfigurationUtil.class, Level.WARN);
        
        try (Reader r = new StringReader(xml)) {
            XMLConfigurationUtil.newInstance(r);
        } finally {
//            appender.stopCountingFor(XMLConfigurationUtil.class);
        }
//        Assert.assertEquals("Validation warnings/errors were found.", 
//                0, appender.getCount());
    }
}
