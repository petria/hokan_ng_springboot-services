package org.freakz.hokan_ng_springboot.bot.services.service;

import org.freakz.hokan_ng_springboot.bot.common.events.IrcMessageEvent;
import org.freakz.hokan_ng_springboot.bot.common.jms.api.JmsSender;
import org.freakz.hokan_ng_springboot.bot.common.jpa.entity.Network;
import org.freakz.hokan_ng_springboot.bot.common.jpa.repository.UrlRepository;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.ChannelPropertyRepositoryService;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.ChannelService;
import org.freakz.hokan_ng_springboot.bot.common.jpa.service.NetworkService;
import org.freakz.hokan_ng_springboot.bot.services.service.urls.UrlCatchServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UrlCatcherTest {

    @Mock
    ChannelPropertyRepositoryService channelProperty;

    @Mock
    ChannelService channelService;

    @Mock
    JmsSender jmsSender;

    @Mock
    NetworkService networkService;

    @Mock
    UrlRepository urlRepo;

    @Test
    public void testCatchUrls() {
        UrlCatchServiceImpl urlCatchService = new UrlCatchServiceImpl(channelProperty, channelService, jmsSender, networkService, urlRepo);


        IrcMessageEvent ircMessageEvent = new IrcMessageEvent();
        ircMessageEvent.setMessage("findddfuurrlll https://www.youtube.com/watch?v=648UkmmTG5w");

        Network network = new Network();

        when(networkService.getNetwork(anyString())).thenReturn(network);

        urlCatchService.catchUrls(ircMessageEvent);

    }

}
