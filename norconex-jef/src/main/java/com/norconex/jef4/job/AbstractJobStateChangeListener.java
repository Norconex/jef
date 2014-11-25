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
 * Adapter for a job life-cycle, focusing on progress state changes.
 * The methods <code>jobFinished, jobProgressed,
 * jobStarted, jobResumed</code> all invoke
 * <code>jobStateChanged</code> for convenience.
 * Override that method for common behaviour upon any type of state change.
 * @author Pascal Essiembre
 */
public abstract class AbstractJobStateChangeListener extends AbstractJobLifeCycleListener {

    @Override
    public final void jobTerminatedPrematuraly(final IJobStatus progress) {
        jobStateChanged(progress);
    }

    @Override
    public final void jobProgressed(final IJobStatus progress) {
        jobStateChanged(progress);
    }

    @Override
    public final void jobStarted(final IJobStatus progress) {
        jobStateChanged(progress);
    }

    @Override
    public final void jobResumed(final IJobStatus progress) {
        jobStateChanged(progress);
    }

    @Override
    public final void jobStopped(final IJobStatus progress) {
        jobStateChanged(progress);
    }

    @Override
    public final void jobStopping(final IJobStatus progress) {
        jobStateChanged(progress);
    }
    @Override
    public final void jobCompleted(IJobStatus progress) {
        jobStateChanged(progress);
    }
    
    @Override
    public final void jobSkipped(IJobStatus progress) {
        jobStateChanged(progress);
    }
    
    /**
     * Invoked when any of the following are invoked 
     * (unless they are overridden):
     * <code>jobTerminatedPrematuraly, jobProgressed, jobStarted, jobResumed,
     * jobStopping, jobStopped, jobCompleted</code>.
     * @param progress progress that changed
     */
    public abstract void jobStateChanged(final IJobStatus progress);
}
