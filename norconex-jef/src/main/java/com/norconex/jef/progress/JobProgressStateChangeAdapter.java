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
package com.norconex.jef.progress;


/**
 * Adapter for a job life-cycle, focusing on progress state changes.
 * The methods <code>jobFinished, jobProgressed,
 * jobStarted, jobResumed</code> all invoke
 * <code>jobStateChanged</code> for convenience.
 * Override that method for common behaviour upon any type of state change.
 * @author Pascal Essiembre
 */
public class JobProgressStateChangeAdapter extends JobProgressAdapter {

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
    public void jobStopped(final IJobStatus progress) {
        jobStateChanged(progress);
    }

    @Override
    public void jobStopping(final IJobStatus progress) {
        jobStateChanged(progress);
    }
    @Override
    public void jobCompleted(IJobStatus progress) {
        jobStateChanged(progress);
    }
    
    /**
     * Invoked when any of the following are invoked 
     * (unless they are overridden):
     * <code>jobTerminatedPrematuraly, jobProgressed, jobStarted, jobResumed,
     * jobStopping, jobStopped, jobCompleted</code>.
     * @param progress progress that changed
     */
    public void jobStateChanged(final IJobStatus progress) {
        // do nothing
    }
}
