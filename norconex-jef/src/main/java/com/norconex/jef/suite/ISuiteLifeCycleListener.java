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
package com.norconex.jef.suite;

/**
 * Listener for life-cycle activities on a job suite.
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 */
public interface ISuiteLifeCycleListener {

    /**
     * Invoked when a job suite is stopped.
     * @param suite job suite
     */
    void suiteStopped(JobSuite suite);
    /**
     * Invoked when a job suite is stopping.
     * @param suite job suite
     */
    void suiteStopping(JobSuite suite);
    /**
     * Invoked when a job suite is started.
     * @param suite job suite
     */
    void suiteStarted(JobSuite suite);
    /**
     * Invoked when a job suite is aborted.  This method offers little in
     * terms of error handling.  Refer to
     * {@link com.norconex.jef.error.IErrorHandler} to implement error handing.
     * @param suite job suite
     */
    void suiteAborted(JobSuite suite);
    /**
     * Invoked when a job suite finished executing.  A job may finish
     * without having completed successfully.
     * @param suite job suite
     * @since 2.0
     */
    void suiteTerminatedPrematuraly(JobSuite suite);
    /**
     * Invoked when a job suite completes.  A completed job suite is one
     * where all job executions returned <code>true</code> and
     * progress is at 100%.  Given that jobs are implemented correctly,
     * this is usually a good indication of success.
     * @param suite job suite
     */
    void suiteCompleted(JobSuite suite);
}
