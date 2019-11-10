package org.freakz.hokan_ng_springboot.bot.services.service;

import org.freakz.hokan_ng_springboot.bot.common.events.IrcMessageEvent;
import org.freakz.hokan_ng_springboot.bot.services.service.urls.UrlCatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.util.Scanner;

/**
 * Created by Petri Airio on 8.3.2017.
 */
//@Service
public class CommandReader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CommandReader.class);

    private static Scanner sc = new Scanner(System.in);

    private boolean echoInput = true;


    public void work() {
        log.debug("<< Ready for input! >>");
        while (true) {
            try {
                String line = sc.nextLine();
                if (echoInput) {
                    System.out.println(line);
                }
                parseLine(line);
            } catch (Exception e) {
                log.error("ERROR", e);
            }
        }
    }

    @Autowired
    private UrlCatchService urlCatchService;

    public void parseLine(String line) {
        if (line.startsWith("!url ")) {
            IrcMessageEvent iEvent = new IrcMessageEvent();
            iEvent.setMessage(line);
            urlCatchService.catchUrls(iEvent);
        }
    }


    @Override
    public void run(String... args) throws Exception {
        Thread t = new Thread(this::work);
        t.setName("CommandReader");
        t.start();

    }
}
