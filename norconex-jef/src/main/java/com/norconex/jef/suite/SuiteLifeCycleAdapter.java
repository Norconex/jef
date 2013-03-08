package com.norconex.jef.suite;

/**
 * Adapter for a suite life-cycle listener.  Default implementation for all
 * methods do nothing.
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 */
public class SuiteLifeCycleAdapter implements ISuiteLifeCycleListener {

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
