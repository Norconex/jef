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

import com.norconex.jef.IJob;
import com.norconex.jef.progress.IJobStatus;

/**
 * Convenience base implementation of {@link IJobSuiteVisitor}.
 * All methods are empty (do nothing) and are meant for developers to pick
 * and chose the method to overwrite.
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 * @since 1.1
 */
public abstract class AbstractJobSuiteVisitor implements IJobSuiteVisitor {

    @Override
    public void visitJob(IJob job) {
        // do nothing
    }

    @Override
    public void visitJobProgress(IJobStatus jobProgress) {
        // do nothing
    }

    @Override
    public void visitJobSuite(JobSuite jobSuite) {
        // do nothing
    }
}
