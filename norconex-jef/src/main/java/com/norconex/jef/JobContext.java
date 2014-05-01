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
package com.norconex.jef;

/**
 * Basic POJO implementation of {@link IJobContext}.
 * @author Pascal Essiembre
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
