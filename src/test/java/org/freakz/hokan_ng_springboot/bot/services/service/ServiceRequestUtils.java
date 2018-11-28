package org.freakz.hokan_ng_springboot.bot.services.service;

import org.freakz.hokan_ng_springboot.bot.common.events.IrcMessageEvent;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.util.CommandArgs;

public class ServiceRequestUtils {


    public static ServiceRequest createServiceRequest(ServiceRequestType type, String channel, String sender, String ircMessage) {
        IrcMessageEvent ircEvent = createIrcMessageEvent(channel, sender, ircMessage);
        CommandArgs args = new CommandArgs(ircMessage);
        ServiceRequest serviceRequest = new ServiceRequest(type, ircEvent, args);
        return serviceRequest;
    }

    private static IrcMessageEvent createIrcMessageEvent(String channel, String sender, String ircMessage) {
        IrcMessageEvent ircMessageEvent = new IrcMessageEvent();
        ircMessageEvent.setNetwork("DevNetWork");
        ircMessageEvent.setChannel(channel);
        ircMessageEvent.setSender(sender);
        ircMessageEvent.setMessage(ircMessage);
        return ircMessageEvent;
    }


}
