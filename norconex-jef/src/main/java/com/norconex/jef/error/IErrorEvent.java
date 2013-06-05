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
package com.norconex.jef.error;

import com.norconex.jef.progress.JobProgress;
import com.norconex.jef.suite.JobSuite;

/**
 * Event thrown by the framework to all registered <code>ErrorHandler</code>
 * instances.
 * @author Pascal Essiembre
 */
public interface IErrorEvent {

    /**
     * Gets the exception behind this error.
     * @return the exception
     */
    Throwable getException();

    /**
     * Gets the job suite from which this error occurred.
     * @return job suite
     */
    JobSuite getJobSuite();

    /**
     * Gets the job progress of the job from which the error got triggered.
     * @return job progress
     */
    JobProgress getProgress();
}
