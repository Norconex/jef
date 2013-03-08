package com.norconex.jef.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

/**
 * File system-related utility methods.
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 */
@SuppressWarnings("nls")
public final class FileSystemUtils {

    /** Represents one kilobytes. */
    private static final int KB_1 = 1024;

    /**
     * Constructor.
     */
    private FileSystemUtils() {
        super();
    }

    /**
     * Resolves a symbolic link's real path.
     * @param link the full path to the link
     * @return link's real path
     * @throws IOException link does not exist
     */
    public static String realPath(final String link) throws IOException {
        return realPath(new File(link)).getAbsolutePath();
    }
    /**
     * Resolves a symbolic link's real path.
     * @param link the full path to the link
     * @return link's real path
     * @throws IOException link does not exist
     */
    public static File realPath(final File link) throws IOException {
        if (!link.exists()) {
            throw new IOException(
                    "Link/File/Dir '" + link + "' does not exists.");
        }
        return link.getCanonicalFile();
    }

    /**
     * Gets the default path to the JEF working directory for file-system
     * related operations.  The path is determined in one of the following
     * way (in order):
     * <ul>
     *   <li>The system property "jef.job.dir".
     *   <li>The system property "user.home", appended with
     *       "/norconex/jef/jobs"
     * </ul>
     * @return path to default working directory
     */
    public static String getDefaultWorkDir() {
        String jobDir = System.getProperty("jef.job.dir");
        if (jobDir == null) {
            jobDir = System.getProperty("user.home")
                    + "/norconex/jef/jobs";
        }
        File dir = new File(jobDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return jobDir;
    }

    /**
     * Deletes a directory.
     * @param dir the directory to delete
     * @return <code>true</code> if deletion was successful
     */
    public static boolean deleteDir(final String dir) {
        return deleteDir(new File(dir));
    }

    /**
     * Deletes a directory.
     * @param dir the directory to delete
     * @return <code>true</code> if deletion was successful
     */
    public static boolean deleteDir(final File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    /**
     * Copies a directory over to another location.  If that location does
     * not already exists, it will be created.
     * @param sourceLocation source directory
     * @param targetLocation target directory
     * @throws IOException problem copying directory
     */
    public static void copyDir(
            final File sourceLocation , final File targetLocation)
            throws IOException {
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < children.length; i++) {
                copyDir(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[KB_1];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }
    
    /**
     * Creates (if not already existing) a series of directories reflecting
     * a date, up to the day unit, under a given parent directory.  For example,
     * a date of 1999-12-31 will create the following directory structure:
     * <code>
     *    /&lt;parentDir&gt;/1999/12/31/
     * </code>
     * @param parentDir the parent directory where to create date directories
     * @param date the date to create directories from
     * @return the directory representing the full path created
     * @throws IOException if the parent directory is not valid
     * @since 1.1.1
     */
    public static File createDateDirs(File parentDir, Date date)
            throws IOException {
        if (parentDir != null && parentDir.exists() 
                && !parentDir.isDirectory()) {
            throw new IOException("Parent directory \"" + parentDir 
                    + "\" already exists and is not a directory.");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String year = Integer.toString(cal.get(Calendar.YEAR));
        String month = Integer.toString(cal.get(Calendar.MONTH));
        if (month.length() == 1) {
            month = "0" + month;
        }
        String day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
        if (day.length() == 1) {
            day = "0" + day;
        }
        File dateDir = new File(parentDir.getAbsolutePath()
                + "/" + year + "/" + month + "/" + day);
        dateDir.mkdirs();
        return dateDir;
    }
    /**
     * Creates (if not already existing) a series of directories reflecting
     * a date, up to the day unit, under a given parent directory.  For example,
     * a date of 1999-12-31 will create the following directory structure:
     * <code>
     *    /&lt;parentDir&gt;/1999/12/31/
     * </code>
     * @param parentDir the parent directory, as string, 
     *                  where to create date directories
     * @param date the date to create directories from
     * @return the directory, as string, representing the full path created
     * @throws IOException if the parent directory is not valid
     * @since 1.1.1
     */
    public static String createDateDirs(String parentDir, Date date) 
            throws IOException {
        return createDateDirs(new File(parentDir), date).getAbsolutePath();
    }

}
