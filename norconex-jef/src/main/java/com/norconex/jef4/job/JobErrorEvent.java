/* Copyright 2010-2014 Norconex Inc.
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
package com.norconex.jef4.job;

import java.io.Serializable;

import com.norconex.jef4.status.IJobStatus;
import com.norconex.jef4.suite.JobSuite;

/**
 * Event thrown by the framework to all registered <code>ErrorHandler</code>
 * instances.
 * @author Pascal Essiembre
 */
public class JobErrorEvent implements Serializable {

    private static final long serialVersionUID = 3751532918788425961L;
    private final Throwable t;
    private final JobSuite suite;
    private final IJobStatus status;
    
    public JobErrorEvent(Throwable t, JobSuite suite, IJobStatus status) {
        super();
        this.t = t;
        this.suite = suite;
        this.status = status;
    }

    /**
     * Gets the exception behind this error.
     * @return the exception
     */
    public Throwable getException() {
        return t;
    }
    /**
     * Gets the job suite from which this error occurred.
     * @return job suite
     */
    public JobSuite getSuite() {
        return suite;
    }
    /**
     * Gets the job progress of the job from which the error got triggered.
     * @return job progress
     */
    public IJobStatus getStatus() {
        return status;
    }

}
