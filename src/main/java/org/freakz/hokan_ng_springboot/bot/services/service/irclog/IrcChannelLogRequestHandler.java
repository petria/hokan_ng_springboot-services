package org.freakz.hokan_ng_springboot.bot.services.service.irclog;

import org.freakz.hokan_ng_springboot.bot.common.events.IrcMessageEvent;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.services.service.annotation.ServiceMessageHandler;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IrcChannelLogRequestHandler {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(IrcLogServiceImpl.class);

    private final IrcLogService ircLogService;

    @Autowired
    public IrcChannelLogRequestHandler(IrcLogService ircLogService) {
        this.ircLogService = ircLogService;
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.IRC_CHANNEL_LOG_REQUEST)
    public void handleIrcChannelLogRequest(ServiceRequest request, ServiceResponse response) {
        log.debug("Handle log request");
        IrcMessageEvent event = request.getIrcMessageEvent();
        ircLogService.logChannelMessage(event.getNetwork(), event.getChannel(), event.getMessage());
    }

}
