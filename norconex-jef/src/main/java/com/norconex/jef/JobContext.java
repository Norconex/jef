package com.norconex.jef;

/**
 * Basic POJO implementation of {@link IJobContext}.
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 * @since 2.0
 */
public class JobContext implements IJobContext {

    private static final long serialVersionUID = 2308884402167244307L;

    private String description;
    private long progressMinimum;
    private long progressMaximum;

    /**
     * Constructor.
     */
    public JobContext() {
        super();
    }
    /**
     * Constructor.
     * @param description the job description
     * @param progressMinimum the minimum possible progress value
     * @param progressMaximum the maximum possible progress value
     */
    public JobContext(
            String description, long progressMinimum, long progressMaximum) {
        super();
        this.description = description;
        this.progressMinimum = progressMinimum;
        this.progressMaximum = progressMaximum;
    }
    /**
     * Constructor. Initialise this job context with values from another job
     * context.
     * @param context
     */
    public JobContext(IJobContext context) {
        super();
        this.description = context.getDescription();
        this.progressMinimum = context.getProgressMinimum();
        this.progressMaximum = context.getProgressMaximum();
    }

    @Override
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    @Override
    public long getProgressMinimum() {
        return progressMinimum;
    }
    public void setProgressMinimum(long progressMinimum) {
        this.progressMinimum = progressMinimum;
    }
    @Override
    public long getProgressMaximum() {
        return progressMaximum;
    }
    public void setProgressMaximum(long progressMaximum) {
        this.progressMaximum = progressMaximum;
    }
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("JobContext [description=").append(description)
                .append(", progressMinimum=").append(progressMinimum)
                .append(", progressMaximum=").append(progressMaximum)
                .append("]");
        return builder.toString();
    }
}
