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
