/**
 * 
 */
package com.norconex.jef.jobs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.norconex.jef.IJob;
import com.norconex.jef.IJobContext;
import com.norconex.jef.JobContext;
import com.norconex.jef.JobException;
import com.norconex.jef.progress.IJobStatus;
import com.norconex.jef.progress.JobProgress;
import com.norconex.jef.suite.JobSuiteOLD;

/**
 * Job for listing the files in a directory, including subdirectories.
 * @author Pascal Essiembre (pascal.essiembre&#x40;norconex.com)
 */
public class ListFilesJob implements IJob {

    private final String id;
    private final File dir;
    private final boolean recursive;
    private String indent = "";
    
    /**
     * Constructor.
     */
    public ListFilesJob(String id, String dir, boolean recursive) {
        super();
        this.id = id;
        File directory = new File(dir);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException(
                    "Must specify a valid directory.");
        }
        this.dir = directory;
        this.recursive = recursive;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getDescription() {
        return "List files in " + dir + " (recursive=" + recursive + ").";
    }

    public long getProgressMinimum() {
        return 0;
    }

    public long getProgressMaximum() {
        return dir.list().length;
    }

    @Override
    public void execute(JobProgress progress, JobSuiteOLD context)
            throws JobException {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(
            		new FileWriter(System.getProperty("java.io.tmpdir")
            		        + id + ".txt")));
                    //new FileWriter("C:\\temp\\" + id + ".txt")));
            listFiles(dir, progress, out);
            out.close();
            progress.setMetadata("");
            progress.setNote("Done listing files.");
        } catch (FileNotFoundException e) {
            throw new JobException(e);
        } catch (IOException e) {
            throw new JobException(e);
        }
    }
    
    private void listFiles(
            File directory, JobProgress progress, PrintWriter out) {
        progress.setNote("Listing files in " + directory);
        progress.setMetadata("dir=" + directory);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (indent.equals("")) {
                progress.setProgress(progress.getProgress() + 1);
            }
            out.println(
                    indent + file.getPath() + " (" + file.lastModified() + ")");
            if (recursive && file.isDirectory()) {
                indent += "  ";
                listFiles(file, progress, out);
                indent = indent.substring(2);
            }
        }
    }

	@Override
	public IJobContext createJobContext() {
		return new JobContext(
		        getDescription(), getProgressMinimum(), getProgressMaximum());
	}

	@Override
	public void stop(IJobStatus progress, JobSuiteOLD suite) {
		// Unstoppable
	}

    

}
