package org.freakz.hokan_ng_springboot.bot.services.service;

import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.services.service.irclog.IrcChannelLogRequestHandler;
import org.freakz.hokan_ng_springboot.bot.services.service.irclog.IrcLogService;
import org.freakz.hokan_ng_springboot.bot.services.service.irclog.IrcLogServiceImpl;
import org.junit.Test;

import static org.freakz.hokan_ng_springboot.bot.services.service.ServiceRequestUtils.createServiceRequest;


public class IrcChannelLogTest {

    @Test
    public void testIrcChannelLog() {
        IrcLogService ircLogService = new IrcLogServiceImpl();
        IrcChannelLogRequestHandler requestHandler = new IrcChannelLogRequestHandler(ircLogService);
        ServiceRequest request = createServiceRequest(ServiceRequestType.IRC_CHANNEL_LOG_REQUEST, "#channel", "SomeNick", "messgage fkdlfkd flkd");
        ServiceResponse response = new ServiceResponse(ServiceRequestType.IRC_CHANNEL_LOG_REQUEST);
        requestHandler.handleIrcChannelLogRequest(request, response);

    }

}
