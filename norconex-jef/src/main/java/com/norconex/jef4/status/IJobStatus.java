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
package com.norconex.jef4.status;

import java.util.Date;

import com.norconex.commons.lang.map.Properties;

public interface IJobStatus {


    String getJobId();
    JobState getState();
    double getProgress();
    String getNote();
    int getResumeAttempts();
    JobDuration getDuration();
    Date getLastActivity();
    Properties getProperties();
    
    //--- State-related methods ---
    boolean isStarted();
    boolean isResumed();
    boolean isAborted();
    boolean isStopped();
    boolean isStopping();
    boolean isCompleted();
    boolean isPrematurlyEnded();
    boolean isRunning();
    boolean isState(JobState... states);
}
