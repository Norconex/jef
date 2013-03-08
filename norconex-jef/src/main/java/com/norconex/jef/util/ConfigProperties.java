package com.norconex.jef.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * The <code>ConfigProperties</code> class represents a persistent set of
 * properties just like its {@link Properties} parent class.  It adds
 * to the parent class the ability to obtain and set primitive values and
 * some commonly used objects.  Upon encountering a problem in parsing the
 * data to its target format, a {@link ConfigException} is thrown.
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 */
@SuppressWarnings("nls")
public class ConfigProperties extends Properties {

    //TODO consider renaming to JEFProperties
    
    /** Default format used to store and retrieve dates. */
    public static final DateFormat DEFAULT_DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSSS");
    
    /** For serialization. */
    private static final long serialVersionUID = -7215126924574341L;

    /** Logger. */
    private static final Logger LOG =
        LogManager.getLogger(ConfigProperties.class);
    
    private DateFormat dateFormat = (DateFormat) DEFAULT_DATE_FORMAT.clone();
    
    /**
     * @see Properties#Properties()
     */
    public ConfigProperties() {
        super();
    }
    /**
     * @see Properties#Properties(Properties)
     */
    public ConfigProperties(Properties defaults) {
        super(defaults);
    }

    /**
     * Reads a property list (key and element pairs) from the input
     * string.  Otherwise, the same considerations as
     * {@link #load(InputStream)} apply.
     * @param str the string to load
     * @throws IOException problem loading string
     * @since 1.1
     */
    public void loadFromString(String str) throws IOException {
        InputStream is = new ByteArrayInputStream(str.getBytes());
        load(is);
        is.close();
    }
    /**
     * Writes this property list (key and element pairs) in this
     * <code>Properties</code> table to the output stream in a format suitable
     * for loading into a <code>Properties</code> table using the
     * {@link #load(InputStream) load} method.
     * Otherwise, the same considerations as
     * {@link #store(OutputStream, String)} apply.
     * @param   comments   a description of the property list.
     * @return the properties as string
     * @throws IOException problem storing to string
     * @since 1.1
     */
    public String storeToString(String comments) throws IOException {
        OutputStream os = new ByteArrayOutputStream();
        store(os, comments);
        String str = os.toString();
        os.close();
        return str;
    }

    public void setDateFormat(DateFormat dateFormat) {
        synchronized (this.dateFormat) {
            this.dateFormat = dateFormat;
        }
    }
    public DateFormat getDateFormat() {
        synchronized (this.dateFormat) {
            return dateFormat;
        }
    }
    
    public String getString(String key) {
        return getProperty(key);
    }
    public String getString(String key, String defaultValue) {
        return getProperty(key, defaultValue);
    }
    /**
     * Sets a string.  Setting a string with a <code>null</code> value
     * will set a blank string.
     * @param key the key of the value to set
     * @param value the value to set
     */
    public void setString(String key, String value) {
    	if (value == null) {
            setProperty(key, "");
    	} else {
            setProperty(key, value);
    	}
    }
    
