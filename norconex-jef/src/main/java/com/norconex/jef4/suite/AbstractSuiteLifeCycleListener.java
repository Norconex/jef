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
