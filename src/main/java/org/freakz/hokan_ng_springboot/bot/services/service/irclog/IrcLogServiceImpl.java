package org.freakz.hokan_ng_springboot.bot.services.service.irclog;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class IrcLogServiceImpl implements IrcLogService {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(IrcLogServiceImpl.class);

    private Map<String, Logger> channelLoggers = new HashMap<>();


    @Override
    public void logChannelMessage(String network, String channel, String message) {
        String key = String.format("%s%s", network, channel);
        Logger logger = channelLoggers.get(key);
        if (logger == null) {
            String path = createPath(network, channel);
            logger = createLoggerFor(key, path);
        }
        logger.info(message);
    }

    private String createPath(String network, String channel) {
        String path = "logs/" + network + "/" + channel + "/";
        File file = new File(path);
        if (!file.exists()) {
            boolean ok = file.mkdirs();
            log.debug("Created log dir: {} -- {}", file.getAbsolutePath(), ok);
        }

        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        String today = formatter.format(localDate);

        return path + today + ".log";
    }


    private Logger createLoggerFor(String string, String file) {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        PatternLayoutEncoder ple = new PatternLayoutEncoder();

        ple.setPattern("%msg%n");
        ple.setContext(lc);
        ple.start();
        FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
        fileAppender.setFile(file);
        fileAppender.setEncoder(ple);
        fileAppender.setContext(lc);
        fileAppender.start();

        Logger logger = (Logger) LoggerFactory.getLogger(string);
        logger.addAppender(fileAppender);
        logger.setLevel(Level.DEBUG);
        logger.setAdditive(true); /* set to true if root should log too */

        return logger;
    }

}
