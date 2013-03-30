package com.norconex.jef.progress;

import java.io.IOException;

import com.norconex.commons.lang.map.Properties;
import com.norconex.jef.JobException;

/**
 * Utility methods for facilitating common job progress operations.
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
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
     * Sets meta data as a string from a {@link ConfigProperties} instance.
     * This is a convenience method for treating the meta data string as 
     * properties.
     * It calls {@link JobProgress#setMetadata()} after converting the
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
