package com.norconex.jef.util;

/**
 * Configuration exception.  Typically thrown when setting/getting invalid
 * configuration values.
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 * @see ConfigProperties
 */
public class ConfigException extends RuntimeException {

    /** For serialization. */
    private static final long serialVersionUID = 3040976896770771979L;

    /**
     * @see Exception#Exception(java.lang.String)
     */
    public ConfigException(final String msg) {
        super(msg);
    }
    /**
     * @see Exception#Exception(java.lang.Throwable)
     */
    public ConfigException(final Throwable cause) {
        super(cause);
    }
    /**
     * @see Exception#Exception(java.lang.String, java.lang.Throwable)
     */
    public ConfigException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
