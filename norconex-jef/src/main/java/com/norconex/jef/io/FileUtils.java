package com.norconex.jef.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

/**
 * File related utility methods.
 * XXX Code largely inspired from
 * XXX http://forum.java.sun.com/thread.jspa?threadID=676327&messageID=3949951
 * 
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 * @since 1.1
 */
@SuppressWarnings("nls")
public final class FileUtils {

    /** Empty strings. */
    private static final String[] EMPTY_STRINGS = new String[] {};

    /**
     * Constructor.
     */
    private FileUtils() {
        super();
    }

    /**
     * Returns the specified number of lines starting from the beginning
     * of a text file.
     * @param file the file to read lines from
     * @param numberOfLinesToRead the number of lines to read
     * @return array of file lines
     * @throws IOException
     */
    public static String[] head(File file, int numberOfLinesToRead)
            throws IOException {
        return head(file, CharsetUtils.ISO_8859_1, numberOfLinesToRead);
    }

    /**
     * Returns the specified number of lines starting from the beginning
     * of a text file, using the given encoding.
     * @param file the file to read lines from
     * @param encoding the file encoding
     * @param numberOfLinesToRead the number of lines to read
     * @return array of file lines
     * @throws IOException
     */
    public static String[] head(File file, String encoding,
            int numberOfLinesToRead) throws IOException {
        return head(file, encoding, numberOfLinesToRead, true);
    }
    /**
     * Returns the specified number of lines starting from the beginning
     * of a text file, using the given encoding.
     * @param file the file to read lines from
     * @param encoding the file encoding
     * @param numberOfLinesToRead the number of lines to read
     * @param stripBlankLines whether to return blank lines or not
     * @return array of file lines
     * @throws IOException
     */
    public static String[] head(File file, String encoding,
            int numberOfLinesToRead, boolean stripBlankLines)
            throws IOException {
        return head(file, encoding, numberOfLinesToRead, stripBlankLines, null);
    }    
    /**
     * Returns the specified number of lines starting from the beginning
     * of a text file, using the given encoding.
     * @param file the file to read lines from
     * @param encoding the file encoding
     * @param numberOfLinesToRead the number of lines to read
     * @param stripBlankLines whether to return blank lines or not
     * @param filter InputStream filter
     * @return array of file lines
     * @throws IOException
     */
    public static String[] head(File file, String encoding,
            int numberOfLinesToRead, boolean stripBlankLines,
            IInputStreamFilter filter)
            throws IOException {
        assertFile(file);
        assertNumOfLinesToRead(numberOfLinesToRead);
        LinkedList<String> lines = new LinkedList<String>();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), encoding));
        for (String line = null; (numberOfLinesToRead-- > 0)
                && (line = reader.readLine()) != null;) {
            if (!stripBlankLines || !line.trim().equals("")) {
                if (filter != null && filter.accept(line)) {
                    lines.addFirst(line);
                } else {
                    numberOfLinesToRead++;
                }
            } else {
                numberOfLinesToRead++;
            }
        }
        reader.close();
        return (String[]) lines.toArray(EMPTY_STRINGS);
    }

    /**
     * Returns the specified number of lines starting from the end
     * of a text file.
     * @param file the file to read lines from
     * @param numberOfLinesToRead the number of lines to read
     * @return array of file lines
     * @throws IOException
     */
    public static String[] tail(File file, int numberOfLinesToRead)
            throws IOException {
        return tail(file, CharsetUtils.ISO_8859_1, numberOfLinesToRead);
    }

    /**
     * Returns the specified number of lines starting from the end
     * of a text file.
     * @param file the file to read lines from
     * @param encoding the file encoding
     * @param numberOfLinesToRead the number of lines to read
     * @return array of file lines
     * @throws IOException
     */
    public static String[] tail(File file, String encoding,
            int numberOfLinesToRead) throws IOException {
        return tail(file, encoding, numberOfLinesToRead, true);
    }

    /**
     * Returns the specified number of lines starting from the end
     * of a text file.
     * @param file the file to read lines from
     * @param encoding the file encoding
     * @param numberOfLinesToRead the number of lines to read
     * @param stripBlankLines whether to return blank lines or not
     * @return array of file lines
     * @throws IOException
     */
    public static String[] tail(File file, String encoding,
            int numberOfLinesToRead, boolean stripBlankLines)
            throws IOException {
        return tail(file, encoding, numberOfLinesToRead, stripBlankLines, null);
    }
    
    /**
     * Returns the specified number of lines starting from the end
     * of a text file.
     * @param file the file to read lines from
     * @param encoding the file encoding
     * @param numberOfLinesToRead the number of lines to read
     * @param stripBlankLines whether to return blank lines or not
     * @param filter InputStream filter
     * @return array of file lines
     * @throws IOException
     */
    public static String[] tail(File file, String encoding,
            int numberOfLinesToRead, boolean stripBlankLines,
            IInputStreamFilter filter)
            throws IOException {
        assertFile(file);
        assertNumOfLinesToRead(numberOfLinesToRead);
        LinkedList<String> lines = new LinkedList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ReverseFileInputStream(file), encoding));
        for (String line = null; (numberOfLinesToRead-- > 0)
                && (line = reader.readLine()) != null;) {
            // Reverse the order of the characters in the string
            char[] chars = line.toCharArray();
            for (int j = 0, k = chars.length - 1; j < k; j++, k--) {
                char temp = chars[j];
                chars[j] = chars[k];
                chars[k] = temp;
            }
            String newLine = new String(chars);
            if (!stripBlankLines || !newLine.trim().equals("")) {
                if (filter != null && filter.accept(newLine)) {
                    lines.addFirst(newLine);
                } else {
                    numberOfLinesToRead++;
                }
            } else {
                numberOfLinesToRead++;
            }
        }
        reader.close();
        return (String[]) lines.toArray(EMPTY_STRINGS);
    }

    private static void assertNumOfLinesToRead(int num) throws IOException {
        if (num <= 0) {
            throw new IllegalArgumentException(
                    "Not a valid number to read: " + num);
        }
    }

    private static void assertFile(File file) throws IOException {
        if (file == null || !file.exists()
                || !file.isFile() || !file.canRead()) {
            throw new IOException("Not a valid file: " + file);
        }
    }
}
