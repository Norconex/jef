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
package com.norconex.jef4.log;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

import com.norconex.jef4.suite.JobSuite;

/**
 * Log layout decorator, prefixing any existing layout with the job
 * id associated with the current thread, separated with colon-space (": ").
 * If no jobs are associated with
 * a given log event, the prefix "[non-job]: " will get prepended.
 * @author Pascal Essiembre
 * @see FileLogManager
 */
public class ThreadSafeLayout extends Layout {

    /** Log4j log layout. */
    private final Layout layout;
    
    /**
     * Constructor.
     * @param layout decorated layout
     */
    public ThreadSafeLayout(final Layout layout) {
        super();
        this.layout = layout;
    }

    @Override
    public void activateOptions() {
        layout.activateOptions();
    }

    /**
     * Calls the decorated instance, prefixing with job identifier.
     * @see org.apache.log4j.Layout#format(org.apache.log4j.spi.LoggingEvent)
     */
    @SuppressWarnings("nls")
    @Override
    public String format(LoggingEvent evt) {
        String jobId = JobSuite.getCurrentJobId();
        if (jobId == null) {
            return "[non-job]: " + layout.format(evt);
        }
        return jobId + ": " + layout.format(evt);
    }

    @Override
    public boolean ignoresThrowable() {
        return layout.ignoresThrowable();
    }
}
