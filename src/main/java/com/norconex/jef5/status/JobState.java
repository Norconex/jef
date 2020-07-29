/* Copyright 2010-2018 Norconex Inc.
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
package com.norconex.jef5.status;

public enum JobState {

    //TODO keep completion and 100% complete (termination) separate??

    /**
     * The job was aborted (i.e. killed).  That is, if a job was started
     * and is no longer running, while it never was
     * marked as completed or stopped.  Under normal conditions, a job
     * should always finish, whether it failed or not.  An aborted
     * progress is usually the results of a job suite which got "killed"
     * in the middle of its execution (not having the chance to return
     * properly).
     */
    ABORTED,
    /**
     * The job execution has completed successfully.
     */
    COMPLETED,
    /**
     * The job stopped running without any reported problems, but
     * its progress indicator has not reached 100% completion.
     */
    UNCOMPLETED,
    /**
     * The job is currently running.
     */
    RUNNING,
    /*STARTED,*/
    /**
     * Job execution status is unknown.  This status is returned when
     * there was no way to establish actual status, for unpredictable
     * reasons.
     */
    UNKNOWN,
    /**
     * A request to stop job execution has been received and the job
     * is currently stopping.
     */
    STOPPING,
    /**
     * A request to stop job execution has been received and the job
     * stopped.
     */
    STOPPED;

    public boolean isOneOf(JobState... jobStates) {
        for (JobState jobState : jobStates) {
            if (jobState == this) {
                return true;
            }
        }
        return false;
    }
}
