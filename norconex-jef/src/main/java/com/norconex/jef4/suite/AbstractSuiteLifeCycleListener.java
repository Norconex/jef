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
package com.norconex.jef4.suite;

/**
 * Adapter for a suite life-cycle listener.  Default implementation for all
 * methods do nothing.
 * @author Pascal Essiembre
 */
public class AbstractSuiteLifeCycleListener implements ISuiteLifeCycleListener {

    @Override
    public void suiteAborted(final JobSuite suite) {
        // do nothing
    }
    @Override
    public void suiteTerminatedPrematuraly(final JobSuite suite) {
        // do nothing
    }
    @Override
    public void suiteStarted(final JobSuite suite) {
        // do nothing
    }
    @Override
    public void suiteCompleted(final JobSuite suite) {
        // do nothing
    }
    @Override
    public void suiteStopped(JobSuite suite) {
        // TODO Auto-generated method stub
    }
    @Override
    public void suiteStopping(JobSuite suite) {
        // TODO Auto-generated method stub
    }
}
