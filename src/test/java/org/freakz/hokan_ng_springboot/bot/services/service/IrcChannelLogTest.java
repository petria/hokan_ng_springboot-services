package org.freakz.hokan_ng_springboot.bot.services.service;

import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.services.service.irclog.IrcChannelLogRequestHandler;
import org.freakz.hokan_ng_springboot.bot.services.service.irclog.IrcLogService;
import org.freakz.hokan_ng_springboot.bot.services.service.irclog.IrcLogServiceImpl;
import org.freakz.hokan_ng_springboot.bot.services.service.irclog.LocalDateTimeProvider;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.freakz.hokan_ng_springboot.bot.services.service.ServiceRequestUtils.createServiceRequest;


public class IrcChannelLogTest {

    @Test
    public void testIrcChannelLog() {
        IrcLogService ircLogService = new IrcLogServiceImpl();

/*        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(LocalDate.)
        */
        List<LocalDateTime> list = new ArrayList<>();
        list.add(LocalDateTime.of(2018, 11, 8, 23, 59));
        list.add(LocalDateTime.of(2018, 11, 8, 23, 59));
        list.add(LocalDateTime.of(2018, 11, 9, 0, 0));
        list.add(LocalDateTime.of(2018, 11, 9, 0, 1));
        list.add(LocalDateTime.of(2018, 11, 9, 0, 2));

        Iterator<LocalDateTime> iterator = list.iterator();

        LocalDateTimeProvider provider = new LocalDateTimeProvider() {
            private Iterator<LocalDateTime> iterator;

            @Override
            public LocalDateTime getLocalDateTime() {
                return iterator.next();
            }

            public void setIterator(Iterator<LocalDateTime> iterator) {
                this.iterator = iterator;
            }
        };
        provider.setIterator(iterator);

        IrcChannelLogRequestHandler requestHandler = new IrcChannelLogRequestHandler(ircLogService);
        requestHandler.setLocalDateTimeProvider(provider);

        ServiceResponse response = new ServiceResponse(ServiceRequestType.IRC_CHANNEL_LOG_REQUEST);

        ServiceRequest request = createServiceRequest(ServiceRequestType.IRC_CHANNEL_LOG_REQUEST, "#channel", "SomeNick", "messgage fkdlfkd flkd");
        requestHandler.handleIrcChannelLogRequest(request, response);

        request = createServiceRequest(ServiceRequestType.IRC_CHANNEL_LOG_REQUEST, "#channel", "SomeNick", "ffufuf messgage fkdlfkd flkd2");
        requestHandler.handleIrcChannelLogRequest(request, response);

        request = createServiceRequest(ServiceRequestType.IRC_CHANNEL_LOG_REQUEST, "#channel", "SomeNick", "messgage fkdlfkd flkd2");
        requestHandler.handleIrcChannelLogRequest(request, response);

        request = createServiceRequest(ServiceRequestType.IRC_CHANNEL_LOG_REQUEST, "#channel2", "SomeNick2", "messgage fkdlfkd flkd3");
        requestHandler.handleIrcChannelLogRequest(request, response);

        request = createServiceRequest(ServiceRequestType.IRC_CHANNEL_LOG_REQUEST, "#channel3", "SomeNick3", "messgage fkdlfkd flkd4");
        requestHandler.handleIrcChannelLogRequest(request, response);
    }

}
