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
package com.norconex.jef4.job;

import com.norconex.jef4.status.IJobStatus;


/**
 * Adapter for a job life-cycle.  Default implementation for all
 * methods do nothing.
 * @author Pascal Essiembre
 */
public abstract class AbstractJobLifeCycleListener 
        implements IJobLifeCycleListener {

    @Override
    public void jobTerminatedPrematuraly(final IJobStatus progress) {
        // do nothing
    }

    @Override
    public void jobProgressed(final IJobStatus progress) {
        // do nothing
    }

    @Override
    public void jobStarted(final IJobStatus progress) {
        // do nothing
    }

    @Override
    public void jobResumed(final IJobStatus progress) {
        // do nothing
    }

    @Override
    public void jobSkipped(final IJobStatus progress) {
        // do nothing
    }

    @Override
    public void jobStopped(final IJobStatus progress) {
        // do nothing
    }

    @Override
    public void jobStopping(final IJobStatus progress) {
        // do nothing
    }

    @Override
    public void jobCompleted(IJobStatus progress) {
        // do nothing
    }
}
