package org.freakz.hokan_ng_springboot.bot.services.service.irclog;

public interface IrcLogService {

    void logChannelMessage(String network, String channel, String message);

}
