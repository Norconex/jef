package com.norconex.jef5.log.impl;

import java.io.IOException;
import java.io.Reader;
import java.util.Date;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.norconex.commons.lang.file.FileUtil;
import com.norconex.jef4.log.FileAppender;
import com.norconex.jef4.log.PatternLayout;
import com.norconex.jef4.log.ThreadSafeLayout;
import com.norconex.jef4.suite.Appender;
import com.norconex.jef5.log.ILogManager;

import ch.qos.logback.classic.LoggerContext;

//TODO consider abstract class doing most file base stuff.
public class LogbackManager implements ILogManager {


//return is whether init was successful... else, pass to another imlementation?
//add Properties as argument in addition to init string? (like JDBC drivers?)
    @Override
    public void init(String initString) {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        //log4j:
//        return new FileAppender(new PatternLayout(LAYOUT_PATTERN),
//                logdirLatest + "/" + 
//                        FileUtil.toSafeFileName(suiteId) + LOG_SUFFIX);
//        Appender appender = getLogManager().createAppender(getId());
//        appender.setLayout(new ThreadSafeLayout(appender.getLayout()));
//        
//        Logger.getRootLogger().addAppender(appender);          
        
        //logback:
//        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
//        PatternLayoutEncoder ple = new PatternLayoutEncoder();
//
//        ple.setPattern("%date %level [%thread] %logger{10} [%file:%line] %msg%n");
//        ple.setContext(lc);
//        ple.start();
//        FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
//        fileAppender.setFile(file);
//        fileAppender.setEncoder(ple);
//        fileAppender.setContext(lc);
//        fileAppender.start();
//
//        Logger logger = (Logger) LoggerFactory.getLogger(string);
//        logger.addAppender(fileAppender);
//        logger.setLevel(Level.DEBUG);
//        logger.setAdditive(false); /* set to true if root should log too */
        
        
        // TODO Auto-generated method stub
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Reader getReader(String suiteId, String jobId) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> tail(String suiteId, String jobId, int qty)
            throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> head(String suiteId, String jobId, int qty)
            throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void backup(String namespace, Date backupDate, int qtyToKeep)
            throws IOException {
        // TODO Auto-generated method stub
        
    }

    
    
}
