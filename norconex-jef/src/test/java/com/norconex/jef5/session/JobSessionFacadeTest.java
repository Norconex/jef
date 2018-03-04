/* Copyright 2018 Norconex Inc.
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
package com.norconex.jef5.session;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class JobSessionFacadeTest {

    @Test
    public void testLoadJobSessions() throws IOException {
        JobSessionFacade facade = JobSessionFacade.get(
                new InputStreamReader(
                JobSessionFacadeTest.class.getResourceAsStream(
                        JobSessionFacadeTest.class.getSimpleName() + ".xml"),
                StandardCharsets.UTF_8));
        
        //TODO test getting sessions when files are not created
        
        List<String> allJobIds = Arrays.asList(
                "Root Job",
                    "Job A",
                    "Job B",
                        "Job B.1",
                        "Job B.2",
                "Job C");

        Assert.assertEquals(allJobIds, facade.getAllIds());
//        System.out.println("facade:\n\n" + facade);
    }
    
//    
//    @Test
//    public void testWriteRead() throws IOException {
//        FileJobStatusStore f = new FileJobStatusStore();
//        f.setStatusDirectory(new File("/blah/somedir").getAbsolutePath());
//        System.out.println("Writing/Reading this: " + f);
//        XMLConfigurationUtil.assertWriteRead(f);
//    }
    
//    @Test
//    public void testValidation() throws IOException {
//        String xml = 
//           "<statusStore class=\"com.norconex.jef4.status.FileJobStatusStore\">"
//         + "<statusDir>/tmp/jeflogs</statusDir>"
//         + "</statusStore>";
//        
////        CountingConsoleAppender appender = new CountingConsoleAppender();
////        appender.startCountingFor(XMLConfigurationUtil.class, Level.WARN);
//        
//        try (Reader r = new StringReader(xml)) {
//            XMLConfigurationUtil.newInstance(r);
//        } finally {
////            appender.stopCountingFor(XMLConfigurationUtil.class);
//        }
////        Assert.assertEquals("Validation warnings/errors were found.", 
////                0, appender.getCount());
//    }
}
