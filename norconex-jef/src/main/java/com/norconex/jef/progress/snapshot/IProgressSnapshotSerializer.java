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
package com.norconex.jef.progress.snapshot;

import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;

/**
 * Interface meant to favour integration with other systems, such as monitoring
 * systems.
 * @author Pascal Essiembre
 * @since 2.0
 */
public interface IProgressSnapshotSerializer extends Serializable {

    /**
     * Writes a job progress snapshot.
     * @param out where to write the progress
     * @param snapshot the job progress snapshopt to write
     */
    void writeProgressSnapshot(Writer out, IProgressSnapshot snapshot);
    /**
     * Reads a job progress snapshot.
     * @param in reader for the progress
     * @return the job progress snapshot
     */
    IProgressSnapshot readProgressSnapshot(Reader in);
}
