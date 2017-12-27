/* Copyright 2010-2014 Norconex Inc.
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
package com.norconex.jef4;

import java.io.File;

import com.norconex.commons.lang.file.FileUtil;

public final class JEFUtil {

    //TODO make it current directory instead + jobsuite name.
    public static final File FALLBACK_WORKDIR = 
            new File(System.getProperty("user.home") + "/Norconex/jef/workdir");

    private JEFUtil() {
        super();
    }
    
    /**
     * Gets the latest index file created for a job suite (if one exists).
     * @param suiteWorkdir suite working directory
     * @param suiteId suite unique ID (ID of the root job)
     * @return file the index file
     */
    public static File getSuiteIndexFile(
            String suiteWorkdir, String suiteId) {
        return new File(suiteWorkdir + File.separator + "latest"
                + File.separator + FileUtil.toSafeFileName(suiteId) + ".index");
    }
}
