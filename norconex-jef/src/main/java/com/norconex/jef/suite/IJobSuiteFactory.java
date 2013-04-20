/* Copyright 2010-2013 Norconex Inc.
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
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 */
public interface IJobSuiteFactory {

    /**
     * Creates a job suite.
     * @return job suite
     */
    JobSuite createJobSuite();

}
