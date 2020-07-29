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

import java.time.Duration;
import java.util.function.Consumer;

import com.norconex.commons.lang.map.Properties;

public class JobStatusUpdater {

    private final JobStatus status;
    private final Consumer<JobStatus> changeListener; 
    
    public JobStatusUpdater(
            JobStatus status, Consumer<JobStatus> changeListener) {
        this.status = status;
        this.changeListener = changeListener;
        status.getProperties().addMapChangeListener(
                event -> statusUpdated(status));
    }

    public String getJobId() {
        return status.getJobId();
    }
    
    //TODO have set/getProperty(...) instead?
    public Properties getProperties() {
        return status.getProperties();
    }
    public double getProgress() {
        return status.getProgress();
    }
    public void setProgress(double progress) {
        status.setProgress(progress);
        statusUpdated(status);
    }
    public void incrementProgress(double increment) {
        status.setProgress(status.getProgress() + increment);
        statusUpdated(status);
    }
    public Duration getDuration() {
        return status.getDuration();
    }
    public void getNote() {
        status.getNote();
    }
    public void setNote(String note) {
        status.setNote(note);
        statusUpdated(status);
    }    
    private void statusUpdated(JobStatus status) {
        changeListener.accept(status);
    }
}
