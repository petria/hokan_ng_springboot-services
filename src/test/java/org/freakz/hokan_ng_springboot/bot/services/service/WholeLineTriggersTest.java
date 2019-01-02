package org.freakz.hokan_ng_springboot.bot.services.service;

import org.freakz.hokan_ng_springboot.bot.services.service.wholelinetricker.WholeLineTriggersImpl;
import org.junit.Test;

import java.time.LocalDateTime;

public class WholeLineTriggersTest {


    @Test
    public void testJouluTime() {

        WholeLineTriggersImpl wholeLineTriggers = new WholeLineTriggersImpl(null);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.of(now.getYear(), 12, 25, 12, 0);

        LocalDateTime jouluTime = wholeLineTriggers.getJouluTime(start);
        System.out.printf("%s\n", jouluTime.toString());


    }


}
