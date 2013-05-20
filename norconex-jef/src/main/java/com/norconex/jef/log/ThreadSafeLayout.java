/* Copyright 2010-2013 Norconex Inc.
 * 
 * This file is part of Norconex JEF.
 * 
 * Norconex JEF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Norconex JEF is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Norconex JEF. If not, see <http://www.gnu.org/licenses/>.
 */
package com.norconex.jef.log;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

import com.norconex.jef.JobRunner;

/**
 * Log layout decorator, prefixing any existing layout with the job
 * id associated with the current thread, separated with colon-space (": ").
 * If no jobs are associated with
 * a given log event, the prefix "[non-job]: " will get prepended.
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
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
        String jobId = JobRunner.getCurrentJobId();
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
