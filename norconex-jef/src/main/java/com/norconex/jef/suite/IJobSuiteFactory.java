package com.norconex.jef.suite;


/**
 * Job suites are an optional concept allowing to further abstract the
 * job creation process.  This class is responsible for creating
 * the job suite used to execute an assembly of jobs.
 * Even though factories are not necessary to create job suites, they may be
 * integrated with other parts or the framework and/or external tools or
 * applications and their use is highly recommended.
 * To further ensure potential integration, implementors should make sure
 * they provide an empty constructor and setting attributes on factories
 * shall be done using accessor methods (JavaBean style).
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 */
public interface IJobSuiteFactory {

    /**
     * Creates a job suite.
     * @return job suite
     */
    JobSuite createJobSuite();

}
