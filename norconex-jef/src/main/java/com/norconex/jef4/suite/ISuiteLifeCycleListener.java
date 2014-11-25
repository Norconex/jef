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
package com.norconex.jef4.suite;

/**
 * Listener for life-cycle activities on a job suite.
 * @author Pascal Essiembre
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
     * {@link com.norconex.jef4.job.IJobErrorListener} for more options.
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
