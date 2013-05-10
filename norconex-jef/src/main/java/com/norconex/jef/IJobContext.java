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
package com.norconex.jef;

import java.io.Serializable;

/**
 * Holds contextual information about a job.  Some attributes are descriptive 
 * in nature while others defines boundaries to be used by the JEF framework.
 * Concrete implementations should be considered immutable once instantiated.  
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 * @since 2.0
 */
public interface IJobContext extends Serializable {

    int PROGRESS_ZERO = 0;
    int PROGRESS_100 = 1000;
    
    /**
     * Gets the job description.
     * @return job description
     */
    String getDescription();

    /**
     * Gets the minimum execution progress value.
     * @return minimum progress value
     */
    long getProgressMinimum();
    /**
     * Gets the maximum execution progress value.
     * @return maximum progress value
     */
    long getProgressMaximum();
    
}
