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
package com.norconex.jef5;

import java.io.File;
import java.io.IOException;

import org.junit.rules.TemporaryFolder;

import com.norconex.jef5.suite.JobSuiteConfig;

public final class JEFTestUtil {

    public static final String TEMP_DIR_ROOT = "jef-junit";
    
    private JEFTestUtil() {
        super();
    }
    
    public static JobSuiteConfig newConfig(
            TemporaryFolder folder) throws IOException {
        JobSuiteConfig config = new JobSuiteConfig();
        config.setWorkdir(folder.newFolder(TEMP_DIR_ROOT).toPath());
        return config;
    }

    public static JobSuiteConfig newConfig(
            File folder, TemporaryFolder fallbackFolder) throws IOException {
        File dir = folder;
        if (dir == null) {
            dir = fallbackFolder.newFolder(TEMP_DIR_ROOT);
        }
        JobSuiteConfig config = new JobSuiteConfig();
        config.setWorkdir(dir.toPath());
        return config;
    }
}
