package org.freakz.hokan_ng_springboot.bot.services.service;

import org.freakz.hokan_ng_springboot.bot.services.service.irclog.CountResult;
import org.freakz.hokan_ng_springboot.bot.services.service.irclog.IrcLogReader;
import org.freakz.hokan_ng_springboot.bot.services.service.irclog.IrcLogReaderImpl;
import org.junit.Test;

import java.time.LocalDateTime;

public class IrcLogReaderTest {

    @Test
    public void testCount() {
        IrcLogReader reader = new IrcLogReaderImpl();
        LocalDateTime time = LocalDateTime.of(2018, 12, 23, 0, 0);
        CountResult countResult = reader.countWordsByPerson("ircnet", "#lowlife", time);
    }

}
