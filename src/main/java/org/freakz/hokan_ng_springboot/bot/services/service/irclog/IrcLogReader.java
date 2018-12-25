package org.freakz.hokan_ng_springboot.bot.services.service.irclog;

import java.time.LocalDateTime;

public interface IrcLogReader {

    CountResult countWordsByPerson(String network, String channel, LocalDateTime day);

}