    public int getInt(String key) {
        try {
            return Integer.parseInt(getProperty(key));
        } catch (NumberFormatException e) {
            throw createConfigException(
                    "Could not parse integer value.", key, getProperty(key), e);
        }
    }
    public int getInt(String key, int defaultValue) {
        String value = getProperty(key, "" + defaultValue);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw createConfigException(
                    "Could not parse integer value.", key, value, e);
        }
    }
    public void setInt(String key, int value) {
        setProperty(key, Integer.toString(value));
    }
    
    public double getDouble(String key) {
        try {
            return Double.parseDouble(getProperty(key));
        } catch (NumberFormatException e) {
            throw createConfigException(
                    "Could not parse double value.", key, getProperty(key), e);
        }
    }
    public double getDouble(String key, double defaultValue) {
        String value = getProperty(key, "" + defaultValue);
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw createConfigException(
                    "Could not parse double value.", key, value, e);
        }
    }
    public void setDouble(String key, double value) {
        setProperty(key, Double.toString(value));
    }

    
    public long getLong(String key) {
        try {
            return Long.parseLong(getProperty(key));
        } catch (NumberFormatException e) {
            throw createConfigException(
                    "Could not parse long value.", key, getProperty(key), e);
        }
    }
    public long getLong(String key, long defaultValue) {
        String value = getProperty(key, "" + defaultValue);
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw createConfigException(
                    "Could not parse long value.", key, value, e);
        }
    }
    public void setLong(String key, long value) {
        setProperty(key, Long.toString(value));
    }
    
    public float getFloat(String key) {
        try {
            return Float.parseFloat(getProperty(key));
        } catch (NumberFormatException e) {
            throw createConfigException(
                    "Could not parse float value.", key, getProperty(key), e);
        }
    }
    public float getFloat(String key, float defaultValue) {
        String value = getProperty(key, "" + defaultValue);
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            throw createConfigException(
                    "Could not parse floag value.", key, value, e);
        }
    }
    public void setFloat(String key, float value) {
        setProperty(key, Float.toString(value));
    }

    
    public BigDecimal getBigDecimal(String key) {
        String value = getProperty(key);
        if (value == null || value.trim().length() == 0) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            throw createConfigException(
                    "Could not parse BigDecimal value.", key, value, e);
        }
    }
    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        BigDecimal value = getBigDecimal(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
    public void setBigDecimal(String key, BigDecimal value) {
        if (value == null) {
            setProperty(key, "");
        } else {
            setProperty(key, value.toString());
        }
    }

    public Date getDate(String key) {
        String value = getProperty(key);
        if (value == null || value.trim().length() == 0) {
            return null;
        }
        try {
            synchronized (dateFormat) {
                return dateFormat.parse(value);
            }
        } catch (NumberFormatException e) {
            throw createConfigException(
                    "Could not parse Date value.", key, value, e);
        } catch (ParseException e) {
            throw createConfigException(
                    "Could not parse Date value.", key, value, e);
        }
    }
    public Date getDate(String key, Date defaultValue) {
        Date value = getDate(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }    
    public void setDate(String key, Date value) {
        if (value == null) {
            setProperty(key, "");
        } else {
            synchronized (dateFormat) {
                setProperty(key, dateFormat.format(value));
            }
        }
    }
    
    public boolean getBoolean(String key) {
        return Boolean.valueOf(getProperty(key)).booleanValue();
    }
    public boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.valueOf(
                getProperty(key, "" + defaultValue)).booleanValue();
    }    
    public void setBoolean(String key, boolean value) {
        setProperty(key, Boolean.toString(value));
    }

    /**
     * Gets a file, assuming key value is a file system path. 
     * @param key configuration key
     */
    public File getFile(String key) {
        String filePath = getString(key);
        if (filePath == null) {
            return null;
        }
    	return new File(filePath);
    }
    /**
     * Gets a file, assuming key value is a file system path. 
     * @param key configuration key
     * @param defaultValue default file being returned if no file has been
     *        defined for the given key in the configuration.
     */
    public File getFile(String key, File defaultValue) {
        File value = getFile(key);
        if (value == null) {
            return defaultValue;
        }
    	return value;
    }
    public void setFile(String key, File value) {
        if (value == null) {
            setProperty(key, "");
        } else {
            setProperty(key, value.getPath());
        }
    }

    
    /**
     * Gets a class, assuming key value is a fully qualified class name
     * available in the classloader. 
     * @param key configuration key
     */
    public Class<?> getClass(String key) {
    	String value = getString(key);
    	try {
			return Class.forName(value);
		} catch (ClassNotFoundException e) {
            throw createConfigException(
                    "Could not parse class value.", key, value, e);
		}
    }
    /**
     * Gets a class, assuming key value is a fully qualified class name
     * available in the classloader. 
     * @param key configuration key
     * @param defaultValue default file being returned if no class has been
     *        defined for the given key in the configuration.
     */
    public Class<?> getClass(String key, Class<?> defaultValue) {
        Class<?> value = getClass(key);
        if (value == null) {
            return defaultValue;
        }
    	return value;
    }
    public void setClass(String key, Class<?> value) {
        if (value == null) {
            setProperty(key, "");
        } else {
            setProperty(key, value.getName());
        }
    }

    
    private ConfigException createConfigException(
            String msg, String key, String value, Exception cause) {
        String message = msg + " [key=" + key + "; value=" + value + "].";
        LOG.error(message, cause);
        return new ConfigException(message, cause);
    }
    /**
     * Converts value to a String using its "toString" method before storing
     * it.  If null, the value is converted to an empty string.
     * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
     * @since 1.1
     */
    public synchronized Object put(Object key, Object value) {
        if (value == null) {
            return super.put(key, "");
        }
        return super.put(key, value.toString());
    }
}
