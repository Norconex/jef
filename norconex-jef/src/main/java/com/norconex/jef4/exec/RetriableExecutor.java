/* Copyright 2010-2017 Norconex Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.norconex.jef4.exec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.norconex.commons.lang.Sleeper;
import com.norconex.commons.lang.exec.Retrier;

/**
 * <code>RetriableExecutor</code> is responsible for executing <code>IRetriable</code>
 * instances.  Upon reaching the maximum number of retries allowed, it 
 * will return the last exception encountered if there was one, or throw
 * a {@link RuntimeException} if {@link IRetriable} simply returned
 * <code>false</code>. 
 * @author Pascal Essiembre
 * @deprecated Since 4.1.0, this class is now {@link Retrier} in 
 *             Norconex Commons Lang.
 */
@Deprecated
public class RetriableExecutor {

    /** Logger. */
    private static final Logger LOG = 
            LoggerFactory.getLogger(RetriableExecutor.class);

    /** Default maximum number of retries. */
    public static final int DEFAULT_MAX_RERUN_ATTEMPTS = 10;
    /** Default wait time (milliseconds) between reruns. */
    public static final long DEFAULT_RERUN_SLEEP_TIME = 5 * 1000;
    
    /** Maximum number of re-run attempts. */
    private final int maxRerunAttempts;
    /** Time to wait between each run. */
    private final long sleepTime;
    /** Exception filter. */
    private final IExceptionFilter exceptionFilter;

    /**
     * Creates a new instance of <code>RetriableExecutor</code> using the default
     * maximum re-run attempts and default re-run wait time.
     */
    public RetriableExecutor() {
        this(DEFAULT_MAX_RERUN_ATTEMPTS);
    }
    /**
     * Creates a new instance of <code>RetriableExecutor</code> using the default
     * re-run wait time.
     * @param maxRerunAttempts maximum number of execution attempts
     */
    public RetriableExecutor(int maxRerunAttempts) {
        this(maxRerunAttempts, DEFAULT_RERUN_SLEEP_TIME);
    }
    /**
     * Creates a new instance of <code>RetriableExecutor</code>.
     * @param maxRecoveryAttempts maximum number of execution attempts
     * @param sleepTime number of milliseconds to wait between each executions
     */
    public RetriableExecutor(
            int maxRecoveryAttempts,
            long sleepTime) {
        this(null, maxRecoveryAttempts, sleepTime);
    }
    /**
     * Creates a new instance of <code>RetriableExecutor</code> which will re-run code
     * triggering exceptions only if the given exception is accepted by
     * the {@link IExceptionFilter}. Uses the default
     * maximum re-run attempts and default re-run wait time.
     * @param exceptionFilter exception filter
     */    
    public RetriableExecutor(
            IExceptionFilter exceptionFilter) {
        this(exceptionFilter, DEFAULT_MAX_RERUN_ATTEMPTS);
    }
    /**
     * Creates a new instance of <code>RetriableExecutor</code> which will re-run code
     * triggering exceptions only if the given exception is accepted by
     * the {@link IExceptionFilter}. Uses the default re-run wait time.
     * @param exceptionFilter exception filter
     * @param maxRerunAttempts maximum number of execution attempts
     */
    public RetriableExecutor(
            IExceptionFilter exceptionFilter,
            int maxRerunAttempts) {
        this(exceptionFilter, maxRerunAttempts, DEFAULT_RERUN_SLEEP_TIME);
    }
    /**
    /**
     * Creates a new instance of <code>RetriableExecutor</code> which will re-run code
     * triggering exceptions only if the given exception is accepted by
     * the {@link IExceptionFilter}.
     * @param exceptionFilter exception filter
     * @param maxRerunAttempts maximum number of execution attempts
     * @param sleepTime number of milliseconds to wait between each executions
     */
    public RetriableExecutor(
            IExceptionFilter exceptionFilter,
            int maxRerunAttempts,
            long sleepTime) {
        super();
        this.maxRerunAttempts = maxRerunAttempts;
        this.sleepTime = sleepTime;
        this.exceptionFilter = exceptionFilter;
    }

    /**
     * Runs the {@link IRetriable} instance.
     * @param retriable the code to run
     * @throws RetriableException wrapper around last exception encountered
     * or exception thrown when max rerun attempts is reached.
     */
    public void execute(IRetriable retriable) throws RetriableException {
        int attemptCount = 0;
        Exception exception = null;
        while (attemptCount < maxRerunAttempts) {
            try {
                retriable.run();
                // no exception, simply return
                return;
            } catch (Exception e) {
                if (exceptionFilter == null || exceptionFilter.accept(e)) {
                    exception = e;
                } else {
                    throw new RetriableException(
                            "Unrecoverable exception encountered.", e);
                }
            }
            attemptCount++;
            if (attemptCount < maxRerunAttempts) {
                LOG.warn("Execution failed, attempting to run again ("
                        + attemptCount + " of " + maxRerunAttempts
                        + ").", exception);
                Sleeper.sleepMillis(sleepTime);
            }
        }
        throw new RetriableException(
                "Execution failed, maximum number of recovery "
              + "attempts reached. Aborting.", exception);
    }
}
