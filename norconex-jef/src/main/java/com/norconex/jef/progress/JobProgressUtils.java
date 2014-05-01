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
package com.norconex.jef.progress;

import java.io.IOException;

import com.norconex.commons.lang.map.Properties;
import com.norconex.jef.JobException;

/**
 * Utility methods for facilitating common job progress operations.
 * @author Pascal Essiembre
 * @since 1.1
 */
@SuppressWarnings("nls")
public final class JobProgressUtils {

    private JobProgressUtils() {
        super();
    }

    /**
     * Gets meta data as a {@link Properties} instance.  This is a 
     * convenience method for treating the meta data string as properties.
     * It calls {@link JobProgress#getMetadata()} and parses it assuming it
     * follows {@link Properties} syntax.
     * @return configuration properties
     */
    public static Properties getMetaDataProperties(JobProgress progress) {
        if (progress == null || progress.getMetadata() == null) {
            return null;
        }
        Properties props = new Properties();
        try {
            props.loadFromString(progress.getMetadata());
        } catch (IOException e) {
            throw new JobException(
                    "Cannot convert progress meta data to properties.", e);
        }
        return props;
    }

    /**
     * Sets meta data as a string from a {@link Properties} instance.
     * This is a convenience method for treating the meta data string as 
     * properties.
     * It calls {@link JobProgress#setMetadata(String)} after converting the
     * properties to a string as per {@link Properties} syntax.
     * @param progress job progress on which to store meta data
     * @param props properties to store in job progress
     */
    public static void setMetaDataProperties(
            JobProgress progress, Properties props) {
        if (progress == null || props == null) {
            return;
        }
        try {
            progress.setMetadata(props.storeToString(null));
        } catch (IOException e) {
            throw new JobException(
                    "Cannot convert properties to progress meta data.", e);
        }
    }
}
