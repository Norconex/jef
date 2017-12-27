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
 * Job suites are an optional concept allowing to further abstract the
 * job creation process.  This class is responsible for creating
 * the job suite used to execute an assembly of jobs.
 * Even though factories are not necessary to create job suites, they may 
 * facilitate integration with other applications or frameworks
 * and their use is recommended.
 * To further ensure potential integration, implementors should make sure
 * they provide an empty constructor and setting attributes on factories
 * shall be done using accessor methods (JavaBean style).
 * @author Pascal Essiembre
 */
public interface IJobSuiteFactory {
//TODO delete this???  Or provide a default implementation maybe?
    
    /**
     * Creates a job suite.
     * @return job suite
     */
    JobSuite createJobSuite();

}
