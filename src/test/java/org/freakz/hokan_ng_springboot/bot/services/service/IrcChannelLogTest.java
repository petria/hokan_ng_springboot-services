package org.freakz.hokan_ng_springboot.bot.services.service;

import org.freakz.hokan_ng_springboot.bot.services.service.irclog.IrcLogService;
import org.freakz.hokan_ng_springboot.bot.services.service.irclog.IrcLogServiceImpl;
import org.junit.Test;


public class IrcChannelLogTest {

    @Test
    public void testIrcChannelLog() {
        IrcLogService ircLogService = new IrcLogServiceImpl();
        ircLogService.logChannelMessage("network", "#foobar", "f fdfjdsk fjdsk jdsksd jkdsfjkfjdskffjksdkf");
    }

}
