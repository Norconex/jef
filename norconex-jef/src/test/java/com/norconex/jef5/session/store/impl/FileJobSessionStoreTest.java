/* Copyright 2017-2018 Norconex Inc.
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
package com.norconex.jef5.session.store.impl;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.norconex.commons.lang.config.XMLConfigurationUtil;

public class FileJobSessionStoreTest {

    private static final Logger LOG =
            LoggerFactory.getLogger(FileJobSessionStoreTest.class);
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Test
    public void testWriteRead() throws IOException {
        FileJobSessionStore f = new FileJobSessionStore();
        f.setStoreDir(folder.newFolder("storedir").toPath());
        LOG.debug("Writing/Reading this: {}", f);
        XMLConfigurationUtil.assertWriteRead(f);
    }
    
    @Test
    public void testValidation() throws IOException {
        String xml = 
           "<store class=\""
         + "com.norconex.jef5.session.store.impl.FileJobSessionStore\">"
         + "<storeDir>/tmp/store</storeDir>"
         + "<storeBackupDir>/tmp/backups</storeBackupDir>"
         + "</store>";
        Assert.assertEquals("Validation warnings/errors were found.", 
                0, XMLConfigurationUtil.validate(FileJobSessionStore.class, 
                        new StringReader(xml)));
    }
}
